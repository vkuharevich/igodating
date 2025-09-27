package com.igodating.questionary.mapper;

import com.igodating.questionary.dto.template.MatchingRuleCreateDto;
import com.igodating.questionary.dto.template.MatchingRuleDefaultValuesCaseDto;
import com.igodating.questionary.dto.template.MatchingRuleDefaultValuesDto;
import com.igodating.questionary.dto.template.MatchingRuleUpdateDto;
import com.igodating.questionary.dto.template.MatchingRuleView;
import com.igodating.questionary.model.MatchingRule;
import com.igodating.questionary.model.MatchingRuleDefaultValues;
import com.igodating.questionary.model.MatchingRuleDefaultValuesCase;
import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:39:47+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class MatchingRuleMapperImpl extends MatchingRuleMapper {

    @Override
    public MatchingRule createRequestToModel(MatchingRuleCreateDto matchingRule) {
        if ( matchingRule == null ) {
            return null;
        }

        MatchingRule matchingRule1 = new MatchingRule();

        matchingRule1.setMatchingType( matchingRule.matchingType() );
        matchingRule1.setDefaultValues( matchingRuleDefaultValuesDtoToMatchingRuleDefaultValues( matchingRule.defaultValues() ) );
        matchingRule1.setAccessType( matchingRule.accessType() );
        matchingRule1.setIsMandatoryForMatching( matchingRule.isMandatoryForMatching() );

        return matchingRule1;
    }

    @Override
    public MatchingRule updateRequestToModel(MatchingRuleUpdateDto matchingRule) {
        if ( matchingRule == null ) {
            return null;
        }

        MatchingRule matchingRule1 = new MatchingRule();

        matchingRule1.setId( matchingRule.id() );
        matchingRule1.setMatchingType( matchingRule.matchingType() );
        matchingRule1.setDefaultValues( matchingRuleDefaultValuesDtoToMatchingRuleDefaultValues( matchingRule.defaultValues() ) );
        matchingRule1.setAccessType( matchingRule.accessType() );
        matchingRule1.setIsMandatoryForMatching( matchingRule.isMandatoryForMatching() );

        return matchingRule1;
    }

    @Override
    public MatchingRuleView modelToView(MatchingRule matchingRule) {
        if ( matchingRule == null ) {
            return null;
        }

        MatchingRuleDefaultValuesDto defaultValues = null;
        Long id = null;
        Long questionId = null;
        RuleMatchingType matchingType = null;
        RuleAccessType accessType = null;
        Boolean isMandatoryForMatching = null;
        LocalDateTime createdAt = null;

        defaultValues = mapDefaultValuesFromModel( matchingRule );
        id = matchingRule.getId();
        questionId = matchingRule.getQuestionId();
        matchingType = matchingRule.getMatchingType();
        accessType = matchingRule.getAccessType();
        isMandatoryForMatching = matchingRule.getIsMandatoryForMatching();
        createdAt = matchingRule.getCreatedAt();

        MatchingRuleView matchingRuleView = new MatchingRuleView( id, questionId, matchingType, defaultValues, accessType, isMandatoryForMatching, createdAt );

        return matchingRuleView;
    }

    protected MatchingRuleDefaultValuesCase matchingRuleDefaultValuesCaseDtoToMatchingRuleDefaultValuesCase(MatchingRuleDefaultValuesCaseDto matchingRuleDefaultValuesCaseDto) {
        if ( matchingRuleDefaultValuesCaseDto == null ) {
            return null;
        }

        String when = null;
        String then = null;

        when = matchingRuleDefaultValuesCaseDto.when();
        then = matchingRuleDefaultValuesCaseDto.then();

        MatchingRuleDefaultValuesCase matchingRuleDefaultValuesCase = new MatchingRuleDefaultValuesCase( when, then );

        return matchingRuleDefaultValuesCase;
    }

    protected List<MatchingRuleDefaultValuesCase> matchingRuleDefaultValuesCaseDtoListToMatchingRuleDefaultValuesCaseList(List<MatchingRuleDefaultValuesCaseDto> list) {
        if ( list == null ) {
            return null;
        }

        List<MatchingRuleDefaultValuesCase> list1 = new ArrayList<MatchingRuleDefaultValuesCase>( list.size() );
        for ( MatchingRuleDefaultValuesCaseDto matchingRuleDefaultValuesCaseDto : list ) {
            list1.add( matchingRuleDefaultValuesCaseDtoToMatchingRuleDefaultValuesCase( matchingRuleDefaultValuesCaseDto ) );
        }

        return list1;
    }

    protected MatchingRuleDefaultValues matchingRuleDefaultValuesDtoToMatchingRuleDefaultValues(MatchingRuleDefaultValuesDto matchingRuleDefaultValuesDto) {
        if ( matchingRuleDefaultValuesDto == null ) {
            return null;
        }

        List<MatchingRuleDefaultValuesCase> cases = null;
        String defaultValue = null;

        cases = matchingRuleDefaultValuesCaseDtoListToMatchingRuleDefaultValuesCaseList( matchingRuleDefaultValuesDto.cases() );
        defaultValue = matchingRuleDefaultValuesDto.defaultValue();

        MatchingRuleDefaultValues matchingRuleDefaultValues = new MatchingRuleDefaultValues( cases, defaultValue );

        return matchingRuleDefaultValues;
    }
}
