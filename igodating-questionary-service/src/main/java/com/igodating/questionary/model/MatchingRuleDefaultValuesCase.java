package com.igodating.questionary.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchingRuleDefaultValuesCase implements Serializable {
    private String when;
    private String then;
}
