package com.igodating.geodata.controller;

import com.igodating.geodata.dto.CountryCreateRequest;
import com.igodating.geodata.dto.CountryDeleteRequest;
import com.igodating.geodata.dto.CountryUpdateRequest;
import com.igodating.geodata.dto.CountryView;
import com.igodating.geodata.mapper.CountryMapper;
import com.igodating.geodata.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CountryController {

    private final CountryService countryService;

    private final CountryMapper countryMapper;

    @MutationMapping
    public Long createCountry(@Argument CountryCreateRequest country) {
        return countryService.create(countryMapper.createRequestToModel(country));
    }

    @MutationMapping
    public Long updateCountry(@Argument CountryUpdateRequest country) {
        return countryService.update(countryMapper.updateRequestToModel(country));
    }

    @MutationMapping
    public Long deleteCountry(@Argument CountryDeleteRequest country) {
        return countryService.delete(countryMapper.deleteRequestToModel(country));
    }

    @QueryMapping
    public List<CountryView> allCountries() {
        return countryService.getAll(countryMapper::modelToView);
    }
}
