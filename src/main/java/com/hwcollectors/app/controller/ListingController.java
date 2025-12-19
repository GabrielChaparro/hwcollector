package com.hwcollectors.app.controller;

import com.hwcollectors.app.dto.BidEvent;
import com.hwcollectors.app.dto.BidRequest;
import com.hwcollectors.app.dto.CreateListingRequest;
import com.hwcollectors.app.model.Bid;
import com.hwcollectors.app.model.CollectionItem;
import com.hwcollectors.app.model.Listing;
import com.hwcollectors.app.model.ListingStatus;
import com.hwcollectors.app.model.ListingType;
import com.hwcollectors.app.model.User;
import com.hwcollectors.app.repository.CollectionItemRepository;
import com.hwcollectors.app.repository.HotWheelRepository;
import com.hwcollectors.app.repository.ListingRepository;
import com.hwcollectors.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingRepository listingRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CollectionItemRepository collectionRepo;
    @Autowired private HotWheelRepository hotwheelRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @PreAuthorize("hasRole('COLLECTOR')")
    public ResponseEntity<Listing> createListing(
            @RequestBody CreateListingRequest request, Authentication auth) {

        String keycloakId = auth.getName();
        User seller = userRepo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seller not found"));

        // Verificar que el vendedor tiene el item en colección
        CollectionItem item = collectionRepo.findByUserIdAndHotwheelId(seller.getId(), request.getHotwheelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item not in your collection"));

        Listing listing = new Listing();
        listing.setSellerId(seller.getId());
        listing.setHotwheelId(request.getHotwheelId());
        listing.setType(request.getType());
        listing.setPrice(request.getPrice());
        listing.setCurrentBid(request.getType() == ListingType.AUCTION ? request.getPrice() : null);
        listing.setStatus(ListingStatus.ACTIVE);

        if (request.getType() == ListingType.AUCTION) {
            listing.setEndDate(LocalDateTime.now().plusHours(request.getDurationHours()));
        }

        Listing saved = listingRepo.save(listing);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Listing>> getActiveListings(
            @RequestParam(required = false) ListingType type) {
        if (type != null) {
            return ResponseEntity.ok(listingRepo.findByStatusAndType(ListingStatus.ACTIVE, type));
        }
        return ResponseEntity.ok(listingRepo.findByStatus(ListingStatus.ACTIVE));
    }

    @PostMapping("/{id}/bid")
    @PreAuthorize("hasRole('COLLECTOR')")
    public ResponseEntity<Listing> placeBid(
            @PathVariable String id,
            @RequestBody BidRequest bidRequest,
            Authentication auth) {

        // Buscar listing
        Listing listing = listingRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        // Validaciones
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing is not active");
        }

        if (listing.getType() != ListingType.AUCTION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot bid on fixed price listing");
        }

        String bidderKeycloakId = auth.getName();
        User bidder = userRepo.findByKeycloakId(bidderKeycloakId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bidder not found"));

        // Validar puja mayor que la actual
        Double currentBid = listing.getCurrentBid() != null ? listing.getCurrentBid() : 0.0;
        if (bidRequest.getAmount() <= currentBid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bid must be higher than current bid: " + currentBid);
        }

        // Crear nueva puja
        Bid bid = new Bid();
        bid.setBidderId(bidder.getId());
        bid.setAmount(bidRequest.getAmount());
        listing.getBids().add(bid);

        // Actualizar listing
        listing.setCurrentBid(bidRequest.getAmount());
        listing.setHighestBidderId(bidder.getId());

        // Guardar cambios
        Listing updatedListing = listingRepo.save(listing);

        // Enviar notificación WebSocket
        BidEvent event = new BidEvent();
        event.setListingId(id);
        event.setAmount(bidRequest.getAmount());
        event.setBidderName(bidder.getEmail());
        event.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/listings/" + id, event);

        return ResponseEntity.ok(updatedListing);
    }

    @PostMapping("/{id}/buy")
    @PreAuthorize("hasRole('COLLECTOR')")
    public ResponseEntity<String> buyFixedPrice(
            @PathVariable String id, Authentication auth) {

        Listing listing = listingRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (listing.getType() != ListingType.FIXED || listing.getStatus() != ListingStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid listing");
        }

        String buyerKeycloakId = auth.getName();
        User buyer = userRepo.findByKeycloakId(buyerKeycloakId).orElseThrow();

        if (buyer.getBalance() < listing.getPrice()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        // Transferencia inmediata (lógica similar a closeAuction)
        // ...

        return ResponseEntity.ok("Purchase completed successfully");
    }
}


