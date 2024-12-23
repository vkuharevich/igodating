package com.igodating.geodata.controller;

import com.igodating.geodata.dto.RegionCreateRequest;
import com.igodating.geodata.dto.RegionDeleteRequest;
import com.igodating.geodata.dto.RegionUpdateRequest;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.mapper.RegionMapper;
import com.igodating.geodata.service.RegionService;
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
public class RegionController {

    private final RegionService regionService;

    private final RegionMapper regionMapper;

    @MutationMapping
    public Long createRegion(@Argument RegionCreateRequest region) {
        return regionService.create(regionMapper.createRequestToModel(region));
    }

    @MutationMapping
    public Long updateRegion(@Argument RegionUpdateRequest region) {
        return regionService.update(regionMapper.updateRequestToModel(region));
    }

    @MutationMapping
    public Long deleteRegion(@Argument RegionDeleteRequest region) {
        return regionService.delete(regionMapper.deleteRequestToModel(region));
    }

    @QueryMapping
    public List<RegionView> allRegions() {
        return regionService.getAll(regionMapper::modelToView);
    }
}
