package com.igodating.geodata.service;

import com.igodating.geodata.dto.RegionCreateRequest;
import com.igodating.geodata.dto.RegionDeleteRequest;
import com.igodating.geodata.dto.RegionUpdateRequest;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.model.Region;

import java.util.List;
import java.util.function.Function;

public interface RegionService {

    RegionView getById(Long id);

    List<RegionView> getAll();

    Long create(RegionCreateRequest regionCreateRequest);

    Long update(RegionUpdateRequest regionUpdateRequest);

    Long delete(RegionDeleteRequest regionDeleteRequest);
}
