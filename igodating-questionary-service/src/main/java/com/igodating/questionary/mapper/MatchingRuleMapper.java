package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.MatchingRuleCreateDto;
import com.igodating.questionary.dto.template.MatchingRuleDefaultValueDto;
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

    public String mapDefaultValueDtoToString(MatchingRuleDefaultValueDto defaultValueDto) {
        if (defaultValueDto.fullTextSearchSettings() != null) {
            return tsQueryConverter.fullTextSearchSettingsToTsQuery(defaultValueDto.fullTextSearchSettings());
        }

        return defaultValueDto.value();
    }

    @Named(value = "mapDefaultValuesFromModel")
    public MatchingRuleDefaultValuesDto mapDefaultValuesFromModel(MatchingRule matchingRule) {
        if (RuleMatchingType.LIKE.equals(matchingRule.getMatchingType())) {
            return new MatchingRuleDefaultValuesDto(
                    CollectionUtils.isEmpty(matchingRule.getDefaultValues().getCases()) ? new ArrayList<>() :
                            matchingRule.getDefaultValues().getCases().stream().map(c -> new MatchingRuleDefaultValuesCaseDto(c.getWhen(), mapDefaultValueDtoFromTsQueryString(c.getThen()))).toList(),
                    mapDefaultValueDtoFromTsQueryString(matchingRule.getDefaultValues().getDefaultValue())
            );
        }

        return new MatchingRuleDefaultValuesDto(
                CollectionUtils.isEmpty(matchingRule.getDefaultValues().getCases()) ? new ArrayList<>() :
                        matchingRule.getDefaultValues().getCases().stream().map(c -> new MatchingRuleDefaultValuesCaseDto(c.getWhen(), mapDefaultValueDtoFromString(c.getThen()))).toList(),
                mapDefaultValueDtoFromTsQueryString(matchingRule.getDefaultValues().getDefaultValue())
        );
    }

    public MatchingRuleDefaultValueDto mapDefaultValueDtoFromTsQueryString(String value) {
        return new MatchingRuleDefaultValueDto(null, tsQueryConverter.tsQueryToFullTextSearchSettings(value));
    }

    public MatchingRuleDefaultValueDto mapDefaultValueDtoFromString(String value) {
        return new MatchingRuleDefaultValueDto(value, null);
    }
}
