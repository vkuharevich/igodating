package com.igodating.questionary.service.validation.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.service.validation.AnswerValueFormatValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AnswerValueFormatValidationServiceImpl implements AnswerValueFormatValidationService {



    @Override
    public void validateValueWithQuestion(String value, Question question) {
        if (StringUtils.isBlank(value)) {
            throw new ValidationException("Value is empty");
        }

    }

    @Override
    public void validateMultipleValues(String value) {
        if (StringUtils.isBlank(value)) {
            throw new ValidationException("Value is empty");
        }

        String[] splitVal = value.split(CommonConstants.VALUE_SPLITTER);
        if (Arrays.stream(splitVal).anyMatch(StringUtils::isBlank)) {
            throw new ValidationException("Element is empty");
        }
    }
}
