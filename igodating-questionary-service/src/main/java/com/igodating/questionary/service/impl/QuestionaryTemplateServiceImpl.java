package com.igodating.questionary.service.impl;

import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.repository.MatchingRuleRepository;
import com.igodating.questionary.repository.QuestionBlockRepository;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.repository.QuestionaryTemplateRepository;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.QuestionaryTemplateService;
import com.igodating.questionary.service.validation.QuestionBlockValidationService;
import com.igodating.questionary.service.validation.QuestionaryTemplateValidationService;
import com.igodating.questionary.util.EntitiesListChange;
import com.igodating.questionary.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionaryTemplateServiceImpl implements QuestionaryTemplateService {

    private final QuestionaryTemplateValidationService questionaryTemplateValidationService;

    private final QuestionBlockValidationService questionBlockValidationService;

    private final QuestionaryTemplateRepository questionaryTemplateRepository;

    private final QuestionRepository questionRepository;

    private final MatchingRuleRepository matchingRuleRepository;

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final QuestionBlockRepository questionBlockRepository;

    @Override
    @Transactional(readOnly = true)
    public QuestionaryTemplate getById(Long id) {
        return questionaryTemplateRepository.findById(id).orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionaryTemplate> getAll() {
        return questionaryTemplateRepository.findAll();
    }

    @Override
    @Transactional
    public void create(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnCreate(questionaryTemplate);

        questionaryTemplateRepository.save(questionaryTemplate);

        createQuestionBlocks(questionaryTemplate);

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

        createQuestionBlocks(questionaryTemplate);

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
    public void updateQuestionBlock(QuestionBlock questionBlock) {
        questionBlockValidationService.validateOnUpdate(questionBlock);

        QuestionBlock existedQuestionBlock = questionBlockRepository.getReferenceById(questionBlock.getId());

        existedQuestionBlock.setName(questionBlock.getName());

        questionBlockRepository.save(existedQuestionBlock);
    }

    @Override
    @Transactional
    public void delete(QuestionaryTemplate questionaryTemplate) {
        questionaryTemplateValidationService.validateOnDelete(questionaryTemplate);
        questionaryTemplate.setToDelete();
        questionaryTemplateRepository.save(questionaryTemplate);
        questionRepository.deleteAllByQuestionaryTemplateId(questionaryTemplate.getId());
        questionBlockRepository.deleteAllByQuestionaryTemplateId(questionaryTemplate.getId());
        userQuestionaryRepository.setDeletedForAllByQuestionaryTemplateId(questionaryTemplate.getId());
    }

    private void createQuestionBlocks(QuestionaryTemplate questionaryTemplate) {
        Map<String, List<Question>> questionBlocksNamesIdsWithQuestionsMap = new HashMap<>();

        questionaryTemplate.getQuestions().forEach(q -> {
            QuestionBlock questionBlock = q.getQuestionBlock();
            if (questionBlock != null && questionBlock.getId() == null) {
                List<Question> questionsForBlock = questionBlocksNamesIdsWithQuestionsMap.computeIfAbsent(questionBlock.getName(), k -> new ArrayList<>());
                questionsForBlock.add(q);
            }
        });

        questionBlocksNamesIdsWithQuestionsMap.forEach((key, value) -> {
            QuestionBlock questionBlock = new QuestionBlock();
            questionBlock.setQuestionaryTemplateId(questionaryTemplate.getId());
            questionBlock.setName(key);

            questionBlockRepository.save(questionBlock);

            value.forEach(question -> {
                question.setQuestionBlockId(questionBlock.getId());
                question.setQuestionBlock(questionBlock);
            });
        });

    }

    private void updateQuestion(Question oldQuestion, Question newQuestion) {
        oldQuestion.setTitle(newQuestion.getTitle());
        oldQuestion.setDescription(newQuestion.getDescription());

        MatchingRule oldMatchingRule = oldQuestion.getMatchingRule();
        MatchingRule newMatchingRule = newQuestion.getMatchingRule();
        if (changesInRules(oldMatchingRule, newMatchingRule)) {
            updateMatchingRule(oldMatchingRule, newMatchingRule, oldQuestion);
        }

        if (oldQuestion.withChoice()) {
            String[] newAnswerOptions = newQuestion.getAnswerOptions();

            if (oldQuestion.getAnswerOptions().length > newAnswerOptions.length) {
                throw new RuntimeException("Answer option delete is not supported");
            }

            oldQuestion.setAnswerOptions(newAnswerOptions);
        }

        questionRepository.save(oldQuestion);
    }

    private void updateMatchingRule(MatchingRule oldMatchingRule, MatchingRule newMatchingRule, Question oldQuestion) {
        if (newMatchingRule == null) {
            matchingRuleRepository.delete(oldMatchingRule);
        } else if (oldMatchingRule == null) {
            newMatchingRule.setQuestionId(oldQuestion.getId());
            matchingRuleRepository.save(newMatchingRule);
        } else {
            oldMatchingRule.setAccessType(newMatchingRule.getAccessType());
            oldMatchingRule.setDefaultValues(newMatchingRule.getDefaultValues());
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
        if (!Objects.equals(oldQuestion.getQuestionBlockId(), newQuestion.getQuestionBlockId())) {
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
        if (!Objects.equals(oldMatchingRule.getDefaultValues(), newMatchingRule.getDefaultValues())) {
            return false;
        }

        return Objects.equals(oldMatchingRule.getAccessType(), newMatchingRule.getAccessType());
    }

    private void createQuestion(Question question, Long questionaryTemplateId) {
        question.setQuestionaryTemplateId(questionaryTemplateId);
        questionRepository.save(question);

        MatchingRule matchingRule = question.getMatchingRule();
        if (matchingRule != null) {
            matchingRule.setQuestionId(question.getId());
            matchingRuleRepository.save(matchingRule);
        }
    }
}
