package com.igodating.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Objects;

@Entity
@Table(name = "similarity_factor")
@Data
public class SimilarityFactor {

    @Id
    @Column(name = "vector_index")
    private Long index;

    @Column(name = "name")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimilarityFactor that = (SimilarityFactor) o;
        return Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }

    @Override
    public String toString() {
        return "SimilarityFactor{" +
                "index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}
