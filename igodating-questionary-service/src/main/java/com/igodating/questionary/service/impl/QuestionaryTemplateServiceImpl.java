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
import com.igodating.commons.utils.EntitiesListChange;
import com.igodating.commons.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
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
    public <T> T getById(Long id, Function<QuestionaryTemplate, T> mappingFunc) {
        log.info("getById for questionary template {}", id);
        return questionaryTemplateRepository.findById(id).map(mappingFunc).orElseThrow(() -> new RuntimeException("Entity not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAllQuestionsFromBlock(Long questionBlockId, Function<Question, T> mappingFunc) {
        log.info("getAllQuestionsFromBlock for questionary template {}", questionBlockId);
        return questionRepository.findAllByQuestionBlockId(questionBlockId).stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAllQuestionsWithoutBlock(Long questionTemplateId, Function<Question, T> mappingFunc) {
        log.info("getAllQuestionsWithoutBlock for questionary template {}", questionTemplateId);
        return questionRepository.findAllByQuestionaryTemplateIdAndQuestionBlockIdIsNull(questionTemplateId).stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAllQuestionBlocksByTemplateId(Long templateId, Function<QuestionBlock, T> mappingFunc) {
        log.info("getAllQuestionBlocksByTemplateId for questionary template {}", templateId);
        return questionBlockRepository.findAllByQuestionaryTemplateId(templateId).stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAll(Function<QuestionaryTemplate, T> mappingFunc) {
        log.info("getAll for questionary template");
        return questionaryTemplateRepository.findAll().stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional
    public <T> Long create(T questionaryTemplateCreateRequest, Function<T, QuestionaryTemplate> mappingFunc) {
        log.info("create for questionary template {}", questionaryTemplateCreateRequest);
        QuestionaryTemplate questionaryTemplate = Optional.of(questionaryTemplateCreateRequest).map(mappingFunc).orElse(null);
        questionaryTemplateValidationService.validateOnCreate(questionaryTemplate);

        questionaryTemplateRepository.save(questionaryTemplate);

        for (Question question : questionaryTemplate.getQuestions()) {
            createQuestion(question, questionaryTemplate.getId());
        }

        return questionaryTemplate.getId();
    }

    @Override
    @Transactional
    public <T> Long update(T questionaryTemplateUpdateRequest, Function<T, QuestionaryTemplate> mappingFunc) {
        log.info("update for questionary template {}", questionaryTemplateUpdateRequest);
        QuestionaryTemplate questionaryTemplate = Optional.of(questionaryTemplateUpdateRequest).map(mappingFunc).orElse(null);
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

        return questionaryTemplate.getId();
    }

    @Override
    @Transactional
    public <T> Long createQuestionBlock(T questionBlockCreateRequest, Function<T, QuestionBlock> mappingFunc) {
        log.info("createQuestionBlock for questionary template {}", questionBlockCreateRequest);
        QuestionBlock questionBlock = Optional.of(questionBlockCreateRequest).map(mappingFunc).orElse(null);
        questionBlockValidationService.validateOnCreate(questionBlock);

        questionBlockRepository.save(questionBlock);

        return questionBlock.getId();
    }

    @Override
    @Transactional
    public <T> Long updateQuestionBlock(T questionBlockUpdateRequest, Function<T, QuestionBlock> mappingFunc) {
        log.info("updateQuestionBlock for questionary template {}", questionBlockUpdateRequest);
        QuestionBlock questionBlock = Optional.of(questionBlockUpdateRequest).map(mappingFunc).orElse(null);
        questionBlockValidationService.validateOnUpdate(questionBlock);

        QuestionBlock existedQuestionBlock = questionBlockRepository.getReferenceById(questionBlock.getId());

        existedQuestionBlock.setName(questionBlock.getName());
        existedQuestionBlock.setDescription(questionBlock.getDescription());

        questionBlockRepository.save(existedQuestionBlock);

        return questionBlock.getId();
    }

    @Override
    @Transactional
    public <T> Long delete(T questionaryTemplateDeleteRequest, Function<T, QuestionaryTemplate> mappingFunc) {
        log.info("delete for questionary template {}", questionaryTemplateDeleteRequest);
        QuestionaryTemplate questionaryTemplate = Optional.of(questionaryTemplateDeleteRequest).map(mappingFunc).orElse(null);
        questionaryTemplateValidationService.validateOnDelete(questionaryTemplate);

        questionaryTemplate.setToDelete();
        questionaryTemplateRepository.save(questionaryTemplate);
        questionRepository.deleteAllByQuestionaryTemplateId(questionaryTemplate.getId());
        questionBlockRepository.deleteAllByQuestionaryTemplateId(questionaryTemplate.getId());
        userQuestionaryRepository.setDeletedForAllByQuestionaryTemplateId(questionaryTemplate.getId());

        return questionaryTemplate.getId();
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
