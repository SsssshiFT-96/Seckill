package com.zzj.miaosha.dao;

import com.zzj.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select * from users where id = #{id}")
    public User getUserById(@Param("id") Long id);
}
