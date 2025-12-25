package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findAllByUserIdOrderByPriorityDescCreatedAtDesc(Long userId);
    Optional<WishlistItem> findByUserIdAndHotwheelId(Long userId, Long hotwheelId);
}

