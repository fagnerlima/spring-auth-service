package br.pro.fagnerlima.spring.auth.api.application.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.pro.fagnerlima.spring.auth.api.infrastructure.security.service.OAuth2SecurityService;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @Autowired
    private OAuth2SecurityService oauth2SecurityService;

    @DeleteMapping("/revoke")
    public void revoke(HttpServletRequest request, HttpServletResponse response) {
        oauth2SecurityService.removeCookieRefreshToken(request, response);
    }

}
