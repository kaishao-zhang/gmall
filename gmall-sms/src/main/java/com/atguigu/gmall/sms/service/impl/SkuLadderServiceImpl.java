package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.vo.SkuSaleInfoVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Override
    public List<SkuSaleVO> querySkuSaleBySkuId(Long skuId) {
        List<SkuSaleVO> saleVOS = new ArrayList<>();
        // 查询积分信息
        SkuBoundsEntity skuBoundsEntity = this.skuBoundsDao.selectOne(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            SkuSaleVO boundsVO = new SkuSaleVO();
            boundsVO.setType("积分");
            StringBuffer sb = new StringBuffer();
            if (skuBoundsEntity.getGrowBounds() != null && skuBoundsEntity.getGrowBounds().intValue() > 0) {
                sb.append("成长积分送" + skuBoundsEntity.getGrowBounds());
            }
            if (skuBoundsEntity.getBuyBounds() != null && skuBoundsEntity.getBuyBounds().intValue() > 0) {
                if (StringUtils.isNotBlank(sb)) {
                    sb.append("，");
                }
                sb.append("赠送积分送" + skuBoundsEntity.getBuyBounds());
            }
            boundsVO.setDesc(sb.toString());
            saleVOS.add(boundsVO);
        }

        // 查询打折
        SkuLadderEntity skuLadderEntity = this.skuLadderDao.selectOne(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (skuBoundsEntity != null) {
            SkuSaleVO ladderVO = new SkuSaleVO();
            ladderVO.setType("打折");
            ladderVO.setDesc("满" + skuLadderEntity.getFullCount() + "件，打" + skuLadderEntity.getDiscount().divide(new BigDecimal(10)) + "折");
            saleVOS.add(ladderVO);
        }

        // 查询满减
        SkuFullReductionEntity reductionEntity = this.fullReductionDao.selectOne(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (reductionEntity != null) {
            SkuSaleVO reductionVO = new SkuSaleVO();
            reductionVO.setType("满减");
            reductionVO.setDesc("满" + reductionEntity.getFullPrice() + "减" + reductionEntity.getReducePrice());

            saleVOS.add(reductionVO);
        }
        return saleVOS;
    }

}