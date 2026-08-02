package com.beat.mall.console;

import com.alibaba.fastjson.JSON;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.module.user.service.BaseUserService;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.SignUtil;
import com.beat.mall.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class UserAuthorityResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private BaseUserService userService;
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
            String isAppS = SpringUtil.getProperty("application.isapp");
            boolean isApp = isAppS.equals("1") ? true : false;
            HttpServletRequest sRequest = (HttpServletRequest) request.getNativeRequest();
            if (isApp) {
                String signKey = SpringUtil.getProperty("application.sign.key");
                String sign = sRequest.getHeader(signKey);
                if (!BaseUtil.isEmpty(sign)) {
                    Long userId = SignUtil.parseSign(sign);
                    log.info("userId: {}, sign: {}", userId, sign);
                    if (!BaseUtil.isEmpty(userId)) {
                        return userService.getById(userId);
                    }
                }
                return null;
            } else {
                HttpSession session = sRequest.getSession(false);
                if (BaseUtil.isEmpty(session)) {
                    return null;
                }
                String signKey = SpringUtil.getProperty("application.session.key");
                Object value = session.getAttribute(signKey);
                if (value == null) {
                    return null;
                }

                String sValue = (String) value;
                return JSON.parseObject(sValue, User.class);
            }
        }
        return userService.getById(Long.valueOf(1));
    }
}
