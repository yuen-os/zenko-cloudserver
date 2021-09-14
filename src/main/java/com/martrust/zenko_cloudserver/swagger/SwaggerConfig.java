package com.martrust.zenko_cloudserver.swagger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * https://tahaburak.medium.com/spring-boot-005-swagger-3-0-implementation-97ec6fd07dce
 */
@Configuration
public class SwaggerConfig {

    private final String CONTROLLER_PACKAGE = "com.martrust.zenko_cloudserver";

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