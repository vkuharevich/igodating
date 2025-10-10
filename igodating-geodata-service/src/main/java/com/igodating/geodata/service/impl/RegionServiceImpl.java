package com.igodating.geodata.service.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.geodata.model.Region;
import com.igodating.geodata.repository.CityRepository;
import com.igodating.geodata.repository.RegionRepository;
import com.igodating.geodata.service.RegionService;
import com.igodating.geodata.service.validation.RegionValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionServiceImpl implements RegionService {

    private final RegionValidationService regionValidationService;

    private final RegionRepository regionRepository;

    private final CityRepository cityRepository;

    @Override
    @Transactional(readOnly = true)
    public <T> T getById(Long id, Function<Region, T> mappingFunc) {
        log.info("getById for region {}", id);
        return regionRepository.findById(id).map(mappingFunc).orElseThrow(() -> new ValidationException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAll(Function<Region, T> mappingFunc) {
        log.info("getById for region");
        return regionRepository.findAll().stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional
    public <T> Long create(T regionCreateRequest, Function<T, Region> mappingFunc) {
        log.info("create for region {}", regionCreateRequest);
        Region region = Optional.of(regionCreateRequest).map(mappingFunc).orElse(null);

        regionValidationService.validateOnCreate(region);

        regionRepository.save(region);

        return region.getId();
    }

    @Override
    @Transactional
    public <T> Long update(T regionUpdateRequest, Function<T, Region> mappingFunc) {
        log.info("update for region {}", regionUpdateRequest);
        Region region = Optional.of(regionUpdateRequest).map(mappingFunc).orElse(null);

        regionValidationService.validateOnUpdate(region);

        regionRepository.save(region);

        return region.getId();
    }

    @Override
    @Transactional
    public <T> Long delete(T regionDeleteRequest, Function<T, Region> mappingFunc) {
        log.info("delete for region {}", regionDeleteRequest);
        Region region = Optional.of(regionDeleteRequest).map(mappingFunc).orElse(null);

        regionValidationService.validateOnDelete(region);

        Long id = region.getId();

        regionRepository.logicalDelete(id);
        cityRepository.logicalDeleteByRegionId(id);

        return id;
    }
}
