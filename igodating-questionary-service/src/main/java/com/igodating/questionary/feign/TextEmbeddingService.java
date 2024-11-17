package com.igodating.questionary.feign;

import com.igodating.questionary.dto.textembedding.TextEmbeddingRequest;
import com.igodating.questionary.dto.textembedding.TextEmbeddingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "textEmbeddingService")
public interface TextEmbeddingService {

    @PostMapping("/embeddings")
    TextEmbeddingResponse getEmbeddings(TextEmbeddingRequest request);
}
