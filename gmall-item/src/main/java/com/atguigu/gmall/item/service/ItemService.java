package com.atguigu.gmall.item.service;

import com.atguigu.gmall.item.vo.ItemVO;

/**
 * @author 凯少
 * @create 2019-12-14 18:22
 */
public interface ItemService {
    //根据sku查询页面的所有信息，即封装的ItemVO对象
    ItemVO queryItemdsVO(Long skuId);
}
