package com.igodating.geodata.repository;

import com.igodating.geodata.model.City;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Override
    @EntityGraph("city")
    List<City> findAll();

    @Modifying
    @Query(value = """
        update city set deleted_at = current_timestamp
        from region r
        inner join country ct on r.country_id = ct.id
        where ct.id = ?1 and r.id = city.region_id
    """, nativeQuery = true)
    void logicalDeleteByCountryId(Long countryId);

    @Modifying
    @Query(value = """
        update city set deleted_at = current_timestamp
        from region r 
        where r.id = ?1 and r.id = city.region_id
    """, nativeQuery = true)
    void logicalDeleteByRegionId(Long regionId);

    @Modifying
    @Query(value = """
        update City c set c.deletedAt = current_timestamp where c.id = :id
    """)
    void logicalDelete(Long id);
}
