package com.niqdev.authserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class ConsentController {

    @GetMapping("/consent")
    public String consent(@RequestParam("client_id") String clientId,
                         @RequestParam("state") String state,
                         @RequestParam("scope") String scope,
                         HttpServletRequest request,
                         Model model) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principal = authentication.getName();
        
        // 將 scope 字串分割成列表
        List<String> scopes = Arrays.asList(scope.split(" "));
        
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", scopes);
        model.addAttribute("scopeString", scope);
        model.addAttribute("principal", principal);
        
        // 添加其他可能的參數
        String responseType = request.getParameter("response_type");
        String redirectUri = request.getParameter("redirect_uri");
        
        if (responseType != null) {
            model.addAttribute("responseType", responseType);
        }
        if (redirectUri != null) {
            model.addAttribute("redirectUri", redirectUri);
        }
        
        return "consent";
    }
}
