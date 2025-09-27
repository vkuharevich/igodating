package com.igodating.geodata.mapper;

import com.igodating.geodata.dto.CountryCreateRequest;
import com.igodating.geodata.dto.CountryDeleteRequest;
import com.igodating.geodata.dto.CountryUpdateRequest;
import com.igodating.geodata.dto.CountryView;
import com.igodating.geodata.model.Country;
import com.igodating.geodata.model.constans.CountryCode;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:39:50+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class CountryMapperImpl implements CountryMapper {

    @Override
    public Country createRequestToModel(CountryCreateRequest country) {
        if ( country == null ) {
            return null;
        }

        Country country1 = new Country();

        country1.setCode( country.code() );
        country1.setName( country.name() );

        return country1;
    }

    @Override
    public Country updateRequestToModel(CountryUpdateRequest country) {
        if ( country == null ) {
            return null;
        }

        Country country1 = new Country();

        country1.setId( country.id() );
        country1.setCode( country.code() );
        country1.setName( country.name() );

        return country1;
    }

    @Override
    public Country deleteRequestToModel(CountryDeleteRequest country) {
        if ( country == null ) {
            return null;
        }

        Country country1 = new Country();

        country1.setId( country.id() );

        return country1;
    }

    @Override
    public CountryView modelToView(Country country) {
        if ( country == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        LocalDateTime createdAt = null;
        LocalDateTime deletedAt = null;

        id = country.getId();
        name = country.getName();
        createdAt = country.getCreatedAt();
        deletedAt = country.getDeletedAt();

        CountryCode countryCode = null;

        CountryView countryView = new CountryView( id, countryCode, name, createdAt, deletedAt );

        return countryView;
    }
}
