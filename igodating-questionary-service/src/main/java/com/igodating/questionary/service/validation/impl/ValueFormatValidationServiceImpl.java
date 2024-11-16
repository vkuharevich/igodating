package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.constant.CommonConstants;
import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.service.validation.ValueFormatValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ValueFormatValidationServiceImpl implements ValueFormatValidationService {



    @Override
    public void validateValueWithType(String value, QuestionAnswerType answerType) {
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
