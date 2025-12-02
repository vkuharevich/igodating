package com.igodating.geodata.service;

import com.igodating.geodata.dto.CityCreateRequest;
import com.igodating.geodata.dto.CityDeleteRequest;
import com.igodating.geodata.dto.CityUpdateRequest;
import com.igodating.geodata.dto.CityView;

import java.util.List;

public interface CityService {

    CityView getById(Long id);

    List<CityView> getAll();

    Long create(CityCreateRequest cityCreateRequest);

    Long update(CityUpdateRequest cityUpdateRequest);

    Long delete(CityDeleteRequest cityDeleteRequest);
}
