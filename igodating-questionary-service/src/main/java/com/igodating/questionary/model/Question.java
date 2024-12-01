package com.igodating.questionary.model;

import com.igodating.questionary.model.constant.QuestionAnswerType;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@NamedEntityGraph(name = "question", attributeNodes = {
        @NamedAttributeNode("questionBlock"),
        @NamedAttributeNode("questionaryTemplate"),
        @NamedAttributeNode("matchingRule")
})
public class Question implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "questionary_template_id")
    private Long questionaryTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionary_template_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QuestionaryTemplate questionaryTemplate;

    @Column(name = "question_block_id")
    private Long questionBlockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_block_id", referencedColumnName = "id", insertable = false, updatable = false)
    private QuestionBlock questionBlock;

    @OneToOne(mappedBy = "question", fetch = FetchType.LAZY)
    private MatchingRule matchingRule;

    private String title;

    private String description;

    @Column(name = "answer_type")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private QuestionAnswerType answerType;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "from_val")
    private BigDecimal fromVal;

    @Column(name = "to_val")
    private BigDecimal toVal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "answer_options")
    @Type(StringArrayType.class)
    private String[] answerOptions;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public boolean withChoice() {
        return QuestionAnswerType.CHOICE.equals(answerType) || QuestionAnswerType.MULTIPLE_CHOICE.equals(answerType);
    }
}
