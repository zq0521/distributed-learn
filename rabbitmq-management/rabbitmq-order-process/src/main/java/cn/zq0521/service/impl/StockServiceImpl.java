package cn.zq0521.service.impl;

import cn.zq0521.dao.ProductMapper;
import cn.zq0521.entity.Order_details;
import cn.zq0521.entity.Warehouse_product;
import cn.zq0521.service.StockService;
import cn.zq0521.tools.CommentTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public void updateProduct(Order_details order_details) {

        int product_id = order_details.getProduct_id();
        int proudctCount = productMapper.getProudctCount(product_id);

        int newProduct = proudctCount - order_details.getProduct_cnt();

        Warehouse_product warehouse_product = new Warehouse_product();
        warehouse_product.setCurrent_cnt(newProduct);
        warehouse_product.setModified_time(CommentTools.getNowTimestamp());
        warehouse_product.setProduct_id(product_id);

        try {
            productMapper.updateProductCount(warehouse_product);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("减库操作失败");
        }





    }
}
