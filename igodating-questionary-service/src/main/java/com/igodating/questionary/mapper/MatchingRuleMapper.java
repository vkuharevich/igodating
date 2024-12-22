package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.MatchingRuleCreateDto;
import com.igodating.questionary.dto.template.MatchingRuleDefaultValuesCaseDto;
import com.igodating.questionary.dto.template.MatchingRuleDefaultValuesDto;
import com.igodating.questionary.dto.template.MatchingRuleUpdateDto;
import com.igodating.questionary.dto.template.MatchingRuleView;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.util.tsquery.TsQueryConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public abstract class MatchingRuleMapper {

    @Autowired
    private TsQueryConverter tsQueryConverter;

    public abstract MatchingRule createRequestToModel(MatchingRuleCreateDto matchingRule);

    public abstract MatchingRule updateRequestToModel(MatchingRuleUpdateDto matchingRule);

    @Mapping(source = "matchingRule", target = "defaultValues", qualifiedByName = "mapDefaultValuesFromModel")
    public abstract MatchingRuleView modelToView(MatchingRule matchingRule);


    @Named(value = "mapDefaultValuesFromModel")
    public MatchingRuleDefaultValuesDto mapDefaultValuesFromModel(MatchingRule matchingRule) {
        if (matchingRule.getDefaultValues() == null) {
            return null;
        }

        if (RuleMatchingType.LIKE.equals(matchingRule.getMatchingType())) {
            return new MatchingRuleDefaultValuesDto(
                    CollectionUtils.isEmpty(matchingRule.getDefaultValues().getCases()) ? new ArrayList<>() :
                            matchingRule.getDefaultValues().getCases().stream().map(c -> new MatchingRuleDefaultValuesCaseDto(c.getWhen(), tsQueryConverter.tsQueryToStr(c.getThen()))).toList(),
                    tsQueryConverter.tsQueryToStr(matchingRule.getDefaultValues().getDefaultValue())
            );
        }

        return new MatchingRuleDefaultValuesDto(
                CollectionUtils.isEmpty(matchingRule.getDefaultValues().getCases()) ? new ArrayList<>() :
                        matchingRule.getDefaultValues().getCases().stream().map(c -> new MatchingRuleDefaultValuesCaseDto(c.getWhen(), c.getThen())).toList(),
                matchingRule.getDefaultValues().getDefaultValue()
        );
    }
}
