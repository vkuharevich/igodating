package com.igodating.geodata.service;

import com.igodating.geodata.model.Region;

import java.util.List;
import java.util.function.Function;

public interface RegionService {

    <T> T getById(Long id, Function<Region, T> mappingFunc);

    <T> List<T> getAll(Function<Region, T> mappingFunc);

    <T> Long create(T regionCreateRequest, Function<T, Region> mappingFunc);

    <T> Long update(T regionUpdateRequest, Function<T, Region> mappingFunc);

    <T> Long delete(T regionDeleteRequest, Function<T, Region> mappingFunc);
}
