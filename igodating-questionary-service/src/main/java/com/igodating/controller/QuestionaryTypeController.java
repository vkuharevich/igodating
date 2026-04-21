package com.igodating.controller;

import com.igodating.dto.questionarytype.create.QuestionaryTypeCreateDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeFullDto;
import com.igodating.dto.questionarytype.view.QuestionaryTypeShortDto;
import com.igodating.service.QuestionaryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questionary-type")
@RequiredArgsConstructor
public class QuestionaryTypeController {

    private final QuestionaryTypeService questionaryTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<QuestionaryTypeFullDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(questionaryTypeService.getType(id));
    }

    @GetMapping
    public ResponseEntity<List<QuestionaryTypeShortDto>> getAll() {
        return ResponseEntity.ok(questionaryTypeService.getAllTypes());
    }

    @PostMapping
    public ResponseEntity<QuestionaryTypeShortDto> create(@RequestBody QuestionaryTypeCreateDto questionaryTypeCreateDto) {
        return ResponseEntity.ok(questionaryTypeService.create(questionaryTypeCreateDto));
    }
}
