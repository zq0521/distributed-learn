package cn.zq0521;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zq0521.TulingvipRabbitmqSpringwithrabbitmqApplication;
import com.zq0521.entity.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TulingvipRabbitmqSpringwithrabbitmqApplication.class)
public class TulingvipRabbitmqSpringwithrabbitmqApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void contextLoads() {
    }


    /**
     * 使用默认的监听方法进行消费
     */
    @Test
    public void testRabbitTemplate() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("company", "tuling");
        messageProperties.getHeaders().put("name", "smlz");

        String msgBody = "hello tuling";
        Message message = new Message(msgBody.getBytes(), messageProperties);

        //不需要message对象发送
        rabbitTemplate.convertAndSend("tuling.direct.exchange", "direct.key", "smlz");
    }


    /**
     * 不同的队列，由不同的方法进行消费
     */
    @Test
    public void messageListenerAdaperQueueOrTagToMethodName() {
        rabbitTemplate.convertAndSend("tuling.topic.exchange", "topic.xixi", "你好 图灵");
        rabbitTemplate.convertAndSend("tuling.topic.exchange", "topic.key.xixi", "你好 smlz");
    }


    /**
     * 发送JSON消息
     *
     * @throws JsonProcessingException
     */
    @Test
    public void sendJson() throws JsonProcessingException {
        Order order = new Order(UUID.randomUUID().toString(), new Date(), 10000.00, "smlz");

        ObjectMapper objectMapper = new ObjectMapper();
        String OrderJson = objectMapper.writeValueAsString(order);

        //设置消息属性体
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message orderMsg = new Message(OrderJson.getBytes(), messageProperties);
        //发送JSON消息
        rabbitTemplate.convertAndSend("tuling.direct.exchange", "rabbitmq.order", orderMsg);

    }


    /**
     * 发送java对象
     *
     * @throws JsonProcessingException
     */
    @Test
    public void sendJavaObj() throws JsonProcessingException {
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setCreateDt(new Date());
        order.setPayMoney(10000.00);
        order.setUserName("smlz");

        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.zq0521.entity.Order");
        Message orderMsg = new Message(orderJson.getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("tuling.direct.exchange", "rabbitmq.order", orderMsg);
    }

    /**
     * 发送文件对象----图片
     *
     * @throws IOException
     */
    @Test
    public void sendImage() throws IOException {
        byte[] imgBody = Files.readAllBytes(Paths.get("e:/test/file01", "1.jpg"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("img/jpg");
        Message message = new Message(imgBody, messageProperties);
        rabbitTemplate.send("tuling.direct.exchange", "rabbitmq.file", message);
    }

    /**
     * 发送文件对象---word文档
     *
     * @throws IOException
     */
    @Test
    public void sendWord() throws IOException {
        byte[] imgBody = Files.readAllBytes(Paths.get("e:/test/file01", "spring.doc"));
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/word");
        Message message = new Message(imgBody, messageProperties);
        rabbitTemplate.send("tuling.direct.exchange", "rabbitmq.file", message);
    }


}
