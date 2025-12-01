package com.igodating.questionary.service.impl;

import com.igodating.questionary.constant.SimilarityCalculatingOperator;
import com.igodating.questionary.dto.filter.UserQuestionaryRecommendationRequest;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionaryTemplate;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import com.igodating.questionary.model.view.UserQuestionaryRecommendationView;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.repository.UserQuestionaryAnswerRepository;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.UserQuestionaryService;
import com.igodating.questionary.service.validation.UserQuestionaryValidationService;
import com.igodating.commons.utils.EntitiesListChange;
import com.igodating.commons.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserQuestionaryServiceImpl implements UserQuestionaryService {

    private final QuestionRepository questionRepository;

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final UserQuestionaryAnswerRepository userQuestionaryAnswerRepository;

    private final UserQuestionaryValidationService userQuestionaryValidationService;

    @Value("${recommendation.similarity-calculating-operator}")
    private SimilarityCalculatingOperator similarityCalculatingOperator;

    @Override
    @Transactional(readOnly = true)
    public <T> T getById(Long id, Function<UserQuestionary, T> mappingFunc) {
        log.info("getById for questionary {}", id);
        return userQuestionaryRepository.findById(id).map(mappingFunc).orElseThrow(() -> new RuntimeException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId(Long templateId, String userId, Function<UserQuestionaryAnswer, T> mappingFunc) {
        log.info("getAllAnswersMatchedWithPublicRulesByTemplateIdAndUserId for questionary, templateId = {}, userId = {}", templateId, userId);
        return userQuestionaryAnswerRepository.findAllNotDeletedByQuestionaryTemplateIdAndUserIdAndRuleAccessType(templateId, userId, RuleAccessType.PUBLIC).stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public <T> Slice<T> findRecommendations(UserQuestionaryRecommendationRequest filter, String userId, BiFunction<UserQuestionaryRecommendationView, SimilarityCalculatingOperator, T> mappingFunc) {
        log.info("findRecommendations for questionary, filter = {}, userId = {}", filter, userId);
        userQuestionaryValidationService.validateUserQuestionaryFilter(filter, userId);

        UserQuestionary forQuestionary = userQuestionaryRepository.findById(filter.forUserQuestionaryId()).orElseThrow(() -> new RuntimeException("Entity not found by id"));
        Slice<UserQuestionaryRecommendationView> recommendations = userQuestionaryRepository.findRecommendations(forQuestionary, filter.userFilters(), similarityCalculatingOperator, filter.limit(), filter.offset());

        return new SliceImpl<>(recommendations.getContent().stream().map((r) -> mappingFunc.apply(r, similarityCalculatingOperator)).toList(), recommendations.getPageable(), recommendations.hasNext());
    }

    @Override
    @Transactional
    public <T> Long createDraft(T userQuestionaryCreateRequest, String userId, Function<T, UserQuestionary> mappingFunc) {
        log.info("createDraft for questionary, filter = {}, userId = {}", userQuestionaryCreateRequest, userId);
        UserQuestionary userQuestionary = Optional.of(userQuestionaryCreateRequest).map(mappingFunc).orElse(null);
        userQuestionary.setUserId(userId);

        userQuestionaryValidationService.validateOnCreate(userQuestionary);

        userQuestionary.setQuestionaryStatus(UserQuestionaryStatus.DRAFT);

        userQuestionaryRepository.save(userQuestionary);

        for (UserQuestionaryAnswer userQuestionaryAnswer : userQuestionary.getAnswers()) {
            initTsVectorValueIfNeeded(userQuestionaryAnswer);
            userQuestionaryAnswer.setUserQuestionaryId(userQuestionary.getId());
        }

        userQuestionaryAnswerRepository.saveAll(userQuestionary.getAnswers());

        return userQuestionary.getId();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <T> Long update(T userQuestionaryUpdateRequest, String userId, Function<T, UserQuestionary> mappingFunc) {
        log.info("update for questionary, filter = {}, userId = {}", userQuestionaryUpdateRequest, userId);
        UserQuestionary userQuestionary = Optional.of(userQuestionaryUpdateRequest).map(mappingFunc).orElse(null);
        userQuestionaryValidationService.validateOnUpdate(userQuestionary, userId);

        UserQuestionary existedQuestionary = userQuestionaryRepository.getReferenceById(userQuestionary.getId());
        existedQuestionary.setQuestionaryStatus(userQuestionary.getQuestionaryStatus());
        existedQuestionary.setTitle(userQuestionary.getTitle());
        existedQuestionary.setDescription(userQuestionary.getDescription());

        EntitiesListChange<UserQuestionaryAnswer, Long> answersChange = ServiceUtils.changes(existedQuestionary.getAnswers(), userQuestionary.getAnswers(), (first, second) -> Objects.equals(first.getValue(), second.getValue()));

        boolean isDraft = UserQuestionaryStatus.DRAFT.equals(existedQuestionary.getQuestionaryStatus());
        boolean semanticRankingAnswerWasChanged = false;

        for (UserQuestionaryAnswer answerToDelete : answersChange.getToDelete()) {
            if (!isDraft && !semanticRankingAnswerWasChanged && questionIsSemantic(questionRepository.getReferenceById(answerToDelete.getQuestionId()))) {
                semanticRankingAnswerWasChanged = true;
            }
            userQuestionaryAnswerRepository.delete(answerToDelete);
        }

        for (Pair<UserQuestionaryAnswer, UserQuestionaryAnswer> answerPairToUpdate : answersChange.getOldNewPairToUpdate()) {
            UserQuestionaryAnswer oldAnswer = answerPairToUpdate.getFirst();
            UserQuestionaryAnswer newAnswer = answerPairToUpdate.getSecond();

            if (!isDraft && !semanticRankingAnswerWasChanged && questionIsSemantic(questionRepository.getReferenceById(oldAnswer.getQuestionId()))) {
                semanticRankingAnswerWasChanged = true;
            }

            oldAnswer.setValue(newAnswer.getValue());
            initTsVectorValueIfNeeded(oldAnswer);
            userQuestionaryAnswerRepository.save(oldAnswer);
        }

        for (UserQuestionaryAnswer answerToCreate : answersChange.getToCreate()) {
            if (!isDraft && !semanticRankingAnswerWasChanged && questionIsSemantic(questionRepository.getReferenceById(answerToCreate.getQuestionId()))) {
                semanticRankingAnswerWasChanged = true;
            }

            initTsVectorValueIfNeeded(answerToCreate);
            answerToCreate.setUserQuestionaryId(userQuestionary.getId());
            userQuestionaryAnswerRepository.save(answerToCreate);
        }

        if (!isDraft && semanticRankingAnswerWasChanged) {
            existedQuestionary.setQuestionaryStatus(UserQuestionaryStatus.ON_PROCESSING);
        }

        userQuestionaryRepository.save(existedQuestionary);

        return existedQuestionary.getId();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void setStatusToPublished(UserQuestionary userQuestionary) {
        log.info("setStatusToPublished for questionary {}", userQuestionary);
        userQuestionaryValidationService.validateOnSetStatusToPublished(userQuestionary);

        UserQuestionary existedQuestionary = userQuestionaryRepository.getReferenceById(userQuestionary.getId());

        existedQuestionary.setQuestionaryStatus(UserQuestionaryStatus.PUBLISHED);

        userQuestionaryRepository.save(existedQuestionary);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <T> Long moveFromDraft(T userQuestionaryMoveFromDraftRequest, String userId, Function<T, UserQuestionary> mappingFunc) {
        log.info("moveFromDraft for questionary, request = {}, userId = {}", userQuestionaryMoveFromDraftRequest, userId);
        UserQuestionary userQuestionary = Optional.of(userQuestionaryMoveFromDraftRequest).map(mappingFunc).orElse(null);
        userQuestionaryValidationService.validateOnMoveFromDraft(userQuestionary, userId);

        UserQuestionary existedQuestionary = userQuestionaryRepository.getReferenceById(userQuestionary.getId());

        boolean answerWithSemanticRangingDoesExist = existedQuestionary.getAnswers()
                .stream()
                .anyMatch(answer -> questionIsSemantic(answer.getQuestion()));

        existedQuestionary.setQuestionaryStatus(answerWithSemanticRangingDoesExist ? UserQuestionaryStatus.ON_PROCESSING : UserQuestionaryStatus.PUBLISHED);
        userQuestionaryRepository.save(existedQuestionary);

        return existedQuestionary.getId();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public <T> Long delete(T userQuestionaryDeleteRequest, String userId, Function<T, UserQuestionary> mappingFunc) {
        log.info("delete for questionary, request = {}, userId = {}", userQuestionaryDeleteRequest, userId);
        UserQuestionary userQuestionary = Optional.of(userQuestionaryDeleteRequest).map(mappingFunc).orElse(null);
        userQuestionaryValidationService.validateOnDelete(userQuestionary, userId);

        UserQuestionary existedQuestionary = userQuestionaryRepository.getReferenceById(userQuestionary.getId());
        existedQuestionary.setToDelete();

        userQuestionaryRepository.save(existedQuestionary);

        userQuestionaryAnswerRepository.deleteAllByUserQuestionaryId(existedQuestionary.getId());

        return existedQuestionary.getId();
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateEmbeddingAndSetProcessed(UserQuestionary userQuestionary) {
        userQuestionaryValidationService.validateOnUpdateEmbedding(userQuestionary);

        UserQuestionary existedQuestionary = userQuestionaryRepository.getReferenceById(userQuestionary.getId());
        existedQuestionary.setEmbedding(userQuestionary.getEmbedding());
        existedQuestionary.setQuestionaryStatus(UserQuestionaryStatus.PUBLISHED);

        userQuestionaryRepository.save(existedQuestionary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserQuestionary> findUnprocessedWithLimit(int limit) {
        return userQuestionaryRepository.findAllByQuestionaryStatusAndDeletedAtIsNull(
                UserQuestionaryStatus.ON_PROCESSING,
                PageRequest.of(0, limit, Sort.by(Sort.Order.desc("id")))
        );
    }

    private boolean questionIsSemantic(Question question) {
        MatchingRule matchingRule = question.getMatchingRule();

        if (matchingRule == null) {
            return false;
        }

        return RuleMatchingType.SEMANTIC_RANGING.equals(matchingRule.getMatchingType());
    }

    private void initTsVectorValueIfNeeded(UserQuestionaryAnswer userQuestionaryAnswer) {
        Question question = questionRepository.getReferenceById(userQuestionaryAnswer.getQuestionId());

        if (question.getAnswerType().equals(QuestionAnswerType.FREE_FORM)) {
            userQuestionaryAnswer.setTsVectorValue(userQuestionaryAnswer.getValue());
        }
    }
}
