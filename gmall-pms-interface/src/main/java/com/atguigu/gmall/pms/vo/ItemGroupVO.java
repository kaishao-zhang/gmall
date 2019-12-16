package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-14 18:11
 */
@Data
public class ItemGroupVO {

    private String name;
    private List<ProductAttrValueEntity> baseAttrs;

}
