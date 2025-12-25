package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.CollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionItemRepository extends JpaRepository<CollectionItem, Long> {
    List<CollectionItem> findByUserId(Long userId);
    Optional<CollectionItem> findByUserIdAndHotwheelId(Long userId, Long hotwheelId);

    @Query("""
        select ci
        from CollectionItem ci
        where ci.hotwheel.code = :code
          and ci.visibility = com.hwcollectors.app.model.Visibility.PUBLIC
          and ci.availability <> com.hwcollectors.app.model.Availability.NOT_FOR_SALE
          and ci.user.id <> :currentUserId
    """)
    List<CollectionItem> findMatches(@Param("code") String code, @Param("currentUserId") Long currentUserId);
}


