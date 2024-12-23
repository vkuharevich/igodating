package com.igodating.geodata.mapper;

import com.igodating.geodata.dto.RegionCreateRequest;
import com.igodating.geodata.dto.RegionDeleteRequest;
import com.igodating.geodata.dto.RegionUpdateRequest;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.model.Region;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { CountryMapper.class })
public interface RegionMapper {

    Region createRequestToModel(RegionCreateRequest region);

    Region updateRequestToModel(RegionUpdateRequest region);

    Region deleteRequestToModel(RegionDeleteRequest region);

    RegionView modelToView(Region region);
}
