package com.igodating.questionary.service.validation.impl;

import com.igodating.questionary.exception.ValidationException;
import com.igodating.questionary.model.Question;
import com.igodating.questionary.model.QuestionBlock;
import com.igodating.questionary.repository.QuestionBlockRepository;
import com.igodating.questionary.service.validation.QuestionBlockValidationService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionBlockValidationServiceImpl implements QuestionBlockValidationService {

    private final QuestionBlockRepository questionBlockRepository;

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(QuestionBlock questionBlock) {
        checkCommonRequiredFieldsForCreateAndUpdateInQuestionBlock(questionBlock);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(QuestionBlock questionBlock) {
        checkCommonRequiredFieldsForCreateAndUpdateInQuestionBlock(questionBlock);

        checkQuestionOnExistenceAndThrowIfDeleted(questionBlock);
    }

    private void checkCommonRequiredFieldsForCreateAndUpdateInQuestionBlock(QuestionBlock questionBlock) {
        if (StringUtils.isBlank(questionBlock.getName())) {
            throw new ValidationException("Block is empty");
        }
    }

    private void checkQuestionOnExistenceAndThrowIfDeleted(QuestionBlock questionBlock) {
        Long id = questionBlock.getId();
        if (id == null) {
            throw new ValidationException("Id is required for question");
        }

        questionBlockRepository.findById(id).orElseThrow(() -> new ValidationException(String.format("Question block doesn't exist by id %d", id)));
    }
}
