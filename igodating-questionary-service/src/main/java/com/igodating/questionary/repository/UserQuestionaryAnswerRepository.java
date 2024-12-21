package com.igodating.questionary.repository;

import com.igodating.questionary.model.UserQuestionaryAnswer;
import com.igodating.questionary.model.constant.RuleAccessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestionaryAnswerRepository extends JpaRepository<UserQuestionaryAnswer, Long> {

    @Query(value = """
            select uqa
            from UserQuestionaryAnswer uqa
            left join fetch uqa.question q
            left join fetch q.matchingRule mr
            left join fetch q.questionBlock qb
            left join fetch uqa.questionary uq
            where uq.userId = :userId and uq.questionaryTemplateId = :questionaryTemplateId and uq.deletedAt is null and mr.accessType = :accessType
            """)
    List<UserQuestionaryAnswer> findAllNotDeletedByQuestionaryTemplateIdAndUserIdAndRuleAccessType(Long questionaryTemplateId, String userId, RuleAccessType accessType);

    void deleteAllByUserQuestionaryId(Long userQuestionaryId);
}
