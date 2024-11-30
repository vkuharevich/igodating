package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.filter.FullTextSearchSettings;
import com.igodating.questionary.dto.template.MatchingRuleCreateDto;
import com.igodating.questionary.dto.template.MatchingRuleUpdateDto;
import com.igodating.questionary.dto.template.MatchingRuleView;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.constant.RuleMatchingType;
import com.igodating.questionary.util.tsquery.TsQueryConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MatchingRuleMapper {

    @Autowired
    private TsQueryConverter tsQueryConverter;

    @Mapping(source = "matchingRule", target = "presetValue", qualifiedByName = "mapPresetValueFromCreateRequest")
    public abstract MatchingRule createRequestToModel(MatchingRuleCreateDto matchingRule);

    @Mapping(source = "matchingRule", target = "presetValue", qualifiedByName = "mapPresetValueFromUpdateRequest")
    public abstract MatchingRule updateRequestToModel(MatchingRuleUpdateDto matchingRule);

    @Mapping(source = "matchingRule", target = "presetValueFullTextSearchSettings", qualifiedByName = "mapPresetValueFullTextSearchSettingsFromModel")
    public abstract MatchingRuleView modelToView(MatchingRule matchingRule);

    @Named("mapPresetValueFromCreateRequest")
    public String mapPresetValueFromCreateRequest(MatchingRuleCreateDto matchingRule) {
        if (matchingRule.presetValueFullTextSearchSettings() != null) {
            return tsQueryConverter.fullTextSearchSettingsToTsQuery(matchingRule.presetValueFullTextSearchSettings());
        }
        return matchingRule.presetValue();
    }

    @Named("mapPresetValueFromUpdateRequest")
    public String mapPresetValueFromUpdateRequest(MatchingRuleUpdateDto matchingRule) {
        if (matchingRule.presetValueFullTextSearchSettings() != null) {
            return tsQueryConverter.fullTextSearchSettingsToTsQuery(matchingRule.presetValueFullTextSearchSettings());
        }
        return matchingRule.presetValue();
    }

    @Named("mapPresetValueFullTextSearchSettingsFromModel")
    public FullTextSearchSettings mapPresetValueFullTextSearchSettingsFromModel(MatchingRule matchingRule) {
        if (RuleMatchingType.LIKE.equals(matchingRule.getMatchingType())) {
            return tsQueryConverter.tsQueryToFullTextSearchSettings(matchingRule.getPresetValue());
        }

        return null;
    }
}
