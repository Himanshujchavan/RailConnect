package com.railconnect.security;

import com.railconnect.auth.repository.UserRepository;
import com.railconnect.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtil {

    private final UserRepository userRepository;

    public SecurityUtil(@Qualifier("authUserRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Extracts the username (or email) from the current SecurityContext.
     */
    public Optional<String> getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return Optional.ofNullable(auth.getName());
    }

    /**
     * Checks if the currently authenticated user possesses ROLE_ADMIN authority.
     */
    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Retrieves the current User entity from the database.
     */
    public User getCurrentAuthenticatedUser() {
        String username = getCurrentUsername()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated to perform this operation."));
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new AccessDeniedException("Authenticated user could not be found."));
    }

    /**
     * Verifies that the currently authenticated user is either the resource owner or an administrator.
     * Throws {@link AccessDeniedException} if unauthorized.
     *
     * @param resourceOwnerUserId the ID of the user who owns the target resource
     * @param actionDescription   a short description of the attempted action for log/error clarity
     */
    public void verifyUserOwnershipOrAdmin(Long resourceOwnerUserId, String actionDescription) {
        if (isCurrentUserAdmin()) {
            return;
        }
        User currentUser = getCurrentAuthenticatedUser();
        if (resourceOwnerUserId == null || !resourceOwnerUserId.equals(currentUser.id)) {
            throw new AccessDeniedException("Access denied: you do not have permission to " + actionDescription + ".");
        }
    }
}
