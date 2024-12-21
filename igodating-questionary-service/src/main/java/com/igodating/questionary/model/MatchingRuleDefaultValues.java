package com.igodating.questionary.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class MatchingRuleDefaultValues implements Serializable {
    private List<MatchingRuleDefaultValuesCase> cases;
    private String defaultValue;
}
