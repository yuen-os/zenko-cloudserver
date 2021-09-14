package com.martrust.zenko_cloudserver.swagger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


    private final String CONTROLLER_PACKAGE = "com.martrust.zenko_cloudserver";

    @Autowired
    private SwaggerRespEnv swaggerRespEnv;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .globalResponseMessage(RequestMethod.GET, this.buildDefaultResp())
                .globalResponseMessage(RequestMethod.POST, this.buildDefaultResp())
                .globalResponseMessage(RequestMethod.PUT, this.buildDefaultResp())
                .globalResponseMessage(RequestMethod.DELETE, this.buildDefaultResp())
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

    private List<ResponseMessage> buildDefaultResp() {
        return Arrays.asList(
                new ResponseMessageBuilder().code(500).message(swaggerRespEnv.getAppDefaultMsgInternalServer()).build(),
                new ResponseMessageBuilder().code(405).message(swaggerRespEnv.getAppDefaultMsgMethodNotSupported()).build(),
                new ResponseMessageBuilder().code(404).message(swaggerRespEnv.getAppDefaultMsgNotFound()).build(),
                new ResponseMessageBuilder().code(401).message(swaggerRespEnv.getAppDefaultMsgUnauthenticated()).build()
        );
    }
}