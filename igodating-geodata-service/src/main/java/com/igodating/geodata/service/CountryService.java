package com.igodating.geodata.service;

import com.igodating.geodata.dto.CountryCreateRequest;
import com.igodating.geodata.dto.CountryDeleteRequest;
import com.igodating.geodata.dto.CountryUpdateRequest;
import com.igodating.geodata.dto.CountryView;

import java.util.List;

public interface CountryService {

    CountryView getById(Long id);

    List<CountryView> getAll();

    Long create(CountryCreateRequest countryCreateRequest);

    Long update(CountryUpdateRequest countryUpdateRequest);

    Long delete(CountryDeleteRequest countryDeleteRequest);
}
