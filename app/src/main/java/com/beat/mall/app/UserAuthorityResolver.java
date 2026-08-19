package com.beat.mall.app;

import com.beat.mall.app.annotations.VerifiedUser;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.SignUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class UserAuthorityResolver implements HandlerMethodArgumentResolver {
    private boolean isCheckAuthority;

    public UserAuthorityResolver(ApplicationArguments appArguments) {
        String[] arguments = appArguments.getSourceArgs();
        if (arguments == null || arguments.length <= 3) {
            isCheckAuthority = true;
            return;
        }
        String isMockUserLogin = arguments[2];
        if (BaseUtil.isEmpty(isMockUserLogin)) {
            isCheckAuthority = true;
        } else {
            isCheckAuthority = Boolean.parseBoolean(isMockUserLogin);
        }
        log.info("Check user authority: {}", Boolean.toString(isCheckAuthority));
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> type = parameter.getParameterType();
        return type.isAssignableFrom(User.class) && parameter.hasParameterAnnotation(VerifiedUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer container,
                                  NativeWebRequest request,
                                  WebDataBinderFactory factory) {
        if (isCheckAuthority) {
            HttpServletRequest sRequest = (HttpServletRequest) request.getNativeRequest();
            String sign = sRequest.getHeader("sign");
            if (BaseUtil.isEmpty(sign)) {
                sign = sRequest.getParameter("sign");
            }
            Long userId = SignUtil.parseSign(sign);
            if (userId == null) {
                return null;
            }
            return new User().setId(userId);
        }
        return new User().setId(1L);
    }
}
