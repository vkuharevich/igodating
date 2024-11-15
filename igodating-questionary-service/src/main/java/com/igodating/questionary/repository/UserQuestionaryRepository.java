package com.igodating.questionary.repository;

import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestionaryRepository extends JpaRepository<UserQuestionary, Long> {

    @EntityGraph("userQuestionary.answers")
    List<UserQuestionary> findAllByQuestionaryStatusAndDeletedAtIsNull(UserQuestionaryStatus status, Pageable pageable);
}
