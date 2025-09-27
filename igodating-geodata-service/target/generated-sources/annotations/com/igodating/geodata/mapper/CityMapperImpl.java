package com.igodating.geodata.mapper;

import com.igodating.geodata.dto.CityCreateRequest;
import com.igodating.geodata.dto.CityDeleteRequest;
import com.igodating.geodata.dto.CityUpdateRequest;
import com.igodating.geodata.dto.CityView;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.model.City;
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
public class CityMapperImpl implements CityMapper {

    @Autowired
    private RegionMapper regionMapper;

    @Override
    public City createRequestToModel(CityCreateRequest city) {
        if ( city == null ) {
            return null;
        }

        City city1 = new City();

        city1.setName( city.name() );
        city1.setRegionId( city.regionId() );

        return city1;
    }

    @Override
    public City updateRequestToModel(CityUpdateRequest city) {
        if ( city == null ) {
            return null;
        }

        City city1 = new City();

        city1.setId( city.id() );
        city1.setName( city.name() );
        city1.setRegionId( city.regionId() );

        return city1;
    }

    @Override
    public City deleteRequestToModel(CityDeleteRequest city) {
        if ( city == null ) {
            return null;
        }

        City city1 = new City();

        city1.setId( city.id() );

        return city1;
    }

    @Override
    public CityView modelToView(City city) {
        if ( city == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        Long regionId = null;
        RegionView region = null;
        LocalDateTime createdAt = null;
        LocalDateTime deletedAt = null;

        id = city.getId();
        name = city.getName();
        regionId = city.getRegionId();
        region = regionMapper.modelToView( city.getRegion() );
        createdAt = city.getCreatedAt();
        deletedAt = city.getDeletedAt();

        CityView cityView = new CityView( id, name, regionId, region, createdAt, deletedAt );

        return cityView;
    }
}
