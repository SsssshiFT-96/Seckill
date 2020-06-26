package com.zzj.miaosha.exception;

import com.zzj.miaosha.result.CodeMsg;
import lombok.Data;

//自定义一个全局异常，将所有异常都为该异常进行集中处理
@Data
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
