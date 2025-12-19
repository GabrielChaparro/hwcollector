package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.Listing;
import com.hwcollectors.app.model.ListingStatus;
import com.hwcollectors.app.model.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByStatusAndType(ListingStatus status, ListingType type);
    List<Listing> findBySellerIdAndStatus(Long sellerId, ListingStatus status);
    List<Listing> findByStatus(ListingStatus status);
    // ¡SUBASTAS EXPIRADAS NATURALES!
    @Query("SELECT l FROM Listing l WHERE l.status = :status AND l.endDate < :now")
    List<Listing> findExpiredAuctions(@Param("status") ListingStatus status,
                                      @Param("now") LocalDateTime now);
}


