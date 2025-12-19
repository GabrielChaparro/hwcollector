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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired private ListingRepository listingRepo;
    @Autowired private UserRepository userRepo;

    @MessageMapping("/bid")
    @SendTo("/topic/listings/{listingId}")
    public BidEvent handleBid(@DestinationVariable String listingId, BidRequest bidRequest,
                              Principal principal) {

        // Validar listing y puja (lógica simplificada)
        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        User bidder = userRepo.findByKeycloakId(principal.getName()).orElseThrow();

        BidEvent event = new BidEvent();
        event.setListingId(listingId);
        event.setAmount(bidRequest.getAmount());
        event.setBidderName(bidder.getEmail());
        event.setTimestamp(LocalDateTime.now());

        // Notificar a todos los suscriptores
        messagingTemplate.convertAndSend("/topic/listings/" + listingId, event);

        return event;
    }
}

