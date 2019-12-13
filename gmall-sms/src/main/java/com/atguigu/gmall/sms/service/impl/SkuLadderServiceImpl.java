package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.vo.SkuSaleInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuLadderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuLadderService")
public class SkuLadderServiceImpl extends ServiceImpl<SkuLadderDao, SkuLadderEntity> implements SkuLadderService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuLadderEntity> page = this.page(
                new Query<SkuLadderEntity>().getPage(params),
                new QueryWrapper<SkuLadderEntity>()
        );

        return new PageVo(page);
    }
    @Autowired
    private SkuBoundsDao skuBoundsDao;
    @Autowired
    private SkuFullReductionDao fullReductionDao;
    @Autowired
    private SkuLadderDao skuLadderDao;

    @Transactional
    @Override
    public void saveSkuSaleInfo(SkuSaleInfoVO skuSaleInfoVO) {
        Long skuId = skuSaleInfoVO.getSkuId();
        //保存积分活动信息
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleInfoVO,skuBoundsEntity);
        List<Integer> work = skuSaleInfoVO.getWork();
        if (!CollectionUtils.isEmpty(work)) {
            skuBoundsEntity.setWork((work.get(0)<<3)+(work.get(1)<<2)+(work.get(2)<<1)+work.get(3));
        }
        skuBoundsDao.insert(skuBoundsEntity);
        //保存满减活动信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleInfoVO,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuSaleInfoVO.getFullAddOther());
        fullReductionDao.insert(skuFullReductionEntity);
        //保存打折活动信息
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleInfoVO,skuLadderEntity);
        skuLadderEntity.setAddOther(skuSaleInfoVO.getLadderAddOther());
        skuLadderDao.insert(skuLadderEntity);
    }

}