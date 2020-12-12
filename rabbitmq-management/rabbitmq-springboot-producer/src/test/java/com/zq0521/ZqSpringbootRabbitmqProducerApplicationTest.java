package com.zq0521;

import com.zq0521.compent.MsgSender;
import com.zq0521.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZqSpringbootRabbitmqProducerApplicationTest {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private MsgSender msgSender;

    @Test
    public void testSenderDelay() {
        Order order = new Order();
        order.setCreateDt(new Date());
        order.setUserName("zq");
        order.setUserName(UUID.randomUUID().toString());
        order.setPayMoney(10000.00);


        msgSender.sendDelayMessage(order);

    }


}
