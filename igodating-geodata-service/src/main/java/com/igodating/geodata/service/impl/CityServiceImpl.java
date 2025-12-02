package com.igodating.geodata.service.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.geodata.dto.CityCreateRequest;
import com.igodating.geodata.dto.CityDeleteRequest;
import com.igodating.geodata.dto.CityUpdateRequest;
import com.igodating.geodata.dto.CityView;
import com.igodating.geodata.mapper.CityMapper;
import com.igodating.geodata.model.City;
import com.igodating.geodata.repository.CityRepository;
import com.igodating.geodata.service.CityService;
import com.igodating.geodata.service.validation.CityValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityServiceImpl implements CityService {

    private final CityValidationService cityValidationService;

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    @Override
    @Transactional(readOnly = true)
    public CityView getById(Long id) {
        log.info("getById for city {}", id);
        return cityRepository.findById(id)
                .map(cityMapper::modelToView)
                .orElseThrow(() -> new ValidationException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityView> getAll() {
        log.info("getAll for city");
        return cityRepository.findAll()
                .stream()
                .map(cityMapper::modelToView)
                .toList();
    }

    @Override
    @Transactional
    public Long create(CityCreateRequest cityCreateRequest) {
        log.info("create for city {}", cityCreateRequest);
        City city = Optional.of(cityCreateRequest)
                .map(cityMapper::createRequestToModel)
                .orElse(null);

        cityValidationService.validateOnCreate(city);

        cityRepository.save(city);

        return city.getId();
    }

    @Override
    @Transactional
    public Long update(CityUpdateRequest cityUpdateRequest) {
        log.info("update for city {}", cityUpdateRequest);
        City city = Optional.of(cityUpdateRequest)
                .map(cityMapper::updateRequestToModel)
                .orElse(null);

        cityValidationService.validateOnUpdate(city);

        cityRepository.save(city);

        return city.getId();
    }

    @Override
    @Transactional
    public Long delete(CityDeleteRequest cityDeleteRequest) {
        log.info("delete for city {}", cityDeleteRequest);
        City city = Optional.of(cityDeleteRequest)
                .map(cityMapper::deleteRequestToModel)
                .orElse(null);

        cityValidationService.validateOnDelete(city);

        Long id = city.getId();

        cityRepository.logicalDelete(id);

        return id;
    }
}
