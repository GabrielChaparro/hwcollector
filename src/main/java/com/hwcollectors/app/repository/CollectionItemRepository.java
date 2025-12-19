package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.CollectionItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionItemRepository extends MongoRepository<CollectionItem, String> {
    List<CollectionItem> findByUserId(String userId);

    Optional<CollectionItem> findByUserIdAndHotwheelId(String userId, String hotwheelId);
}

