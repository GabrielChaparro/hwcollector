//package com.hwcollectors.app.controller;
//
//import com.hwcollectors.app.dto.CreateWishlistItemRequest;
//import com.hwcollectors.app.dto.WishlistItemDto;
//import com.hwcollectors.app.service.WishlistService;
//import com.hwcollectors.app.utils.UserUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.http.HttpStatus;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/wishlist")
//public class WishlistController {
//
//    private final WishlistService wishlistService;
//    private final UserUtils userService; // o lo que uses para obtener current user
//
//    public WishlistController(WishlistService wishlistService, UserUtils userService) {
//        this.wishlistService = wishlistService;
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public List<WishlistItemDto> list() {
//        String user = userService.getUserId();
//        return wishlistService.list(Long.valueOf(user));
//    }
//
//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public WishlistItemDto add(@RequestBody CreateWishlistItemRequest req) {
//        var user = userService.getCurrentUser();
//        return wishlistService.add(user.getId(), req, user);
//    }
//
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable Long id) {
//        var user = userService.getCurrentUser();
//        wishlistService.delete(user.getId(), id);
//    }
//}
//
