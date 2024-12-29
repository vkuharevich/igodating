package com.igodating.geodata.controller;

import com.igodating.geodata.dto.CountryCreateRequest;
import com.igodating.geodata.dto.CountryDeleteRequest;
import com.igodating.geodata.dto.CountryUpdateRequest;
import com.igodating.geodata.dto.CountryView;
import com.igodating.geodata.mapper.CountryMapper;
import com.igodating.geodata.service.CountryService;
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
@RequestMapping("/api/country")
@RequiredArgsConstructor
@Slf4j
public class CountryController {

    private final CountryService countryService;

    private final CountryMapper countryMapper;

    @GetMapping("/{id}")
    public ResponseEntity<CountryView> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(countryService.getById(id, countryMapper::modelToView));
    }

    @PostMapping
    public Long createCountry(@RequestBody CountryCreateRequest countryCreateRequest) {
        return countryService.create(countryCreateRequest, countryMapper::createRequestToModel);
    }

    @PutMapping
    public Long updateCountry(@RequestBody CountryUpdateRequest countryUpdateRequest) {
        return countryService.update(countryUpdateRequest, countryMapper::updateRequestToModel);
    }

    @DeleteMapping
    public Long deleteCountry(@RequestBody CountryDeleteRequest countryDeleteRequest) {
        return countryService.delete(countryDeleteRequest, countryMapper::deleteRequestToModel);
    }
}
