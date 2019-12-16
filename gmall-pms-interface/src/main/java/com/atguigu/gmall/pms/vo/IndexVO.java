package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-13 11:36
 */
@Data
public class IndexVO extends CategoryEntity {

    private List<CategoryEntity> subs;

}
