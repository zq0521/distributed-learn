package cn.zq0521.dao;

import cn.zq0521.entity.Order_master;

public class OrderProvider {

    /**
     * 更新订单状态
     *
     * @param order_master
     * @return sql语句
     */
    public String updateOrderStatus(Order_master order_master) {
        StringBuffer sql = new StringBuffer("update order_master set order_status=" + order_master.getOrder_status() + ",");
        if (order_master.getPayment_method() != null && !order_master.getPayment_method().equals("")) {
            sql.append(" payment_method=" + order_master.getPayment_method() + ",");
        }
        if (order_master.getPayment_time() != null && !order_master.getPayment_time().equals("")) {
            sql.append(" payment_time=" + order_master.getPayment_time() + ",");
        }

        sql.append("update_time=" + order_master.getUpdate_time()+" where order_id="+order_master.getOrder_id());

        return sql.toString();
    }
}
