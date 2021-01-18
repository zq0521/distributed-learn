package com.zq.rocketmq.domain;

import java.io.Serializable;

public class ProductWithPayload<T> implements Serializable {

    private static final long serialVersionUID = -470078490582001132L;
    private String productName;
    private T payload;

    public ProductWithPayload() {
    }

    public ProductWithPayload(String productName, T payload) {
        this.productName = productName;
        this.payload = payload;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ProductWithPayload{" +
                "productName='" + productName + '\'' +
                ", payload=" + payload +
                '}';
    }

}
