package com.igodating.questionary.feign;

import com.igodating.questionary.dto.TextEmbeddingRequest;
import com.igodating.questionary.dto.TextEmbeddingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "textEmbeddingService")
public interface TextEmbeddingService {

    @PostMapping("/embeddings")
    TextEmbeddingResponse getEmbeddings(TextEmbeddingRequest request);
}
