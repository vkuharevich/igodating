package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.repository.QuestionRepository;
import com.igodating.questionary.repository.UserQuestionaryAnswerRepository;
import com.igodating.questionary.service.validation.UserQuestionaryAnswerValidationService;
import com.igodating.questionary.service.validation.ValueFormatValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserQuestionaryAnswerValidationServiceImpl implements UserQuestionaryAnswerValidationService {

    private final ValueFormatValidationService valueFormatValidationService;

    private final QuestionRepository questionRepository;

    private final UserQuestionaryAnswerRepository userQuestionaryAnswerRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdateEmbedding(UserQuestionaryAnswer userQuestionaryAnswer) {
        checkAnswerOnExistence(userQuestionaryAnswer);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(UserQuestionaryAnswer userQuestionaryAnswer, UserQuestionary userQuestionary) {
        checkCommonRequiredFieldsForCreateAndUpdateInAnswer(userQuestionaryAnswer);

        if (userQuestionaryAnswer.getId() != null) {
            throw new ValidationException("Cannot create questionary answer with preset id");
        }

        Question question = checkQuestionOnExistenceAndReturn(userQuestionaryAnswer.getQuestionId());

        if (!Objects.equals(question.getQuestionaryTemplateId(), userQuestionary.getQuestionaryTemplateId())) {
            throw new ValidationException(String.format("Wrong template id %d", question.getQuestionaryTemplateId()));
        }

        valueFormatValidationService.validateValueWithType(userQuestionaryAnswer.getValue(), question.getAnswerType());
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(UserQuestionaryAnswer userQuestionaryAnswer, UserQuestionary userQuestionary) {
        checkCommonRequiredFieldsForCreateAndUpdateInAnswer(userQuestionaryAnswer);

        UserQuestionaryAnswer existedAnswer = checkAnswerOnExistence(userQuestionaryAnswer);
        if (!Objects.equals(existedAnswer.getUserQuestionaryId(), userQuestionary.getId())) {
            throw new ValidationException(String.format("Answer is related to questionary %d", existedAnswer.getUserQuestionaryId()));
        }

        Question question = checkQuestionOnExistenceAndReturn(userQuestionaryAnswer.getQuestionId());

        if (!Objects.equals(question.getQuestionaryTemplateId(), userQuestionary.getQuestionaryTemplateId())) {
            throw new ValidationException(String.format("Wrong template id %d", question.getQuestionaryTemplateId()));
        }

        valueFormatValidationService.validateValueWithType(userQuestionaryAnswer.getValue(), question.getAnswerType());
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInAnswer(UserQuestionaryAnswer userQuestionaryAnswer) {
        if (userQuestionaryAnswer.getQuestionId() == null) {
            throw new ValidationException("Answer question is empty");
        }

        if (StringUtils.isBlank(userQuestionaryAnswer.getValue())) {
            throw new ValidationException("Answer is empty");
        }
    }

    private Question checkQuestionOnExistenceAndReturn(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new ValidationException(String.format("Question doesn't exist by id %d", questionId)));
    }

    private UserQuestionaryAnswer checkAnswerOnExistence(UserQuestionaryAnswer answer) {
        Long id = answer.getId();
        if (id == null) {
            throw new ValidationException("Id is required for questionary updating");
        }

        return userQuestionaryAnswerRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Questionary doesn't exist by id %d", id)));
    }
}
