package com.hwcollectors.app.controller;

import com.hwcollectors.app.dto.AddItemRequest;
import com.hwcollectors.app.model.CollectionItem;
import com.hwcollectors.app.model.HotWheel;
import com.hwcollectors.app.model.User;
import com.hwcollectors.app.repository.CollectionItemRepository;
import com.hwcollectors.app.repository.HotWheelRepository;
import com.hwcollectors.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private CollectionItemRepository collectionRepo;
    @Autowired private HotWheelRepository hotwheelRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/my")
    @PreAuthorize("hasRole('COLLECTOR')")
    public ResponseEntity<List<CollectionItem>> getMyCollection(Authentication auth) {
        String keycloakId = auth.getName();
        User user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<CollectionItem> items = collectionRepo.findByUserId(user.getId());
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('COLLECTOR')")
    public ResponseEntity<CollectionItem> addToCollection(
            @RequestBody AddItemRequest request, Authentication auth) {

        String keycloakId = auth.getName();
        User user = userRepo.findByKeycloakId(keycloakId).orElseThrow();
        HotWheel hotwheel = hotwheelRepo.findByCode(request.getHotwheelCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HotWheel not found"));

        CollectionItem item = new CollectionItem();
        item.setUserId(user.getId());
        item.setHotwheelId(hotwheel.getId());
        item.setCondition(request.getCondition());
        item.setAcquiredDate(LocalDate.now());

        CollectionItem saved = collectionRepo.save(item);
        return ResponseEntity.ok(saved);
    }
}

