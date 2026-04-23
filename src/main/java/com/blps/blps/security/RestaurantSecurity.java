package com.blps.blps.security;

import com.blps.blps.security.service.XmlUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("restaurantSecurity")
@RequiredArgsConstructor
public class RestaurantSecurity {

    private final XmlUserDetailsService xmlUserDetailsService;

    public boolean isRestaurantMatch(Long restaurantId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String username = authentication.getName();
        Long userReferenceId = xmlUserDetailsService.getReferenceId(username);
        return userReferenceId != null && userReferenceId.equals(restaurantId);
    }
}