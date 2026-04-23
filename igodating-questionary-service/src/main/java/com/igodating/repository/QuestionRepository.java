package com.igodating.repository;

import com.igodating.model.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @EntityGraph("question.full")
    List<Question> findAllByBlockId(Long blockId);
}
