package com.igodating.commons.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaginationRequest {

    @Min(1)
    @Schema(example = "1", description = "Page number")
    @Parameter(example = "1", description = "Page number")
    private int num;

    @Min(1)
    @Schema(example = "10", description = "Page size")
    @Parameter(example = "10", description = "Page size")
    private int size;
}
