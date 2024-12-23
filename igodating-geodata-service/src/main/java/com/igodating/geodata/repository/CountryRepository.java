package com.igodating.geodata.repository;

import com.igodating.geodata.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Modifying
    @Query(value = """
        update Country c set c.deletedAt = current_timestamp where c.id = :id
    """)
    void logicalDelete(Long id);
}
