package com.igodating.geodata.mapper;

import com.igodating.geodata.dto.CountryCreateRequest;
import com.igodating.geodata.dto.CountryDeleteRequest;
import com.igodating.geodata.dto.CountryUpdateRequest;
import com.igodating.geodata.dto.CountryView;
import com.igodating.geodata.model.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    Country createRequestToModel(CountryCreateRequest country);

    Country updateRequestToModel(CountryUpdateRequest country);

    Country deleteRequestToModel(CountryDeleteRequest country);

    CountryView modelToView(Country country);
}
