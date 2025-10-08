package com.igodating.commons.security;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityRequestModifierImpl implements ConstraintValidator<SecurityRequestModify, SecurityRequestModifier> {

    @Override
    public boolean isValid(SecurityRequestModifier securityRequestModifier, ConstraintValidatorContext constraintValidatorContext) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .ifPresent(x -> {
                    if (x instanceof JwtUser user) {
                        securityRequestModifier.modifyRequest(user);
                    }
                });

        return true;
    }
}
