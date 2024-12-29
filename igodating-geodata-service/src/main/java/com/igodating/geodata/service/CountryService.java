package com.igodating.geodata.service;

import com.igodating.geodata.model.Country;

import java.util.List;
import java.util.function.Function;

public interface CountryService {

    <T> T getById(Long id, Function<Country, T> mappingFunc);

    <T> List<T> getAll(Function<Country, T> mappingFunc);

    <T> Long create(T countryCreateRequest, Function<T, Country> mappingFunc);

    <T> Long update(T countryUpdateRequest, Function<T, Country> mappingFunc);

    <T> Long delete(T countryDeleteRequest, Function<T, Country> mappingFunc);
}
