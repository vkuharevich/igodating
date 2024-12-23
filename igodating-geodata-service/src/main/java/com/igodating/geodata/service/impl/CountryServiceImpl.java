package com.igodating.geodata.service.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.geodata.model.Country;
import com.igodating.geodata.repository.CityRepository;
import com.igodating.geodata.repository.CountryRepository;
import com.igodating.geodata.repository.RegionRepository;
import com.igodating.geodata.service.CountryService;
import com.igodating.geodata.service.validation.CountryValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryValidationService countryValidationService;

    private final CountryRepository countryRepository;

    private final RegionRepository regionRepository;

    private final CityRepository cityRepository;

    @Override
    @Transactional(readOnly = true)
    public <T> T getById(Long id, Function<Country, T> mappingFunc) {
        return countryRepository.findById(id).map(mappingFunc).orElseThrow(() -> new ValidationException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAll(Function<Country, T> mappingFunc) {
        return countryRepository.findAll().stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional
    public Long create(Country country) {
        countryValidationService.validateOnCreate(country);

        countryRepository.save(country);

        return country.getId();
    }

    @Override
    @Transactional
    public Long update(Country country) {
        countryValidationService.validateOnUpdate(country);

        countryRepository.save(country);

        return country.getId();
    }

    @Override
    @Transactional
    public Long delete(Country country) {
        countryValidationService.validateOnDelete(country);

        Long id = country.getId();

        countryRepository.logicalDelete(id);
        regionRepository.logicalDeleteByCountryId(id);
        cityRepository.logicalDeleteByCountryId(id);

        return id;
    }
}
