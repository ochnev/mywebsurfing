package com.mywebsurfing.controller;

import com.mywebsurfing.entity.AppUser;
import com.mywebsurfing.entity.Realm;
import com.mywebsurfing.service.HomePageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomePageController extends BaseController {

    @Autowired
    private HomePageService homePageService;

    @GetMapping("/home")
    public List<Realm> home() {
        // TODO: do a more appropriate thing to get the current user
        final AppUser user = getOrCreateUser();
        return homePageService.getRealms(user);
    }

}
