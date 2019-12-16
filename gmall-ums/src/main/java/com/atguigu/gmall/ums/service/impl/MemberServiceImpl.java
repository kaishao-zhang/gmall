package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.exception.MemberException;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean check(String data, Integer type) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1: wrapper.eq("username",data); break;
            case 2: wrapper.eq("mobile",data); break;
            case 3: wrapper.eq("email",data); break;
            default: return false;
        }
        int count = this.count(wrapper);
        return count == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
//        - 1）校验短信验证码
//        - 2）生成盐
        String salt = UUID.randomUUID().toString().substring(0, 6);
//        - 3）对密码加密
        memberEntity.setPassword(DigestUtils.md5Hex(salt+memberEntity.getPassword()));
        //初始化其他数据
        memberEntity.setBirth(new Date());
        memberEntity.setGrowth(0);
        memberEntity.setLevelId(1L);
        memberEntity.setSign("1");
        memberEntity.setSalt(salt);
        memberEntity.setIntegration(0);
//        - 4）写入数据库
        this.save(memberEntity);
//        - 5）删除Redis中的验证码
    }

    @Override
    public MemberEntity query(String username, String password) {
        //先根据用户名查询用户信息
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        if (memberEntity == null) {
            throw new MemberException("用户名或者密码错误");
        }
        //得到用户的盐
        String salt = memberEntity.getSalt();
        //判断用户的密码是否匹配
        String s = DigestUtils.md5Hex(salt + password);
        if (!memberEntity.getPassword().equals(s)) {
            throw new MemberException("用户名或者密码错误");
        }
        return memberEntity;
    }

}