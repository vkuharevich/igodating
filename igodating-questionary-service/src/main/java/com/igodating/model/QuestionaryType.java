package com.igodating.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;
import java.util.Objects;

import static com.igodating.constant.Constants.LAMBDA_MAX;

@Entity
@Table(name = "questionary_type")
@Data
@NamedEntityGraph(name = "questionaryType.simple", attributeNodes = {
        @NamedAttributeNode(value = "questionBlocks", subgraph = "questionaryType.simple.questionBlocks")
}, subgraphs = {
        @NamedSubgraph(name = "questionaryType.simple.questionBlocks", attributeNodes = {
                @NamedAttributeNode("questionaryType")
        })
})
@DynamicInsert
public class QuestionaryType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "factor_lambda_part")
    private Float factorLambdaPart;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionaryType", cascade = CascadeType.PERSIST)
    private List<QuestionBlock> questionBlocks;

    public float getSemanticLambdaPart() {
        return LAMBDA_MAX - factorLambdaPart;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuestionaryType type = (QuestionaryType) o;
        return Objects.equals(id, type.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QuestionaryType{" +
                "id=" + id +
                '}';
    }
}
