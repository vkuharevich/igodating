package com.igodating.repository;

import com.igodating.model.QuestionaryType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionaryTypeRepository extends JpaRepository<QuestionaryType, Long> {

    @Override
    @EntityGraph("questionaryType.simple")
    List<QuestionaryType> findAll();

    @Override
    @EntityGraph("questionaryType.simple")
    Optional<QuestionaryType> findById(Long aLong);
}
