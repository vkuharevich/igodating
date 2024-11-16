package com.igodating.questionary.repository;

import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuestionaryRepository extends JpaRepository<UserQuestionary, Long> {

    @EntityGraph("userQuestionary.answers")
    @NonNull
    UserQuestionary getReferenceById(@NonNull Long id);

    @EntityGraph("userQuestionary.answers")
    @NonNull
    Optional<UserQuestionary> findById(@NonNull Long id);

    @EntityGraph("userQuestionary.answers")
    List<UserQuestionary> findAllByQuestionaryStatusAndDeletedAtIsNull(UserQuestionaryStatus status, Pageable pageable);

    @Modifying
    @Query(value = """
            update UserQuestionary uq set uq.deletedAt = current_timestamp, uq.questionaryStatus = 'DELETED' where uq.questionaryTemplateId = :questionaryTemplateId
            """)
    void setDeletedForAllByQuestionaryTemplateId(@Param("questionaryTemplateId") Long questionaryTemplateId);

    @Modifying
    @Query(value = """
            update UserQuestionary uq set uq.questionaryStatus = 'NEEDS_CHANGES' where uq.questionaryTemplateId = :questionaryTemplateId
            """)
    void setNeedsChangesForAllByQuestionaryTemplateId(@Param("questionaryTemplateId") Long questionaryTemplateId);
}
