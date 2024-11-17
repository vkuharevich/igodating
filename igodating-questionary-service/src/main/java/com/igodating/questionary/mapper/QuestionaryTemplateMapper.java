package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateView;
import com.igodating.questionary.model.QuestionaryTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { QuestionMapper.class })
public interface QuestionaryTemplateMapper {

    QuestionaryTemplate createRequestToModel(QuestionaryTemplateCreateRequest questionaryTemplate);

    QuestionaryTemplate updateRequestToModel(QuestionaryTemplateUpdateRequest questionaryTemplate);

    QuestionaryTemplateView modelToView(QuestionaryTemplate questionaryTemplate);
}
