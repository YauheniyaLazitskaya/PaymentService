package com.payment.services;

import com.common.dto.userDto.UserDataDTO;
import com.payment.entities.Payment;
import com.payment.exceptions.PaymentException;
import com.payment.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityCheck")
@RequiredArgsConstructor
public class SecurityCheckService {
    private final PaymentRepository paymentRepository;
    private final UserIntegrationService userIntegrationService;

    public boolean isUserOwner(Integer requestedUserId, Authentication authentication) {
        if (!isAuthenticated(authentication)) return false;

        Integer tokenUserId = extractAppUserId(authentication);
        return tokenUserId != null && tokenUserId.equals(requestedUserId);
    }

    @Transactional(readOnly = true)
    public boolean isPaymentOwner(Integer paymentId, Authentication authentication) {
        if (!isAuthenticated(authentication)) return false;

        Integer tokenUserId = extractAppUserId(authentication);
        if (tokenUserId == null) return false;

        return paymentRepository.findById(Integer.valueOf(paymentId))
                .map(payment -> payment.getUserId().equals(tokenUserId))
                .orElse(false);
    }

    public boolean isAdmin(Authentication authentication) {
        if (!isAuthenticated(authentication)) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private Integer extractAppUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String uuid = jwt.getClaimAsString("sub");
            try {
                UserDataDTO user = userIntegrationService.getUserByKeycloakId(uuid);
                return user != null ? user.getId() : null;
            } catch (Exception e) {
                throw new PaymentException("User id not found by keycloakId.");
            }
        }
        return null;
    }
}
