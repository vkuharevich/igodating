package com.igodating.questionary.repository;

import com.igodating.questionary.model.QuestionaryTemplate;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionaryTemplateRepository extends JpaRepository<QuestionaryTemplate, Long> {

    @Override
    @EntityGraph("questionaryTemplate")
    @NonNull
    QuestionaryTemplate getReferenceById(@NonNull Long id);

    @Override
    @EntityGraph("questionaryTemplate")
    @NonNull
    Optional<QuestionaryTemplate> findById(@NonNull Long id);
}
