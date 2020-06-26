package com.zzj.miaosha.service;

import com.zzj.miaosha.dao.UserDao;
import com.zzj.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getUserById(Long id){
        User user = userDao.getUserById(id);
        return user;
    }
}
