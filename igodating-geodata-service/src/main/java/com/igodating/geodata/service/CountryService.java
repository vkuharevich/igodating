package com.igodating.geodata.service;

import com.igodating.geodata.model.Country;

import java.util.List;
import java.util.function.Function;

public interface CountryService {

    <T> T getById(Long id, Function<Country, T> mappingFunc);

    <T> List<T> getAll(Function<Country, T> mappingFunc);

    Long create(Country country);

    Long update(Country country);

    Long delete(Country country);
}
