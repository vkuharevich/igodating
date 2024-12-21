package com.igodating.questionary.model;

import com.igodating.questionary.model.constant.RuleAccessType;
import com.igodating.questionary.model.constant.RuleMatchingType;
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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "matching_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@NamedEntityGraph(name = "matchingRule", attributeNodes = {
        @NamedAttributeNode("question")
})
public class MatchingRule implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Question question;

    @Column(name = "matching_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RuleMatchingType matchingType;

    @Column(name = "default_values")
    @JdbcTypeCode(SqlTypes.JSON)
    private MatchingRuleDefaultValues defaultValues;

    @Column(name = "access_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RuleAccessType accessType;

    @Column(name = "is_mandatory_for_matching")
    private Boolean isMandatoryForMatching;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MatchingRule that = (MatchingRule) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
