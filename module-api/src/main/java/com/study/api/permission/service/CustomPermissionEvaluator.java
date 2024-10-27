package com.study.api.permission.service;


import com.study.domain.permission.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final PermissionService permissionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (!(targetDomainObject instanceof Long) || !(permission instanceof UserRole)) {
            return false;
        }
        String userId = authentication.getName();
        Long blogId = (Long) targetDomainObject;
        UserRole requiredUserRole = (UserRole) permission;

        UserRole userRole = permissionService.getUserRoleForBlog(blogId, userId);
        return userRole.equals(requiredUserRole);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
