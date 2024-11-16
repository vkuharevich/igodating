package com.igodating.questionary.repository;

import com.igodating.questionary.model.Question;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Override
    @EntityGraph("question")
    @NonNull
    Optional<Question> findById(@NonNull Long id);

    @EntityGraph("question")
    List<Question> findAllByQuestionaryTemplateIdAndDeletedAtIsNull(Long questionaryTemplateId);

    @Modifying
    @Query(value = """
            update Question q set q.deletedAt = current_timestamp where q.questionaryTemplateId = :questionTemplateId
            """)
    void setDeletedByQuestionTemplateId(@Param("questionTemplateId") Long questionTemplateId);
}
