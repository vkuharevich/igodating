package com.igodating.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "question")
@Data
@DynamicInsert
@NamedEntityGraph(name = "question.full", attributeNodes = {
        @NamedAttributeNode("filter"),
        @NamedAttributeNode("answerOptions")
})
public class Question {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.PERSIST)
    private QuestionaryFilter filter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_block_id", referencedColumnName = "id")
    private QuestionBlock block;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "ordering")
    private Integer order;

    //todo только для FREE_FORM, true - будет участвовать в семантическом векторе, false - ознакомительный характер
    @Column(name = "for_semantic_similarity")
    private Boolean forSemanticSimilarity;

    @Column(name = "weight_coefficient_for_vector")
    private Float weightCoefficientForVector;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.PERSIST)
    private List<AnswerOption> answerOptions;

    public boolean withChoice() {
        return type == QuestionType.CHOICE || type == QuestionType.MULTIPLE_CHOICE;
    }

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

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                '}';
    }
}
