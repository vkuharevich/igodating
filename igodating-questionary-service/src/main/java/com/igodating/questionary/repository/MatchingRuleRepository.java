package com.igodating.questionary.repository;

import com.igodating.questionary.model.MatchingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingRuleRepository extends JpaRepository<MatchingRule, Long> {
}
