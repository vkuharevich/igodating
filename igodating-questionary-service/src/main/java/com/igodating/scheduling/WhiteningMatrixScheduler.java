package com.igodating.scheduling;

import com.igodating.service.vector.WhiteningMatrixService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class WhiteningMatrixScheduler {

    private final WhiteningMatrixService whiteningMatrixService;

    @Scheduled(fixedDelayString = "${scheduling.whitening-matrix-delay}")
    public void schedule() {
        log.debug("Обновление whitening");
        whiteningMatrixService.refreshWhiteningMatrix();
    }
}
