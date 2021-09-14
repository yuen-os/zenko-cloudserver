package com.martrust.zenko_cloudserver.swagger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor
public class SwaggerRespEnv {
    @Value("${zenko.application.default.msg.internal-server}")
    private String appDefaultMsgInternalServer;
    @Value("${zenko.application.default.msg.not-found}")
    private String  appDefaultMsgNotFound;
    @Value("${zenko.application.default.msg.unauthenticated}")
    private String appDefaultMsgUnauthenticated;
    @Value("${zenko.application.default.msg.method-not-supported}")
    private String appDefaultMsgMethodNotSupported;
}
