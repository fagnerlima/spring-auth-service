package br.pro.fagnerlima.spring.auth.api.infrastructure.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.OAuth2Properties;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OAuth2SecurityFilter implements Filter {

    private static final String TOKEN_RESOURCE = "/oauth/token";
    private static final String GRANT_TYPE_PARAMETER = "grant_type";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private OAuth2Properties oauth2Properties;

    public OAuth2SecurityFilter(OAuth2Properties oauth2Properties) {
        this.oauth2Properties = oauth2Properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (oauth2Properties.getRefreshToken().getEnabled() && TOKEN_RESOURCE.equalsIgnoreCase(httpServletRequest.getRequestURI())
                && REFRESH_TOKEN_GRANT_TYPE.equals(httpServletRequest.getParameter(GRANT_TYPE_PARAMETER))
                && httpServletRequest.getCookies() != null) {
            String refreshToken = Stream.of(httpServletRequest.getCookies())
                    .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .findFirst()
                    .map(cookie -> cookie.getValue())
                    .orElse(null);
            httpServletRequest = new AppHttpServletRequestWrapper(httpServletRequest, refreshToken);
        }

        chain.doFilter(httpServletRequest, response);
    }

    public static class AppHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private String refreshToken;

        public AppHttpServletRequestWrapper(HttpServletRequest request, String refreshToken) {
            super(request);
            this.refreshToken = refreshToken;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> map = new HashMap<>(getRequest().getParameterMap());
            map.put(REFRESH_TOKEN_COOKIE_NAME, new String[] { refreshToken });

            return map;
        }

    }

}
