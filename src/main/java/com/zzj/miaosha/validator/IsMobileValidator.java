package com.zzj.miaosha.validator;

import com.zzj.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//系统来执行如何判断手机号格式是否正确
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    //初始化方法可以拿到注解
    @Override
    public void initialize(IsMobile constraintAnnotation) {
       required = constraintAnnotation.required();
    }

    //实际的判断是否合法
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        //首先判断是否允许为空
        if(required){
            return ValidatorUtil.isMobile(value);
        }else{
            if(StringUtils.isEmpty(value)){
                return true;
            }else{
                return ValidatorUtil.isMobile(value);
            }

        }
    }
}
