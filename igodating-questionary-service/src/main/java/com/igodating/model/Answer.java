package com.igodating.model;

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
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Type;

import java.util.Objects;

@Entity
@Table(name = "answer")
@Data
@DynamicInsert
@NamedEntityGraph(name = "answer.full", attributeNodes = {
        @NamedAttributeNode("questionary"),
        @NamedAttributeNode("question")
})
public class Answer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionary_id", referencedColumnName = "id")
    private Questionary questionary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    //todo свободная форма
    @Column(name = "text_value")
    private String textValue;

    //todo число
    @Column(name = "numeric_value")
    private Double numericValue;

    //todo выбранные варианты ответа (для простого choice - всегда 1 элемент)
    @Column(name = "chosen_options_keys")
    @Type(StringArrayType.class)
    private String[] chosenOptions;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return Objects.equals(id, answer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                '}';
    }
}
