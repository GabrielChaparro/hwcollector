package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {
    List<CollectionItem> findByUserId(Long userId);
    Optional<CollectionItem> findByUserIdAndHotwheelId(Long userId, Long hotwheelId);
}


