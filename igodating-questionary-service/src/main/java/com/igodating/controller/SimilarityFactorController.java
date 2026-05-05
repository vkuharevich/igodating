package com.igodating.controller;

import com.igodating.dto.similarityfactor.SimilarityFactorDto;
import com.igodating.service.SimilarityFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/similarity-factor")
@RequiredArgsConstructor
public class SimilarityFactorController {

    private final SimilarityFactorService service;

    @GetMapping
    public ResponseEntity<List<SimilarityFactorDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
