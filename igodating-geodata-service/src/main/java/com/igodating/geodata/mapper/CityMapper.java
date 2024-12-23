package com.igodating.geodata.mapper;

import com.igodating.geodata.dto.CityCreateRequest;
import com.igodating.geodata.dto.CityDeleteRequest;
import com.igodating.geodata.dto.CityUpdateRequest;
import com.igodating.geodata.dto.CityView;
import com.igodating.geodata.model.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { RegionMapper.class })
public interface CityMapper {

    City createRequestToModel(CityCreateRequest city);

    City updateRequestToModel(CityUpdateRequest city);

    City deleteRequestToModel(CityDeleteRequest city);

    CityView modelToView(City city);
}
