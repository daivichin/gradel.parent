package com.gradel.parent.component.web.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

public class OrderItemArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> c = methodParameter.getParameterType();
        return c == UOrderList.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        Assert.notNull(request, "request is null");
        String[] orders = request.getParameterValues("pageOrder");

        UOrderList list = new UOrderList();
        if (null != orders && orders.length > 0) {
            Arrays.stream(orders).forEach(o -> list.add(UOrder.build(o)));
        }
        return list;
    }
}
