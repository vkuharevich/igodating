package com.igodating.geodata.service.validation;

import com.igodating.geodata.model.Region;

public interface RegionValidationService {

    void validateOnCreate(Region region);

    void validateOnUpdate(Region region);

    void validateOnDelete(Region region);
}
