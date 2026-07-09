package com.igodating.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import java.util.Objects;

@Entity
@Table(name = "questionary_filter")
@Data
@DynamicInsert
public class QuestionaryFilter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type")
    private FilterType type;

    //todo приватные = скрытые, пользователь не может отправлять, фронту не отдаются
    @Column(name = "is_public")
    private Boolean isPublic;

    //todo в случае приватной фильтрации - берется как аргумент именно это. первое - для 1 выбора. второе - для ренджа.
    @Column(name = "default_options_in_set_keys")
    private String[] defaultOptionsInSetKeys;

    @Column(name = "default_numeric_value_from")
    private Float defaultNumericValueFrom;

    @Column(name = "default_numeric_value_to")
    private Float defaultNumericValueTo;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuestionaryFilter filter = (QuestionaryFilter) o;
        return Objects.equals(id, filter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QuestionaryFilter{" +
                "id=" + id +
                '}';
    }
}
