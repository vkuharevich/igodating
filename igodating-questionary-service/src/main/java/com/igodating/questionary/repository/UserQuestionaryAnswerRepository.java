package com.igodating.questionary.repository;

import com.igodating.questionary.model.UserQuestionaryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuestionaryAnswerRepository extends JpaRepository<UserQuestionaryAnswer, Long> {

    void deleteAllByUserQuestionaryId(Long userQuestionaryId);
}
