package com.pwy.utils;



import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Verification;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {
    private static final String key = "milkTea";

    private static final long expireTime = 1000*60*90;

    public static String createToken(Map<String, Object> map) {
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        String token = JWT.create()
                .withHeader(header)
                .withIssuedAt(new Date()) //设置发布时期
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime)) // 设置过期时间
                .withClaim("userInfo", map)  // 封装信息
                .sign(Algorithm.HMAC256(key));
        return  token;

    }
    public static boolean verify(String token){ //验证token是否有效
        Verification require = JWT.require(Algorithm.HMAC256(key));
        try {
            require.build().verify(token);
            return true;
        } catch (JWTVerificationException e) {
            System.out.println("令牌失效");
            return false;
        }
    }
    public static  Map<String,Object> getUserInfo(String token){
        JWTVerifier build = JWT.require(Algorithm.HMAC256(key)).build();

        Map<String, Object> map = build.verify(token).getClaim("userInfo").asMap();
        return map;
    }

}
