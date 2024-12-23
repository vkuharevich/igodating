package com.igodating.geodata.service.validation.impl;

import com.igodating.geodata.model.City;
import com.igodating.geodata.service.validation.CityValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityValidationServiceImpl implements CityValidationService {

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(City city) {

    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(City city) {

    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnDelete(City city) {

    }
}
