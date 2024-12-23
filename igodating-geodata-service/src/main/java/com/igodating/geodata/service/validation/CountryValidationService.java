package com.igodating.geodata.service.validation;

import com.igodating.geodata.model.Country;

public interface CountryValidationService {

    void validateOnCreate(Country country);

    void validateOnUpdate(Country country);

    void validateOnDelete(Country country);
}
