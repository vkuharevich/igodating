package com.igodating.questionary.service.impl;

import com.igodating.questionary.model.AnswerOption;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.repository.AnswerOptionRepository;
import com.igodating.questionary.repository.MatchingRuleRepository;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.repository.QuestionaryTemplateRepository;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.QuestionaryTemplateService;
import com.igodating.questionary.service.validation.QuestionaryTemplateValidationService;
import com.igodating.questionary.util.EntitiesListChange;
import com.igodating.questionary.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionaryTemplateServiceImpl implements QuestionaryTemplateService {

    private final QuestionaryTemplateValidationService questionaryTemplateValidationService;

    private final QuestionaryTemplateRepository questionaryTemplateRepository;

    private final QuestionRepository questionRepository;

    private final MatchingRuleRepository matchingRuleRepository;

    private final AnswerOptionRepository answerOptionRepository;

    private final UserQuestionaryRepository userQuestionaryRepository;

    @Override
    @Transactional(readOnly = true)
    public QuestionaryTemplate getById(Long id) {
        QuestionaryTemplate questionaryTemplate = questionaryTemplateRepository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found"));

        loadAnswersForQuestionsWithChoice(questionaryTemplate);

        return questionaryTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionaryTemplate> getAll() {
        List<QuestionaryTemplate> questionaryTemplates = questionaryTemplateRepository.findAll();

        for (QuestionaryTemplate questionaryTemplate : questionaryTemplates) {
            loadAnswersForQuestionsWithChoice(questionaryTemplate);
        }

        return questionaryTemplates;
    }

    @Override
    @Transactional
    public void create(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnCreate(questionaryTemplate);

        questionaryTemplateRepository.save(questionaryTemplate);

        for (Question question : questionaryTemplate.getQuestions()) {
            createQuestion(question, questionaryTemplate.getId());
        }
    }

    @Override
    @Transactional
    public void update(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnUpdate(questionaryTemplate);

        QuestionaryTemplate existedQuestionaryTemplate = questionaryTemplateRepository.getReferenceById(questionaryTemplate.getId());

        existedQuestionaryTemplate.setName(questionaryTemplate.getName());
        existedQuestionaryTemplate.setDescription(questionaryTemplate.getDescription());

        EntitiesListChange<Question, Long> changes = ServiceUtils.changes(existedQuestionaryTemplate.getQuestions(), questionaryTemplate.getQuestions(), this::changesInQuestions);

        List<Question> questionsToCreate = changes.getToCreate();
        for (Question questionToCreate : questionsToCreate) {
            if (Boolean.TRUE.equals(questionToCreate.getIsMandatory())) {
                userQuestionaryRepository.setNeedsChangesForAllByQuestionaryTemplateId(questionaryTemplate.getId());

                createQuestion(questionToCreate, questionaryTemplate.getId());
            }
        }

        List<Question> questionsToDelete = changes.getToDelete();
        for (Question questionToDelete : questionsToDelete) {
            questionRepository.delete(questionToDelete);
        }

        List<Pair<Question, Question>> questionsToUpdatePairs = changes.getOldNewPairToUpdate();
        for (Pair<Question, Question> oldNewPair : questionsToUpdatePairs) {
            Question oldQuestion = oldNewPair.getFirst();
            Question newQuestion = oldNewPair.getSecond();

            updateQuestion(oldQuestion, newQuestion);
        }
    }

    @Override
    @Transactional
    public void delete(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnDelete(questionaryTemplate);
        questionaryTemplate.setToDelete();
        questionaryTemplateRepository.save(questionaryTemplate);
        questionRepository.deleteAllByQuestionaryTemplateId(questionaryTemplate.getId());
        userQuestionaryRepository.setDeletedForAllByQuestionaryTemplateId(questionaryTemplate.getId());
    }

    private void updateQuestion(Question oldQuestion, Question newQuestion) {
        oldQuestion.setTitle(newQuestion.getTitle());
        oldQuestion.setDescription(newQuestion.getDescription());

        MatchingRule oldMatchingRule = oldQuestion.getMatchingRule();
        MatchingRule newMatchingRule = newQuestion.getMatchingRule();
        if (changesInRules(oldMatchingRule, newMatchingRule)) {
            updateMatchingRule(oldMatchingRule, newMatchingRule, oldQuestion);
        }

        List<AnswerOption> oldAnswerOptions = oldQuestion.getAnswerOptions();
        List<AnswerOption> newAnswerOptions = newQuestion.getAnswerOptions();
        if (!CollectionUtils.isEmpty(oldAnswerOptions) || !CollectionUtils.isEmpty(newAnswerOptions)) {
            mergeAnswerOptions(oldAnswerOptions, newAnswerOptions);
        }
    }

    private void mergeAnswerOptions(List<AnswerOption> oldAnswerOptions, List<AnswerOption> newAnswerOptions) {
        List<Pair<AnswerOption, AnswerOption>> oldNewAnswerOptionsPairs = ServiceUtils.changes(oldAnswerOptions, newAnswerOptions, (oldAnswerOption, newAnswerOption) -> Objects.equals(oldAnswerOption.getValue(), newAnswerOption.getValue())).getOldNewPairToUpdate();
        for (Pair<AnswerOption, AnswerOption> oldNewAnswerOptionsPair : oldNewAnswerOptionsPairs) {
            AnswerOption oldAnswerOption = oldNewAnswerOptionsPair.getFirst();
            AnswerOption newAnswerOption = oldNewAnswerOptionsPair.getSecond();

            oldAnswerOption.setValue(newAnswerOption.getValue());

            answerOptionRepository.save(oldAnswerOption);
        }
    }

    private void updateMatchingRule(MatchingRule oldMatchingRule, MatchingRule newMatchingRule, Question oldQuestion) {
        if (newMatchingRule == null) {
            matchingRuleRepository.delete(oldMatchingRule);
        } else if (oldMatchingRule == null) {
            newMatchingRule.setQuestionId(oldQuestion.getId());
            matchingRuleRepository.save(newMatchingRule);
        } else {
            oldMatchingRule.setAccessType(newMatchingRule.getAccessType());
            oldMatchingRule.setPresetValue(newMatchingRule.getPresetValue());
            oldMatchingRule.setMatchingType(newMatchingRule.getMatchingType());

            matchingRuleRepository.save(oldMatchingRule);
        }
    }

    private boolean changesInQuestions(Question oldQuestion, Question newQuestion) {
        if (!Objects.equals(oldQuestion.getTitle(), newQuestion.getTitle())) {
            return false;
        }
        if (!Objects.equals(oldQuestion.getDescription(), newQuestion.getDescription())) {
            return false;
        }

        MatchingRule oldMatchingRule = oldQuestion.getMatchingRule();
        MatchingRule newMatchingRule = newQuestion.getMatchingRule();

        return changesInRules(oldMatchingRule, newMatchingRule);
    }

    private boolean changesInRules(MatchingRule oldMatchingRule, MatchingRule newMatchingRule) {
        if (!Objects.equals(oldMatchingRule, newMatchingRule)) {
            return false;
        }
        if (!Objects.equals(oldMatchingRule.getMatchingType(), newMatchingRule.getMatchingType())) {
            return false;
        }
        if (!Objects.equals(oldMatchingRule.getPresetValue(), newMatchingRule.getPresetValue())) {
            return false;
        }

        return Objects.equals(oldMatchingRule.getAccessType(), newMatchingRule.getAccessType());
    }

    private void createQuestion(Question question, Long questionaryTemplateId) {
        question.setQuestionaryTemplateId(questionaryTemplateId);
        questionRepository.save(question);

        if (!CollectionUtils.isEmpty(question.getAnswerOptions())) {
            question.getAnswerOptions().forEach(answerOption -> {
                answerOption.setQuestionId(question.getId());
                answerOptionRepository.save(answerOption);
            });
        }

        MatchingRule matchingRule = question.getMatchingRule();
        if (matchingRule != null) {
            matchingRule.setQuestionId(question.getId());
            matchingRuleRepository.save(matchingRule);
        }
    }

    private void loadAnswersForQuestionsWithChoice(QuestionaryTemplate questionaryTemplate) {
        for (Question question : questionaryTemplate.getQuestions()) {
            if (question.withChoice()) {
                question.setAnswerOptions(answerOptionRepository.findAllByQuestionId(question.getId()));
            }
        }
    }
}
