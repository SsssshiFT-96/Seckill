package com.zzj.miaosha.result;


public class CodeMsg {

    private Integer code;
    private String msg;

    //通用的错误码
    public static CodeMsg SUCCESS = new CodeMsg(0, "success!!");
    public static CodeMsg SERVER_ERROR = new CodeMsg(501, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(502, "手机号参数校验异常");

    //登录模块
    public static CodeMsg SESSION_ERROR = new CodeMsg(511, "session不存在或已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(512, "密码为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(516, "手机号为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(513, "手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(514, "手机号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(515, "密码错误");

    //秒杀模块
    public static CodeMsg MIAO_SHA_OVER = new CodeMsg(521, "商品已经秒杀完毕");
    public static CodeMsg REPEAT_MIAOSHA = new CodeMsg(522, "不能重复秒杀");

    //订单模块
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(531, "订单不存在");


    public CodeMsg(Integer code, String msg) {
        this.code = code;

        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args){
        Integer code = this.code;
        //将原始的msg与args拼接
        String message = String.format(this.msg,args);
        return new CodeMsg(code, message);
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
