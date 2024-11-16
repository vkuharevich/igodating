package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.model.constant.QuestionAnswerType;
import com.igodating.questionary.service.validation.ValueFormatValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValueFormatValidationServiceImpl implements ValueFormatValidationService {


    @Override
    public void validateValueWithType(String value, QuestionAnswerType answerType) {

    }
}
