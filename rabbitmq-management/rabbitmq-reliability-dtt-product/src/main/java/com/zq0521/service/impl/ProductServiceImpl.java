package com.zq0521.service.impl;

import com.zq0521.bo.MsgTxtBo;
import com.zq0521.entity.MessageContent;
import com.zq0521.enumration.MsgStatusEnum;
import com.zq0521.exception.BizExp;
import com.zq0521.mapper.MsgContentMapper;
import com.zq0521.mapper.ProductInfoMapper;
import com.zq0521.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private MsgContentMapper msgContentMapper;


    @Override
    @Transactional
    public boolean updateProductStore(MsgTxtBo msgTxtBo) {
        boolean updateFlag = true;

        try {
            //更新库存
            productInfoMapper.updateProductStoreById(msgTxtBo.getProductNo());

            //更新消息
            MessageContent messageContent = new MessageContent();
            messageContent.setMsgId(msgTxtBo.getMsgId());
            //messageContent.setUpdateTime(new Date());
            messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_SUCCESS.getCode());
            msgContentMapper.updateMsgStatus(messageContent);

            //模拟事务异常
            //System.out.println(1/0);

        } catch (Exception e) {
            log.error("更新数据库失败：{}", e);
            throw new BizExp(0, "更新数据库异常");
        }

        return updateFlag;
    }
}
