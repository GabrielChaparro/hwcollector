package com.hwcollectors.app.repository;

import com.hwcollectors.app.model.Listing;
import com.hwcollectors.app.model.ListingStatus;
import com.hwcollectors.app.model.ListingType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingRepository extends MongoRepository<Listing, String> {
    List<Listing> findByStatusAndType(ListingStatus status, ListingType type);
    List<Listing> findBySellerIdAndStatus(String sellerId, ListingStatus status);
    List<Listing> findByStatus(ListingStatus status);
    Optional<Listing> findByIdAndStatus(String id, ListingStatus status);

    List<Listing> findByStatusAndEndDateBefore(ListingStatus listingStatus, LocalDateTime endDateBefore);
}

