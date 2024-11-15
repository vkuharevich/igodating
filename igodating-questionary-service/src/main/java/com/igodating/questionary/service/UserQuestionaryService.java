package com.igodating.questionary.service;

import com.igodating.questionary.model.UserQuestionary;
import com.igodating.questionary.model.constant.UserQuestionaryStatus;

import java.util.List;

public interface UserQuestionaryService {

    void create(UserQuestionary userQuestionary);

    void update(UserQuestionary userQuestionary);

    List<UserQuestionary> findUnprocessedWithLimit(int limit);
}
