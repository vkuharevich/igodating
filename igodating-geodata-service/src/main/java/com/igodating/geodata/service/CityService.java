package com.igodating.geodata.service;

import com.igodating.geodata.model.City;

import java.util.List;
import java.util.function.Function;

public interface CityService {

    <T> T getById(Long id, Function<City, T> mappingFunc);

    <T> List<T> getAll(Function<City, T> mappingFunc);

    <T> Long create(T cityCreateRequest, Function<T, City> mappingFunc);

    <T> Long update(T cityUpdateRequest, Function<T, City> mappingFunc);

    <T> Long delete(T cityDeleteRequest, Function<T, City> mappingFunc);
}
