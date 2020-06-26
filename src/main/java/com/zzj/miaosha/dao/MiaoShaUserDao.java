package com.zzj.miaosha.dao;

import com.zzj.miaosha.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface
MiaoShaUserDao {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoShaUser getById(@Param("id") Long id);
}

