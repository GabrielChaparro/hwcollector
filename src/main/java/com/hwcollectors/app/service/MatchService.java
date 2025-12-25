package com.hwcollectors.app.service;

import com.hwcollectors.app.dto.MatchDto;
import com.hwcollectors.app.repository.CollectionItemRepository;
import com.hwcollectors.app.repository.HotWheelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
import java.util.List;

@Service
public class MatchService {

    private final HotWheelRepository hotwheelRepo;
    private final CollectionItemRepository collectionRepo;

    public MatchService(HotWheelRepository hotwheelRepo, CollectionItemRepository collectionRepo) {
        this.hotwheelRepo = hotwheelRepo;
        this.collectionRepo = collectionRepo;
    }

    @Transactional(readOnly = true)
    public List<MatchDto> matches(Long currentUserId, String code) {
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "code required");
        }

        // valida que exista en catálogo
        hotwheelRepo.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(UNPROCESSABLE_ENTITY, "HOTWHEEL_NOT_FOUND"));

        return collectionRepo.findMatches(code, currentUserId).stream()
                .map(ci -> new MatchDto(
                        // temporal alias:
                        ci.getUser().getEmail().split("@")[0],
                        ci.getCondition(),
                        ci.getAvailability().name(),
                        ci.getAskPrice()
                ))
                .toList();
    }
}

