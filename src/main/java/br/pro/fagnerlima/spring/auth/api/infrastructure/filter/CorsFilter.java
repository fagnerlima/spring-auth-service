package br.pro.fagnerlima.spring.auth.api.infrastructure.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.CorsProperties;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Autowired
    private CorsProperties corsProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        httpServletResponse.setHeader("Access-Control-Allow-Origin", corsProperties.getAllowedOrigin());
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(httpServletRequest.getMethod())
                && corsProperties.getAllowedOrigin().equals(httpServletRequest.getHeader("Origin"))) {
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(request, response);
        }
    }

}
