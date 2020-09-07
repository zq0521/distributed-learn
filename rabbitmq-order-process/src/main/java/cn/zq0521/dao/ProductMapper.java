package cn.zq0521.dao;

import cn.zq0521.entity.Warehouse_product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ProductMapper {

    @Select("select count(1) from warehouse_product where product_id=#{product_id}")
    int getProudctCount(@Param("product_id") int product_id);

    @Update("update warehouse_product set current_cnt=#{current_cnt},momodified_time=#{modified_time} where product_id=#{product_id}")
    void updateProductCount(Warehouse_product warehouse_product);


}
