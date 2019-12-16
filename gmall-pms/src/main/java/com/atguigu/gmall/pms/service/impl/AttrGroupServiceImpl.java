package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrGroupById(QueryCondition condition, Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id",catId);
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(condition), wrapper);
        return new PageVo(page);
    }
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Override
    public AttrGroupVO withAttrs(Long gid) {
        //创建一个AttrGroupVo对象
        AttrGroupVO groupVo = new AttrGroupVO();
        //将AttrGroupEntities封装进去AttrGroupVo
        AttrGroupEntity groupEntity = this.attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(groupEntity,groupVo);
        //根据分组id查询分组和属性的中间表数据并封装进去AttrGroupVo
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_group_id",gid);
        List<AttrAttrgroupRelationEntity> relations = relationDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(relations)) {
            return groupVo;
        }
        groupVo.setRelations(relations);
        //根据attrid获取所有的attr对象
        List<Long> attrIds = relations.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList());
        List<AttrEntity> attrEntities = this.attrDao.selectBatchIds(attrIds);
        groupVo.setAttrEntities(attrEntities);
        return groupVo;
    }

    @Override
    public List<AttrGroupVO> queryGroupAttrByCatId(Long catId) {
        //根据catid查询所有的分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catId));
        //根据所有的分组查询所有的属性
        return attrGroupEntities.stream().map(attrGroupEntity -> this.withAttrs(attrGroupEntity.getAttrGroupId())).collect(Collectors.toList());

    }

    @Override
    public List<ItemGroupVO> queryItemGroupVOByCidAndSpuId(Long cid, Long spuId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));

        List<ItemGroupVO> itemGroupVOS = attrGroupEntities.stream().map(attrGroupEntity -> {
            ItemGroupVO itemGroupVO = new ItemGroupVO();
            itemGroupVO.setName(attrGroupEntity.getAttrGroupName());
            Long groupId = attrGroupEntity.getAttrGroupId();
            List<AttrAttrgroupRelationEntity> attrgroupRelationEntities = this.relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));
            List<Long> attrIds = attrgroupRelationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            if (attrIds.size() != 0) {
                List<ProductAttrValueEntity> productAttrValueEntities = this.productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));
                itemGroupVO.setBaseAttrs(productAttrValueEntities);
            }
            return itemGroupVO;
        }).collect(Collectors.toList());
        return itemGroupVOS;
    }

}