package com.hwcollectors.app.controller;

import com.hwcollectors.app.dto.BidEvent;
import com.hwcollectors.app.dto.BidRequest;
import com.hwcollectors.app.model.Listing;
import com.hwcollectors.app.model.User;
import com.hwcollectors.app.repository.ListingRepository;
import com.hwcollectors.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class BidWebSocketController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private ListingRepository listingRepo;
    @Autowired private UserRepository userRepo;

    @MessageMapping("/bid/{listingId}")  // ← CAMBIAR: Path con parámetro
    @SendTo("/topic/listings/{listingId}")  // ← SendTo con parámetro
    public BidEvent handleBid(
            @DestinationVariable Long listingId,  // ← Long para PostgreSQL
            BidRequest bidRequest,
            Principal principal) {

        // ✅ CORREGIR: Long listingId para PostgreSQL
        Listing listing = listingRepo.findById(listingId)  // ← findById(Long)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        User bidder = userRepo.findByKeycloakId(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bidder not found"));

        BidEvent event = new BidEvent();
        event.setListingId(listingId.toString());  // ← Long → String para JSON
        event.setAmount(bidRequest.getAmount());
        event.setBidderName(bidder.getEmail());
        event.setTimestamp(LocalDateTime.now());

        // Notificar a todos los suscriptores
        messagingTemplate.convertAndSend("/topic/listings/" + listingId, event);

        return event;
    }
}
