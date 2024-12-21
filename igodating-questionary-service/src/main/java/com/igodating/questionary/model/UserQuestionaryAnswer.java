package com.igodating.questionary.model;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import io.hypersistence.utils.hibernate.type.search.PostgreSQLTSVectorType;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_questionary_answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@NamedEntityGraph(name = "userQuestionaryAnswer", attributeNodes = {
        @NamedAttributeNode("question"),
        @NamedAttributeNode("questionary")
})
public class UserQuestionaryAnswer implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Question question;

    @Column(name = "user_questionary_id")
    private Long userQuestionaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_questionary_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserQuestionary questionary;

    private String value;

    @Column(name = "ts_vector_value")
    @Type(PostgreSQLTSVectorType.class)
    private String tsVectorValue;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 512)
    private float[] embedding;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserQuestionaryAnswer that = (UserQuestionaryAnswer) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
