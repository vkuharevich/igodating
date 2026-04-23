package com.igodating.service.impl;

import com.igodating.dto.similarityfactor.SimilarityFactorDto;
import com.igodating.mapper.SimilarityFactorMapper;
import com.igodating.repository.SimilarityFactorRepository;
import com.igodating.service.SimilarityFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarityFactorServiceImpl implements SimilarityFactorService {

    private final SimilarityFactorMapper similarityFactorMapper;

    private final SimilarityFactorRepository similarityFactorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SimilarityFactorDto> getAll() {
        return similarityFactorRepository.findAll().stream().map(similarityFactorMapper::modelToDto).toList();
    }
}
