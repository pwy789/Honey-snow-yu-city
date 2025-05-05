package com.pwy.common;
//redis的键定义常量
public class RedisKeyConstant {
    public static  final String GOODS_CATEGORY_KEY="Goods:Category";

    public static final Long GOODS_CATEGORY_KEY_EXPIRE=60L;

    public static final String GOODS_DETAIL_KEY="Goods:Detail:";

    public static final Long GOODS_DETAIL_EXPIRE=30L;

    public static final  String LOCK_ORDER_KEY="Order:Lock:";
    //设置过期时间为15秒
    public static final  Long LOCK_ORDER_KEY_EXPIRE=15L;

    public static final  String ORDER_IS_TIMEOUT_KEY="Order-Timeout-List";
    //消息队列key
    public static final String ORDER_MESSAGE_QUEUE="OrderStream";

    public static final String ORDER_DELIVERY_KEY="Order-Delivery-List";

    //验证码
    public static final String USER_PHONE_CODE_KEY="User-Code:";
    //验证码TTL(5分钟)
    public static final Long USER_PHONE_CODE_TTL=300L;
}
