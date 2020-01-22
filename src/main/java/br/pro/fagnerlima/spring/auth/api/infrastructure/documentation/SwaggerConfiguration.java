package br.pro.fagnerlima.spring.auth.api.infrastructure.documentation;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.pro.fagnerlima.spring.auth.api.application.configuration.properties.ApiInfoProperties;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("dev")
public class SwaggerConfiguration {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String HTTP_HEADER = "header";
    private final String SCOPE = "global";
    private final String DEFAULT_INCLUDE_PATTERN = "/.*";

    @Autowired
    private ApiInfoProperties apiInfoProperties;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(apiInfoProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(apiInfoProperties.getTitle())
                .description(apiInfoProperties.getDescription())
                .version(apiInfoProperties.getVersion())
                .build();
    }

    private List<ApiKey> securitySchemes() {
        return Arrays.asList(new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, HTTP_HEADER));
    }

    private List<SecurityContext> securityContexts() {
        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(securityReferences())
                .forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN))
                .build();

        return Arrays.asList(securityContext);
    }

    private List<SecurityReference> securityReferences() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[] { new AuthorizationScope(SCOPE, SCOPE) };

        return Arrays.asList((new SecurityReference(AUTHORIZATION_HEADER, authorizationScopes)));
    }

}
