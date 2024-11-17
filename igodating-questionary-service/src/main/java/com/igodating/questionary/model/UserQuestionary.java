package com.igodating.questionary.model;

import com.igodating.questionary.model.constant.UserQuestionaryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_questionary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@NamedEntityGraph(name = "userQuestionary.answers", attributeNodes = {
        @NamedAttributeNode(value = "answers", subgraph = "userQuestionaryAnswer"),
        @NamedAttributeNode(value = "template")
}, subgraphs = {
        @NamedSubgraph(name = "userQuestionaryAnswer", attributeNodes = {
                @NamedAttributeNode(value = "question", subgraph = "question")
        }),
        @NamedSubgraph(name = "question", attributeNodes = {
                @NamedAttributeNode("questionaryTemplate"),
                @NamedAttributeNode("matchingRule")
        })
})
public class UserQuestionary implements SoftDeletable, Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "questionary_template_id")
    private Long questionaryTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionary_template_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QuestionaryTemplate template;

    @Column(name = "questionary_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private UserQuestionaryStatus questionaryStatus;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 512)
    private float[] embedding;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "questionary", fetch = FetchType.LAZY)
    private List<UserQuestionaryAnswer> answers;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserQuestionary that = (UserQuestionary) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public void setToDelete() {
        questionaryStatus = UserQuestionaryStatus.DELETED;
        deletedAt = LocalDateTime.now();
    }

    @Override
    public boolean isDeleted() {
        return UserQuestionaryStatus.DELETED.equals(questionaryStatus);
    }
}
