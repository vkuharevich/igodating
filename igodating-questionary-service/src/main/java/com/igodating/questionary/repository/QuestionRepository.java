package com.igodating.questionary.repository;

import com.igodating.questionary.model.Question;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<Question> findAllByQuestionaryTemplateId(Long questionaryTemplateId);

    void deleteAllByQuestionaryTemplateId(Long questionaryTemplateId);
}
