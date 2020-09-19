package com.gradel.parent.component.web.configuration;

import com.gradel.parent.component.web.entity.UException;
import com.gradel.parent.component.web.entity.UMessage;
import com.gradel.parent.component.web.service.PermissionsSecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 开放接口安全拦截器
 *
 * @author hechunzhan (hechunzhan@tcl.com)
 * @date 2017年8月31日 下午3:03:47
 */
public class SecurityServiceInterceptor extends HandlerInterceptorAdapter {
    /**
     * 安全验证权限服务
     */
    PermissionsSecurityService permissionsSecurityService;

    //apisix header token字段
    private static final String AUTHORIZATION = "Authorization";
    // 来源: web、mobile、social
    private static final String ORIGIN_TYPE = "origin";

    protected final Logger logger = LoggerFactory.getLogger(SecurityServiceInterceptor.class);
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpServletRequest req = ((HttpServletRequest) request);
        String token = req.getHeader(AUTHORIZATION);
        String origin = req.getHeader(ORIGIN_TYPE);
        //POST、GET、PUT OPTION 。。。
        String methos = req.getMethod().toUpperCase();
        String url = urlPathHelper.getLookupPathForRequest(request);

        Map<String, String> permissionsMap = new HashMap<>(8);

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        if ("web".equals(origin)) {
            permissionsMap = permissionsSecurityService.checkMobilePermissionsByAccessToken(token, methos, url);
        } else if ("mobile".equals(origin)) {
            permissionsMap = permissionsSecurityService.checkWebPermissionsByAccessToken(token, methos, url);
        } else if ("social".equals(origin)) {
            permissionsMap = permissionsSecurityService.checkSocialPermissionsByAccessToken(token, methos, url);
        } else {
            return false;
        }
        //获取token对应的refresh_token
        //根据refresh_token获取用户信息
        //将token和refresh_token缓存到本地，设置有效期半小时
        //获取refresh_token 对应的
        boolean privilege = false;
        if (permissionsMap.isEmpty()) {
            logger.debug(url);
            privilege = methos .equals(permissionsMap.get(url));
        }
        if (!privilege) {
            throw new UException(null, UMessage.UN_MATCH_STATUS);
        }
        return true;
    }

    private static final String voidResponse = "{}";

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (!response.isCommitted()) {
            // Used ModelAndView?
            if (modelAndView != null) {
                return;
            }
            // Access static resource?
            if (DefaultServletHttpRequestHandler.class == handler.getClass()) {
                return;
            }
            response.setStatus(200);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(voidResponse);
            response.flushBuffer();
            return;
        }
        super.postHandle(request, response, handler, modelAndView);
    }

}
