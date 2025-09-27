package com.igodating.geodata.mapper;

import com.igodating.geodata.dto.CountryView;
import com.igodating.geodata.dto.RegionCreateRequest;
import com.igodating.geodata.dto.RegionDeleteRequest;
import com.igodating.geodata.dto.RegionUpdateRequest;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.model.Region;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T14:39:50+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21 (BellSoft)"
)
@Component
public class RegionMapperImpl implements RegionMapper {

    @Autowired
    private CountryMapper countryMapper;

    @Override
    public Region createRequestToModel(RegionCreateRequest region) {
        if ( region == null ) {
            return null;
        }

        Region region1 = new Region();

        region1.setName( region.name() );
        region1.setCountryId( region.countryId() );

        return region1;
    }

    @Override
    public Region updateRequestToModel(RegionUpdateRequest region) {
        if ( region == null ) {
            return null;
        }

        Region region1 = new Region();

        region1.setId( region.id() );
        region1.setName( region.name() );
        region1.setCountryId( region.countryId() );

        return region1;
    }

    @Override
    public Region deleteRequestToModel(RegionDeleteRequest region) {
        if ( region == null ) {
            return null;
        }

        Region region1 = new Region();

        region1.setId( region.id() );

        return region1;
    }

    @Override
    public RegionView modelToView(Region region) {
        if ( region == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        Long countryId = null;
        CountryView country = null;
        LocalDateTime createdAt = null;
        LocalDateTime deletedAt = null;

        id = region.getId();
        name = region.getName();
        countryId = region.getCountryId();
        country = countryMapper.modelToView( region.getCountry() );
        createdAt = region.getCreatedAt();
        deletedAt = region.getDeletedAt();

        RegionView regionView = new RegionView( id, name, countryId, country, createdAt, deletedAt );

        return regionView;
    }
}
