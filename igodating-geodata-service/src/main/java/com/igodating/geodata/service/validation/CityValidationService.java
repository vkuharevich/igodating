package com.igodating.geodata.service.validation;

import com.igodating.geodata.model.City;

public interface CityValidationService {

    void validateOnCreate(City city);

    void validateOnUpdate(City city);

    void validateOnDelete(City city);
}
