package com.igodating.repository;

import com.igodating.model.Questionary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuestionaryRepository extends JpaRepository<Questionary, Long> {

    @Override
    @EntityGraph("questionary.simple")
    Optional<Questionary> findById(Long aLong);

    @Query(value = """
            select id
            from questionary q
            where q.vectors_initialized is false and q.status = 'PUBLISHED'
            limit 1
            for update
            """, nativeQuery = true)
    Long getPublishedQuestionaryWithUninitializedVectorWithLock();

    @Query(value = """
            select factor_vector
            from questionary q
            """, nativeQuery = true)
    List<float[]> getFactorVectorsAsMatrix(Pageable pageable);
}
