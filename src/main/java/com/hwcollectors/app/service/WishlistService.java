package com.hwcollectors.app.service;

import com.hwcollectors.app.dto.CreateWishlistItemRequest;
import com.hwcollectors.app.dto.WishlistItemDto;
import com.hwcollectors.app.model.User;
import com.hwcollectors.app.model.WishlistItem;
import com.hwcollectors.app.repository.HotWheelRepository;
import com.hwcollectors.app.repository.WishlistItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class WishlistService {

    private final WishlistItemRepository wishlistRepo;
    private final HotWheelRepository hotwheelRepo;

    public WishlistService(WishlistItemRepository wishlistRepo, HotWheelRepository hotwheelRepo) {
        this.wishlistRepo = wishlistRepo;
        this.hotwheelRepo = hotwheelRepo;
    }

    @Transactional(readOnly = true)
    public List<WishlistItemDto> list(Long userId) {
        return wishlistRepo.findAllByUserIdOrderByPriorityDescCreatedAtDesc(userId)
                .stream()
                .map(w -> new WishlistItemDto(
                        w.getId(),
                        w.getHotwheel().getCode(),
                        w.getHotwheel().getName(),
                        w.getPriority(),
                        w.getMaxPrice()
                ))
                .toList();
    }

    @Transactional
    public WishlistItemDto add(Long userId, CreateWishlistItemRequest req, User user) {
        if (req.hotwheelCode() == null || req.hotwheelCode().isBlank()) {
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY, "hotwheelCode required");
        }

        var hotwheel = hotwheelRepo.findByCode(req.hotwheelCode())
                .orElseThrow(() -> new ResponseStatusException(UNPROCESSABLE_ENTITY, "HOTWHEEL_NOT_FOUND"));

        wishlistRepo.findByUserIdAndHotwheelId(userId, hotwheel.getId())
                .ifPresent(x -> { throw new ResponseStatusException(CONFLICT, "WISHLIST_ITEM_EXISTS"); });

        var w = new WishlistItem();
        w.setUser(user);
        w.setHotwheel(hotwheel);
        w.setPriority(req.priority() == null ? 3 : req.priority());
        w.setMaxPrice(req.maxPrice());

        var saved = wishlistRepo.save(w);

        return new WishlistItemDto(
                saved.getId(),
                hotwheel.getCode(),
                hotwheel.getName(),
                saved.getPriority(),
                saved.getMaxPrice()
        );
    }

    @Transactional
    public void delete(Long userId, Long id) {
        var item = wishlistRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "WISHLIST_ITEM_NOT_FOUND"));

        if (!item.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "NOT_YOURS");
        }

        wishlistRepo.delete(item);
    }
}

