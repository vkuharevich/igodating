package com.igodating.geodata.controller;

import com.igodating.geodata.dto.CityCreateRequest;
import com.igodating.geodata.dto.CityDeleteRequest;
import com.igodating.geodata.dto.CityUpdateRequest;
import com.igodating.geodata.dto.CityView;
import com.igodating.geodata.mapper.CityMapper;
import com.igodating.geodata.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CityController {

    private final CityService cityService;

    private final CityMapper cityMapper;

    @MutationMapping
    public Long createCity(@Argument CityCreateRequest city) {
        return cityService.create(cityMapper.createRequestToModel(city));
    }

    @MutationMapping
    public Long updateCity(@Argument CityUpdateRequest city) {
        return cityService.update(cityMapper.updateRequestToModel(city));
    }

    @MutationMapping
    public Long deleteCity(@Argument CityDeleteRequest city) {
        return cityService.delete(cityMapper.deleteRequestToModel(city));
    }

    @QueryMapping
    public List<CityView> allCities() {
        return cityService.getAll(cityMapper::modelToView);
    }
}
