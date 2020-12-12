package com.zq0521.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductInfoMapper {

    @Update("update product_info set product_num=product_num-1 where product_no=#{productNo}" +
            "        and product_num>0")
    int updateProductStoreById(Integer productId);
}
