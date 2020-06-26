package com.zzj.miaosha.result;

public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    private Result(T data) {
        this.code = 0;
        this.msg = "success!!!";
        this.data = data;
    }

    private Result(CodeMsg codeMsg) {
        if(codeMsg == null){
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    /**
     *成功时候的调用
     */
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }

    /**
     *失败时候的调用
     */
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<>(codeMsg);
    }


    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
