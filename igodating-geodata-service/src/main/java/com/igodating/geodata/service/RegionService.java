package com.igodating.geodata.service;

import com.igodating.geodata.model.Region;

import java.util.List;
import java.util.function.Function;

public interface RegionService {

    <T> T getById(Long id, Function<Region, T> mappingFunc);

    <T> List<T> getAll(Function<Region, T> mappingFunc);

    Long create(Region region);

    Long update(Region region);

    Long delete(Region region);
}
