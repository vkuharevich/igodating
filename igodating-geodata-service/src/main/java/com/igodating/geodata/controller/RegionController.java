package com.igodating.geodata.controller;

import com.igodating.geodata.dto.RegionCreateRequest;
import com.igodating.geodata.dto.RegionDeleteRequest;
import com.igodating.geodata.dto.RegionUpdateRequest;
import com.igodating.geodata.dto.RegionView;
import com.igodating.geodata.mapper.RegionMapper;
import com.igodating.geodata.service.RegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
@Slf4j
public class RegionController {

    private final RegionService regionService;

    private final RegionMapper regionMapper;

    @GetMapping("/{id}")
    public ResponseEntity<RegionView> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(regionService.getById(id, regionMapper::modelToView));
    }

    @PostMapping
    public ResponseEntity<Long> createRegion(@RequestBody RegionCreateRequest regionCreateRequest) {
        return ResponseEntity.ok(regionService.create(regionCreateRequest, regionMapper::createRequestToModel));
    }

    @PutMapping
    public ResponseEntity<Long> updateRegion(@RequestBody RegionUpdateRequest regionUpdateRequest) {
        return ResponseEntity.ok(regionService.update(regionUpdateRequest, regionMapper::updateRequestToModel));
    }

    @DeleteMapping
    public ResponseEntity<Long> deleteRegion(@RequestBody RegionDeleteRequest regionDeleteRequest) {
        return ResponseEntity.ok(regionService.delete(regionDeleteRequest, regionMapper::deleteRequestToModel));
    }
}
