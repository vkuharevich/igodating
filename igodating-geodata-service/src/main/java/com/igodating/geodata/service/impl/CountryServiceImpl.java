package com.igodating.geodata.service.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.geodata.dto.CountryCreateRequest;
import com.igodating.geodata.dto.CountryDeleteRequest;
import com.igodating.geodata.dto.CountryUpdateRequest;
import com.igodating.geodata.dto.CountryView;
import com.igodating.geodata.mapper.CountryMapper;
import com.igodating.geodata.model.Country;
import com.igodating.geodata.repository.CityRepository;
import com.igodating.geodata.repository.CountryRepository;
import com.igodating.geodata.repository.RegionRepository;
import com.igodating.geodata.service.CountryService;
import com.igodating.geodata.service.validation.CountryValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryServiceImpl implements CountryService {

    private final CountryValidationService countryValidationService;

    private final CountryRepository countryRepository;

    private final RegionRepository regionRepository;

    private final CityRepository cityRepository;

    private final CountryMapper countryMapper;

    @Override
    @Transactional(readOnly = true)
    public CountryView getById(Long id) {
        log.info("getById for country {}", id);
        return countryRepository.findById(id)
                .map(countryMapper::modelToView)
                .orElseThrow(() -> new ValidationException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryView> getAll() {
        log.info("getAll for country");
        return countryRepository.findAll()
                .stream()
                .map(countryMapper::modelToView)
                .toList();
    }

    @Override
    @Transactional
    public Long create(CountryCreateRequest countryCreateRequest) {
        log.info("create for country {}", countryCreateRequest);
        Country country = Optional.of(countryCreateRequest)
                .map(countryMapper::createRequestToModel)
                .orElse(null);

        countryValidationService.validateOnCreate(country);

        countryRepository.save(country);

        return country.getId();
    }

    @Override
    @Transactional
    public Long update(CountryUpdateRequest countryUpdateRequest) {
        log.info("update for country {}", countryUpdateRequest);
        Country country = Optional.of(countryUpdateRequest)
                .map(countryMapper::updateRequestToModel)
                .orElse(null);

        countryValidationService.validateOnUpdate(country);

        countryRepository.save(country);

        return country.getId();
    }

    @Override
    @Transactional
    public Long delete(CountryDeleteRequest countryDeleteRequest) {
        log.info("delete for country {}", countryDeleteRequest);
        Country country = Optional.of(countryDeleteRequest)
                .map(countryMapper::deleteRequestToModel)
                .orElse(null);

        countryValidationService.validateOnDelete(country);

        Long id = country.getId();

        countryRepository.logicalDelete(id);
        regionRepository.logicalDeleteByCountryId(id);
        cityRepository.logicalDeleteByCountryId(id);

        return id;
    }
}
