package com.atguigu.gmall.auth.service;

import org.springframework.stereotype.Service;

/**
 * @author 凯少
 * @create 2019-12-16 20:56
 */
public interface AuthService {

    String accredit(String username, String password) throws Exception;
}
