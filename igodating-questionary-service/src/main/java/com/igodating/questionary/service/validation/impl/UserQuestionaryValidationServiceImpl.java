package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.repository.UserQuestionaryRepository;
import com.igodating.questionary.service.validation.UserQuestionaryAnswerValidationService;
import com.igodating.questionary.service.validation.UserQuestionaryValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionaryValidationServiceImpl implements UserQuestionaryValidationService {

    private final UserQuestionaryRepository userQuestionaryRepository;

    private final UserQuestionaryAnswerValidationService userQuestionaryAnswerValidationService;

    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateOnDelete(UserQuestionary userQuestionary) {
        checkQuestionaryOnExistenceAndThrowIfDeleted(userQuestionary);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnSetStatusToPublished(UserQuestionary userQuestionary) {
        if (userQuestionary == null) {
            throw new ValidationException("Object is null");
        }

        checkQuestionaryOnExistenceAndThrowIfDeleted(userQuestionary);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnMoveFromDraft(UserQuestionary userQuestionary) {
        UserQuestionary existedQuestionary = checkQuestionaryOnExistenceAndThrowIfDeleted(userQuestionary);

        if (!UserQuestionaryStatus.DRAFT.equals(existedQuestionary.getQuestionaryStatus())) {
            throw new ValidationException("Questionary is not Draft");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdateEmbedding(UserQuestionary userQuestionary) {
        if (CollectionUtils.isEmpty(userQuestionary.getAnswers())) {
            throw new ValidationException("Answers for questionary is empty");
        }

        userQuestionary.getAnswers().forEach(userQuestionaryAnswerValidationService::validateOnUpdateEmbedding);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(UserQuestionary userQuestionary) {
        if (userQuestionary.getId() != null) {
            throw new ValidationException("Cannot create questionary with preset id");
        }

        checkCommonFieldsForCreateAndUpdateInQuestionary(userQuestionary);

        List<Question> allQuestions = questionRepository.findAllByQuestionaryTemplateIdAndDeletedIsFalse(userQuestionary.getQuestionaryTemplateId());
        List<Question> mandatoryQuestions = allQuestions.stream().filter(q -> Boolean.TRUE.equals(q.getIsMandatory())).toList();
        Map<Long, Boolean> mandatoryQuestionsPresentMap = mandatoryQuestions.stream().collect(Collectors.toMap(Question::getId, v -> false));
        Set<Long> allQuestionsIdentifiers = allQuestions.stream().map(Question::getId).collect(Collectors.toSet());

        userQuestionary.getAnswers().forEach(answer -> {
            if (mandatoryQuestionsPresentMap.containsKey(answer.getId())) {
                mandatoryQuestionsPresentMap.put(answer.getId(), true);
            }
            userQuestionaryAnswerValidationService.validateOnCreate(answer, userQuestionary);
            if (allQuestionsIdentifiers.contains(answer.getId())) {
                throw new ValidationException(String.format("Answer on question %d doesn't relate to template", answer.getId()));
            }
        });

        mandatoryQuestionsPresentMap.forEach((key, value) -> {
            if (!value) {
                throw new ValidationException(String.format("Mandatory question with id %d is missed", key));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(UserQuestionary userQuestionary) {
        checkCommonFieldsForCreateAndUpdateInQuestionary(userQuestionary);

        UserQuestionary actualQuestionary = checkQuestionaryOnExistenceAndThrowIfDeleted(userQuestionary);

        if (UserQuestionaryStatus.ON_PROCESSING.equals(actualQuestionary.getQuestionaryStatus())) {
            throw new ValidationException(String.format("Questionary with id %d on processing", userQuestionary.getId()));
        }

        List<Question> allQuestions = questionRepository.findAllByQuestionaryTemplateIdAndDeletedIsFalse(userQuestionary.getQuestionaryTemplateId());
        List<Question> mandatoryQuestions = allQuestions.stream().filter(q -> Boolean.TRUE.equals(q.getIsMandatory())).toList();
        Map<Long, Boolean> mandatoryQuestionsPresentMap = mandatoryQuestions.stream().collect(Collectors.toMap(Question::getId, v -> false));
        Set<Long> allQuestionsIdentifiers = allQuestions.stream().map(Question::getId).collect(Collectors.toSet());

        userQuestionary.getAnswers().forEach(answer -> {
            if (mandatoryQuestionsPresentMap.containsKey(answer.getId())) {
                mandatoryQuestionsPresentMap.put(answer.getId(), true);
            }
            if (!Objects.equals(answer.getUserQuestionaryId(), userQuestionary.getId())) {
                throw new ValidationException(String.format("Wrong questionary id %d for answer", answer.getUserQuestionaryId()));
            }
            if (allQuestionsIdentifiers.contains(answer.getId())) {
                throw new ValidationException(String.format("Answer on question %d doesn't relate to template", answer.getId()));
            }

            if (answer.getId() != null) {
                userQuestionaryAnswerValidationService.validateOnUpdate(answer, userQuestionary);
            } else {
                userQuestionaryAnswerValidationService.validateOnCreate(answer, userQuestionary);
            }
        });

        mandatoryQuestionsPresentMap.forEach((key, value) -> {
            if (!value) {
                throw new ValidationException(String.format("Mandatory question with id %d is missed", key));
            }
        });
    }

    private void checkCommonFieldsForCreateAndUpdateInQuestionary(UserQuestionary userQuestionary) {
        if (StringUtils.isBlank(userQuestionary.getTitle())) {
            throw new ValidationException("Questionary title is empty");
        }

        if (StringUtils.isBlank(userQuestionary.getDescription())) {
            throw new ValidationException("Questionary description is empty");
        }

        if (StringUtils.isBlank(userQuestionary.getUserId())) {
            throw new ValidationException("Questionary user is empty");
        }

        if (userQuestionary.getQuestionaryTemplateId() == null) {
            throw new ValidationException("Questionary template is empty");
        }

        if (CollectionUtils.isEmpty(userQuestionary.getAnswers())) {
            throw new ValidationException("Answers for questionary is empty");
        }

        if (userQuestionary.isDeleted()) {
            throw new ValidationException("Could not manipulate with deleted questionary instance");
        }
    }

    private UserQuestionary checkQuestionaryOnExistenceAndThrowIfDeleted(UserQuestionary userQuestionary) {
        Long id = userQuestionary.getId();
        if (id == null) {
            throw new ValidationException("Id is required for questionary updating");
        }

        UserQuestionary existedQuestionary = userQuestionaryRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Questionary doesn't exist by id %d", id)));

        if (existedQuestionary.isDeleted()) {
            throw new ValidationException("Couldn't change a deleted questionary");
        }

        return existedQuestionary;
    }
}
