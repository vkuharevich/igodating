package com.igodating.geodata.repository;

import com.igodating.geodata.model.Region;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    @Override
    @EntityGraph("region")
    List<Region> findAll();

    @Modifying
    @Query(value = """
        update region set deleted_at = current_timestamp
        from country ct
        where ct.id = ?1 and ct.id = region.country_id
    """, nativeQuery = true)
    void logicalDeleteByCountryId(Long countryId);

    @Modifying
    @Query(value = """
        update Region r set r.deletedAt = current_timestamp where r.id = :id
    """)
    void logicalDelete(Long id);
}
