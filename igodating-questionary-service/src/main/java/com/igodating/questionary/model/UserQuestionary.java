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
import java.util.Objects;

@Entity
@Table(name = "user_questionary")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class UserQuestionary {

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

    @Column( name = "embedding" )
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 512)
    private float[] embedding;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

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
}
