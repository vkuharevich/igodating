package com.igodating.geodata.service.validation.impl;

import com.igodating.geodata.model.Country;
import com.igodating.geodata.service.validation.CountryValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CountryValidationServiceImpl implements CountryValidationService {

    @Override
    @Transactional(readOnly = true)
    public void validateOnCreate(Country country) {

    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnUpdate(Country country) {

    }

    @Override
    @Transactional(readOnly = true)
    public void validateOnDelete(Country country) {

    }
}
