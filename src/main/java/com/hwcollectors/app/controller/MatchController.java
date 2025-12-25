//package com.hwcollectors.app.controller;
//
//import com.hwcollectors.app.dto.MatchDto;
//import com.hwcollectors.app.service.MatchService;
//import com.hwcollectors.app.utils.UserUtils;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class MatchController {
//
//    private final MatchService matchService;
//    private final UserUtils userService;
//
//    public MatchController(MatchService matchService, UserUtils userService) {
//        this.matchService = matchService;
//        this.userService = userService;
//    }
//
//    @GetMapping("/matches")
//    public List<MatchDto> matches(@RequestParam String code, Authentication auth) {
//        String user = userService.getUserId(auth);
//        return matchService.matches(user, code);
//    }
//}
//
