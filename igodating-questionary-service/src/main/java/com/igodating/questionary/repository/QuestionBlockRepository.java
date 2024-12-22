package com.igodating.questionary.repository;

import com.igodating.questionary.model.QuestionBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionBlockRepository extends JpaRepository<QuestionBlock, Long> {

    List<QuestionBlock> findAllByQuestionaryTemplateId(Long questionaryTemplateId);

    void deleteAllByQuestionaryTemplateId(Long questionaryTemplateId);
}
