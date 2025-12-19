package com.hwcollectors.app.service;

import com.hwcollectors.app.dto.AuctionClosedEvent;
import com.hwcollectors.app.model.Listing;
import com.hwcollectors.app.model.ListingStatus;
import com.hwcollectors.app.model.User;
import com.hwcollectors.app.repository.CollectionItemRepository;
import com.hwcollectors.app.repository.ListingRepository;
import com.hwcollectors.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {

    @Autowired
    private ListingRepository listingRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private CollectionItemRepository collectionRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 30000) // Cada 30 segundos
    public void checkExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();

        List<Listing> expiredAuctions = listingRepo.findByStatusAndEndDateBefore(
                ListingStatus.ACTIVE, now);

        for (Listing listing : expiredAuctions) {
            closeAuction(listing);
        }
    }

    @Transactional
    public void closeAuction(Listing listing) {
        if (listing.getHighestBidderId() != null && listing.getCurrentBid() != null) {
            // Transferir dinero
            User seller = userRepo.findById(listing.getSellerId()).orElseThrow();
            User buyer = userRepo.findById(listing.getHighestBidderId()).orElseThrow();

            seller.setBalance(seller.getBalance() + listing.getCurrentBid());
            buyer.setBalance(buyer.getBalance() - listing.getCurrentBid());

            // Cambiar estado
            listing.setStatus(ListingStatus.SOLD);

            // Mover item de colección vendedor a comprador
            moveItemOwnership(listing.getHotwheelId(), listing.getSellerId(), listing.getHighestBidderId());

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

    private void moveItemOwnership(String hotwheelId, String fromUserId, String toUserId) {
        // Lógica para transferir item de colección
    }

    private void sendAuctionClosedNotification(Listing listing) {
        AuctionClosedEvent event = new AuctionClosedEvent();
        event.setListingId(listing.getId());
        event.setWinner(listing.getHighestBidderId());
        event.setFinalPrice(listing.getCurrentBid());
        messagingTemplate.convertAndSend("/topic/auctions/closed", event);
    }
}

