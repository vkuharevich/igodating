package com.igodating.service.impl;

import com.igodating.dto.questionarytype.create.QuestionaryTypeCreateDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeFullDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeShortDto;
import com.igodating.mapper.QuestionaryTypeMapper;
import com.igodating.model.QuestionaryType;
import com.igodating.repository.QuestionRepository;
import com.igodating.repository.QuestionaryTypeRepository;
import com.igodating.service.QuestionaryTypeService;
import com.igodating.service.validation.QuestionaryTypeValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.igodating.constant.Constants.QUESTIONARY_TYPE_CACHE;

@Service
@Log4j2
@RequiredArgsConstructor
public class CachedQuestionaryTypeServiceImpl implements QuestionaryTypeService {

    @Autowired
    @Lazy
    private QuestionaryTypeService self;

    private final QuestionRepository questionRepository;

    private final QuestionaryTypeRepository questionaryTypeRepository;

    private final QuestionaryTypeValidationService validationService;

    private final QuestionaryTypeMapper questionaryTypeMapper;

    @Override
    public QuestionaryTypeFullDto getType(long id) {
        return questionaryTypeMapper.modelToFullDto(self.getTypeModel(id));
    }

    @Override
    public List<QuestionaryTypeShortDto> getAllTypes() {
        return questionaryTypeRepository.findAll().stream().map(questionaryTypeMapper::modelToDto).toList();
    }

    @Override
    @Transactional
    public QuestionaryTypeShortDto create(QuestionaryTypeCreateDto createDto) {
        validationService.validateOnCreate(createDto);
        QuestionaryType type = questionaryTypeMapper.createDtoToModel(createDto);
        questionaryTypeRepository.save(type);
        return questionaryTypeMapper.modelToDto(type);
    }

    @Override
    @Cacheable(value = QUESTIONARY_TYPE_CACHE)
    @Transactional(readOnly = true)
    public QuestionaryType getTypeModel(long id) {
        QuestionaryType questionaryType = questionaryTypeRepository.findById(id).orElseThrow();
        loadType(questionaryType);
        return questionaryType;
    }

    private void loadType(QuestionaryType questionaryType) {
        questionaryType.getQuestionBlocks().forEach(b -> {
            b.setQuestions(questionRepository.findAllByBlockId(b.getId()));
        });
    }
}
