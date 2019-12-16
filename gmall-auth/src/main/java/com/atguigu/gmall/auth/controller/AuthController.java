package com.atguigu.gmall.auth.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.MemberException;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.propertites.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 凯少
 * @create 2019-12-16 20:45
 */
@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;
    @PostMapping("/accredit")
    public Resp<Object> accredit(@RequestParam("username")String username, @RequestParam("password")String password,
                                 HttpServletRequest request , HttpServletResponse response) throws Exception {
        String token = this.authService.accredit(username, password);
        if (!StringUtils.isBlank(token)) {
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
            return Resp.ok(null);
        }
        throw  new MemberException("用户名或者密码错误");
    }

}
