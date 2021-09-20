package com.martrust.zenko_cloudserver.swagger;


import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.Example;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * https://tahaburak.medium.com/spring-boot-005-swagger-3-0-implementation-97ec6fd07dce
 * https://stackoverflow.com/questions/64236324/swagger-ui-does-not-display-response-models-from-custom-annotation-interface
 * https://stackoverflow.com/questions/54805169/set-list-of-objects-in-swagger-api-response
 */
@Configuration
public class SwaggerConfig {

    private final String CONTROLLER_PACKAGE = "com.martrust";

    @Autowired
    private SwaggerRespEnv swaggerRespEnv;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponses(HttpMethod.GET, this.buildDefaultResp())
                .globalResponses(HttpMethod.POST, this.buildDefaultResp())
                .globalResponses(HttpMethod.PUT, this.buildDefaultResp())
                .globalResponses(HttpMethod.DELETE, this.buildDefaultResp())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage(CONTROLLER_PACKAGE))
                .paths(regex("/.*")).build().apiInfo(this.buildApiInfo());
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("Zenko Private Cloudserver")
                .description("POC on Zenko Private Cloudserver")
                .version("1.0")
                .license("License of API")
                .licenseUrl("https://www.google.com/")
                .build();
    }

    private List<Response> buildDefaultResp() {
        return Arrays.asList(
        new  Response("500", swaggerRespEnv.getAppDefaultMsgInternalServer(), true, new ArrayList<Header>(),  new ArrayList<Representation>(), new ArrayList<Example>(),  new ArrayList<VendorExtension>()),
        new  Response("405", swaggerRespEnv.getAppDefaultMsgMethodNotSupported(), true, new ArrayList<Header>(),  new ArrayList<Representation>(), new ArrayList<Example>(),  new ArrayList<VendorExtension>()),
        new  Response("404", swaggerRespEnv.getAppDefaultMsgNotFound(), true, new ArrayList<Header>(),  new ArrayList<Representation>(), new ArrayList<Example>(),  new ArrayList<VendorExtension>()),
        new  Response("401", swaggerRespEnv.getAppDefaultMsgUnauthenticated(), true, new ArrayList<Header>(),  new ArrayList<Representation>(), new ArrayList<Example>(),  new ArrayList<VendorExtension>())
        );
    }
}