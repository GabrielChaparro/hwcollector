package com.hwcollectors.app.service;

import com.hwcollectors.app.dto.AuctionClosedEvent;
import com.hwcollectors.app.model.*;
import com.hwcollectors.app.repository.CollectionItemRepository;
import com.hwcollectors.app.repository.ListingRepository;
import com.hwcollectors.app.repository.UserRepository;
import com.hwcollectors.app.repository.HotWheelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {

    @Autowired private ListingRepository listingRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CollectionItemRepository collectionRepo;
    @Autowired private HotWheelRepository hotwheelRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void checkExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();

        // ✅ CORREGIR: Método correcto JPA
        List<Listing> expiredAuctions = listingRepo.findExpiredAuctions(
                ListingStatus.ACTIVE, now);

        for (Listing listing : expiredAuctions) {
            closeAuction(listing);
        }
    }

    @Transactional
    public void closeAuction(Listing listing) {
        if (listing.getHighestBidder() != null && listing.getCurrentBid() != null) {
            // Transferir dinero
            User seller = listing.getSeller();  // ✅ Relación JPA directa
            User buyer = listing.getHighestBidder();  // ✅ Relación JPA directa

            seller.setBalance(seller.getBalance() + listing.getCurrentBid());
            buyer.setBalance(buyer.getBalance() - listing.getCurrentBid());

            // Cambiar estado
            listing.setStatus(ListingStatus.SOLD);

            // Mover item de colección vendedor a comprador
            moveItemOwnership(listing.getHotwheel(), seller.getId(), buyer.getId());

            userRepo.save(seller);
            userRepo.save(buyer);
            listingRepo.save(listing);

            // Notificar
            sendAuctionClosedNotification(listing);
        } else {
            // Sin pujas - cancelar
            listing.setStatus(ListingStatus.CANCELLED);
            listingRepo.save(listing);
        }
    }

    private void moveItemOwnership(HotWheel hotwheel, Long fromUserId, Long toUserId) {
        // Eliminar item de vendedor
        collectionRepo.findByUserIdAndHotwheelId(fromUserId, hotwheel.getId())
                .ifPresent(collectionRepo::delete);

        // Crear item para comprador
        User buyer = userRepo.findById(toUserId).orElseThrow();
        CollectionItem newItem = new CollectionItem();
        newItem.setUser(buyer);
        newItem.setHotwheel(hotwheel);
        newItem.setCondition("TRANSFERRED");
        newItem.setAcquiredDate(LocalDate.now());
        collectionRepo.save(newItem);
    }

    private void sendAuctionClosedNotification(Listing listing) {
        AuctionClosedEvent event = new AuctionClosedEvent();
        event.setListingId(listing.getId().toString());  // ✅ Long → String
        event.setWinner(listing.getHighestBidder().getEmail());  // ✅ Email del User
        event.setFinalPrice(listing.getCurrentBid());
        event.setClosedAt(LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/auctions/closed", event);
    }
}
