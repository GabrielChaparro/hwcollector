package com.hwcollectors.app.service;
import com.hwcollectors.app.dto.CollectionItemDto;
import com.hwcollectors.app.dto.UpdateCollectionItemRequest;
import com.hwcollectors.app.model.Availability;
import com.hwcollectors.app.model.Visibility;
import com.hwcollectors.app.model.CollectionItem;
import com.hwcollectors.app.model.User;
import com.hwcollectors.app.repository.CollectionItemRepository;
import com.hwcollectors.app.repository.UserRepository;
import com.hwcollectors.app.utils.CollectionItemMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class CollectionService {

    private final UserRepository userRepo;
    private final CollectionItemRepository collectionRepo;
    private final CollectionItemMapper collectionItemMapper;

    public CollectionService(UserRepository userRepo,
                             CollectionItemRepository collectionRepo,
                             CollectionItemMapper collectionItemMapper) {
        this.userRepo = userRepo;
        this.collectionRepo = collectionRepo;
        this.collectionItemMapper = collectionItemMapper;
    }

    @Transactional
    public CollectionItemDto updateItem(String keycloakId, Long itemId, UpdateCollectionItemRequest req) {

        User user = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CollectionItem item = collectionRepo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "COLLECTION_ITEM_NOT_FOUND"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_YOURS");
        }

        // availability (record accessor: req.availability())
        if (req.availability() != null) {
            item.setAvailability(parseAvailability(req.availability()));
        }

        // visibility
        if (req.visibility() != null) {
            item.setVisibility(parseVisibility(req.visibility()));
        }

        // askPrice
        if (req.askPrice() != null) {
            validateAskPrice(req.askPrice());
            item.setAskPrice(req.askPrice());
        }

        // Reglas de negocio recomendadas:
        // 1) Si NO está a la venta, limpiamos askPrice
        if (item.getAvailability() == Availability.NOT_FOR_SALE) {
            item.setAskPrice(null);
        }

        // 2) Si está disponible (FOR_SALE/ASK_ME) debe ser PUBLIC (si no, no sirve para matching)
        if (item.getAvailability() != Availability.NOT_FOR_SALE && item.getVisibility() != Visibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PUBLIC_VISIBILITY_REQUIRED");
        }

        CollectionItem saved = collectionRepo.save(item);
        return collectionItemMapper.toDto(saved);
    }

    private Availability parseAvailability(String value) {
        try {
            return Availability.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_AVAILABILITY");
        }
    }

    private Visibility parseVisibility(String value) {
        try {
            return Visibility.valueOf(value.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_VISIBILITY");
        }
    }

    private void validateAskPrice(BigDecimal price) {
        if (price.signum() < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_PRICE");
        }
    }
}
