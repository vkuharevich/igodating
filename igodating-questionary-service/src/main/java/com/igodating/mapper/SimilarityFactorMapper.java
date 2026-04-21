package com.igodating.mapper;

import com.igodating.dto.similarityfactor.SimilarityFactorDto;
import com.igodating.model.SimilarityFactor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SimilarityFactorMapper {

    SimilarityFactorDto modelToDto(SimilarityFactor similarityFactor);
}
