package com.study.api.config.resolver;

import com.study.api.config.annotation.AuthenticatedUserId;
import com.study.common.exception.user.NotSignedInException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasParameterAnnotations = parameter.hasParameterAnnotation(AuthenticatedUserId.class);
        boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());
        return hasParameterAnnotations && hasStringType;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        /**
         * 사용자가 로그인하지 않거나 인증되지 않은 상태에서는 Spring Security는 Authentication 객체를 anonymousUser로 설정합니다.
         * 이 객체의 getPrincipal() 메서드는 "anonymousUser" 문자열을 반환합니다.
         */
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            throw new NotSignedInException("anonymousUser");
        }

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // getPrincipal()의 반환 타입이 Object이므로, 실제 사용하는 타입인 UserDetails로 캐스팅하여 사용 (일반적으로 UserDetails로 인식하면됨)
        // 인증된 사용자에 대한 더 많은 세부 정보를 얻기 위해 캐스팅을 통해 접근
        return userDetails.getUsername();
    }
}
