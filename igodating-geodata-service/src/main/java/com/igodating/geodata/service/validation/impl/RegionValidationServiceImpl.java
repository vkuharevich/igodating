package com.igodating.geodata.service.validation.impl;

import com.igodating.geodata.model.Region;
import com.igodating.geodata.service.validation.RegionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegionValidationServiceImpl implements RegionValidationService {

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(Region region) {

    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(Region region) {

    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnDelete(Region region) {

    }
}
