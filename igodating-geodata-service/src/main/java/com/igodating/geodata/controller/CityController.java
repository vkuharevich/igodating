package com.igodating.geodata.controller;

import com.igodating.geodata.dto.CityCreateRequest;
import com.igodating.geodata.dto.CityDeleteRequest;
import com.igodating.geodata.dto.CityUpdateRequest;
import com.igodating.geodata.dto.CityView;
import com.igodating.geodata.mapper.CityMapper;
import com.igodating.geodata.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/city")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Контроллер для справочника городов", description = "Контроллер, отвечающий за справочник городов")
public class CityController {

    private final CityService cityService;

    private final CityMapper cityMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Город", description = "Получение информации о городе по ID")
    public ResponseEntity<CityView> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cityService.getById(id, cityMapper::modelToView));
    }

    @PostMapping
    @Operation(summary = "Создание города", description = "Создание информации в справочнике о новом городе")
    public ResponseEntity<Long> createCity(@RequestBody CityCreateRequest cityCreateRequest) {
        return ResponseEntity.ok(cityService.create(cityCreateRequest, cityMapper::createRequestToModel));
    }

    @PutMapping
    @Operation(summary = "Обновление города", description = "Обновление информации в справочнике о городе")
    public ResponseEntity<Long> updateCity(@RequestBody CityUpdateRequest cityUpdateRequest) {
        return ResponseEntity.ok(cityService.update(cityUpdateRequest, cityMapper::updateRequestToModel));
    }

    @DeleteMapping
    @Operation(summary = "Удаление города", description = "Удаление информации в справочнике о городе")
    public ResponseEntity<Long> deleteCity(@RequestBody CityDeleteRequest cityDeleteRequest) {
        return ResponseEntity.ok(cityService.delete(cityDeleteRequest, cityMapper::deleteRequestToModel));
    }
}
