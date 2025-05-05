package com.pwy.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
//微信登录参数
@Data
@Component
@ConfigurationProperties(prefix = "weixin.login")
public class WeiXinLoginProperties {
    private String url;

    private String appId;

    private String appSecret;

    private String grant_type;
}
