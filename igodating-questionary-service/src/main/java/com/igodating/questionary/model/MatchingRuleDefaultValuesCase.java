package com.igodating.questionary.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MatchingRuleDefaultValuesCase implements Serializable {
    private String when;
    private String then;
}
