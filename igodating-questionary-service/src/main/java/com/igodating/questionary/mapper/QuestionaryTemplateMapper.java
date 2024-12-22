package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.QuestionaryTemplateCreateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateDeleteRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateUpdateRequest;
import com.igodating.questionary.dto.template.QuestionaryTemplateView;
import com.igodating.questionary.model.QuestionaryTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { MatchingRuleMapper.class, QuestionBlockMapper.class })
public interface QuestionaryTemplateMapper {

    QuestionaryTemplate createRequestToModel(QuestionaryTemplateCreateRequest questionaryTemplate);

    QuestionaryTemplate updateRequestToModel(QuestionaryTemplateUpdateRequest questionaryTemplate);

    QuestionaryTemplate deleteRequestToModel(QuestionaryTemplateDeleteRequest questionaryTemplate);

    QuestionaryTemplateView modelToView(QuestionaryTemplate questionaryTemplate);
}
