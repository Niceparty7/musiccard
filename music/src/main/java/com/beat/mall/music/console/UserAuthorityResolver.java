package com.beat.mall.music.console;

import com.alibaba.fastjson.JSON;
import com.beat.mall.music.console.annotations.VerifiedUser;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
            HttpSession session = sRequest.getSession(false);
            if (session == null) {
                return null;
            }
            String signKey = SpringUtil.getProperty("application.session.key");
            Object value = session.getAttribute(signKey);
            if (!(value instanceof String sValue)) {
                return null;
            }
            return JSON.parseObject(sValue, User.class);
        }
        return new User().setId(1L);
    }
}

