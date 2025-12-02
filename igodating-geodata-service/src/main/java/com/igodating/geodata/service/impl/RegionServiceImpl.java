package com.igodating.geodata.service.impl;

import com.igodating.commons.exception.ValidationException;
import com.igodating.geodata.dto.RegionCreateRequest;
import com.igodating.geodata.dto.RegionDeleteRequest;
import com.igodating.geodata.dto.RegionUpdateRequest;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.mapper.RegionMapper;
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

    private final RegionMapper regionMapper;

    @Override
    @Transactional(readOnly = true)
    public RegionView getById(Long id) {
        log.info("getById for region {}", id);
        return regionRepository.findById(id)
                .map(regionMapper::modelToView)
                .orElseThrow(() -> new ValidationException("Entity not found by id"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionView> getAll() {
        log.info("getById for region");
        return regionRepository.findAll()
                .stream()
                .map(regionMapper::modelToView)
                .toList();
    }

    @Override
    @Transactional
    public <T> Long create(RegionCreateRequest regionCreateRequest) {
        log.info("create for region {}", regionCreateRequest);
        Region region = Optional.of(regionCreateRequest)
                .map(regionMapper::createRequestToModel)
                .orElse(null);

        regionValidationService.validateOnCreate(region);

        regionRepository.save(region);

        return region.getId();
    }

    @Override
    @Transactional
    public <T> Long update(RegionUpdateRequest regionUpdateRequest) {
        log.info("update for region {}", regionUpdateRequest);
        Region region = Optional.of(regionUpdateRequest)
                .map(regionMapper::updateRequestToModel)
                .orElse(null);

        regionValidationService.validateOnUpdate(region);

        regionRepository.save(region);

        return region.getId();
    }

    @Override
    @Transactional
    public <T> Long delete(RegionDeleteRequest regionDeleteRequest) {
        log.info("delete for region {}", regionDeleteRequest);
        Region region = Optional.of(regionDeleteRequest)
                .map(regionMapper::deleteRequestToModel)
                .orElse(null);

        regionValidationService.validateOnDelete(region);

        Long id = region.getId();

        regionRepository.logicalDelete(id);
        cityRepository.logicalDeleteByRegionId(id);

        return id;
    }
}
