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
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.igodating.constant.Constants.FACTOR_VECTOR_SIZE;
import static com.igodating.constant.Constants.SEMANTIC_VECTOR_SIZE;

@Entity
@Table(name = "questionary")
@Data
@NamedEntityGraph(name = "questionary.simple", attributeNodes = {
        @NamedAttributeNode("type"),
        @NamedAttributeNode(value = "answers", subgraph = "questionary.simple.answer")
}, subgraphs = {
        @NamedSubgraph(name = "questionary.simple.answer", attributeNodes = {
                @NamedAttributeNode("question"),
                @NamedAttributeNode("questionary")
        })
})
@DynamicInsert
public class Questionary {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private QuestionaryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionary_type_id", referencedColumnName = "id")
    private QuestionaryType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionary", cascade = CascadeType.PERSIST)
    private List<Answer> answers;

    @Column(name = "vectors_initialized")
    private Boolean vectorsInitialized;

    @Column(name = "semantic_vector")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = SEMANTIC_VECTOR_SIZE)
    private double[] semanticVector = new double[SEMANTIC_VECTOR_SIZE];

    @Column(name = "factor_vector")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = FACTOR_VECTOR_SIZE)
    private double[] factorVector = new double[FACTOR_VECTOR_SIZE];

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Questionary that = (Questionary) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Questionary{" +
                "id=" + id +
                '}';
    }
}
