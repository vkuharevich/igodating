package com.igodating.geodata.service.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.geodata.model.Region;
import com.igodating.geodata.repository.CityRepository;
import com.igodating.geodata.repository.RegionRepository;
import com.igodating.geodata.service.RegionService;
import com.igodating.geodata.service.validation.RegionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {

    private final RegionValidationService regionValidationService;

    private final RegionRepository regionRepository;

    private final CityRepository cityRepository;

    @Override
    @Transactional(readOnly = true)
    public <T> T getById(Long id, Function<Region, T> mappingFunc) {
        return regionRepository.findById(id).map(mappingFunc).orElseThrow(() -> new ValidationException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getAll(Function<Region, T> mappingFunc) {
        return regionRepository.findAll().stream().map(mappingFunc).toList();
    }

    @Override
    @Transactional
    public <T> Long create(T regionCreateRequest, Function<T, Region> mappingFunc) {
        Region region = Optional.of(regionCreateRequest).map(mappingFunc).orElse(null);

        regionValidationService.validateOnCreate(region);

        regionRepository.save(region);

        return region.getId();
    }

    @Override
    @Transactional
    public <T> Long update(T regionUpdateRequest, Function<T, Region> mappingFunc) {
        Region region = Optional.of(regionUpdateRequest).map(mappingFunc).orElse(null);

        regionValidationService.validateOnUpdate(region);

        regionRepository.save(region);

        return region.getId();
    }

    @Override
    @Transactional
    public <T> Long delete(T regionDeleteRequest, Function<T, Region> mappingFunc) {
        Region region = Optional.of(regionDeleteRequest).map(mappingFunc).orElse(null);

        regionValidationService.validateOnDelete(region);

        Long id = region.getId();

        regionRepository.logicalDelete(id);
        cityRepository.logicalDeleteByRegionId(id);

        return id;
    }
}
