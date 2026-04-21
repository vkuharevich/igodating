package com.igodating.scheduling;

import com.igodating.service.vector.VectorInitializationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class VectorInitializingScheduler {

    private final VectorInitializationService vectorInitializationService;

    @Value("${scheduling.vector_init_limit}")
    private int vectorInitLimit;

    @Scheduled(fixedDelayString = "${scheduling.vector-init-delay}")
    public void schedule() {
        for (int i = 1; i <= vectorInitLimit; i++) {
            log.debug("Инициализация нового вектора");
            if (!vectorInitializationService.initNewVector()) {
                break;
            }
        }
    }
}
