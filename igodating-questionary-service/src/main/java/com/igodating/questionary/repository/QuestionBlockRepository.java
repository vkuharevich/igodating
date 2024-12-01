package com.igodating.questionary.repository;

import com.igodating.questionary.model.QuestionBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionBlockRepository extends JpaRepository<QuestionBlock, Long> {

    void deleteAllByQuestionaryTemplateId(Long questionaryTemplateId);
}
