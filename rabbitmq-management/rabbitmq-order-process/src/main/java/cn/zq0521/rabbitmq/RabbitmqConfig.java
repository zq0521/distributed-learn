package cn.zq0521.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        //connectionFactory.setAddresses("192.168.1.155:5672");
        connectionFactory.setHost("192.168.1.155");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("tuling");
        connectionFactory.setUsername("zhangsan");
        connectionFactory.setPassword("123456");
        connectionFactory.setConnectionTimeout(100000);
        return connectionFactory;
    }


    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    //声明交换机

    /**
     * 订单服务交换机
     * @return
     */
    @Bean
    public DirectExchange orderExchange(){
        DirectExchange directExchange = new DirectExchange("order.exchange",true,false);
        return directExchange;
    }

    /**
     * 库存服务交换机
     * @return
     */
    @Bean
    public DirectExchange stockExchange(){
        DirectExchange stockExchange = new DirectExchange("stock.exchange",true,false);
        return stockExchange;
    }

    /**
     * 回调服务交换机
     * @return
     */
    @Bean
    public DirectExchange callbackExchange(){
        DirectExchange callbackExchange = new DirectExchange("callback.exchange",true,false);
        return callbackExchange;
    }




    //声明队列

    /**
     * 订单业务队列
     * @return
     */
    @Bean
    public Queue orderQueue(){
        Queue queue = new Queue("orderQueue",true,false,false,null);
        return queue;
    }

    /**
     * 库存服务队列
     * @return
     */
    @Bean
    public Queue stockQueue(){
        Queue queue = new Queue("stackQueue",true,false,false,null);
        return queue;
    }

    /**
     * 回调服务队列
     * @return
     */
    @Bean
    public Queue callbackQueue(){
        Queue queue = new Queue("callbackQueue",true,false,false,null);
        return queue;
    }



    //申明绑定

    /**
     * 订单业务队列绑定交换机
     * @return
     */
    @Bean
    public Binding orderQueueBinding(){
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("order.key");
    }

    /**
     * 库存队列绑定交换机
     * @return
     */
    @Bean
    public Binding stockQueueBinding(){
        return BindingBuilder.bind(stockQueue()).to(stockExchange()).with("stock.key");
    }

    /**
     * 回调队列绑定交换机
     * @return
     */
    @Bean
    public Binding callbackQueueBinding(){
        return BindingBuilder.bind(callbackQueue()).to(callbackExchange()).with("callback.key");
    }


    /**
     * rabbitTemple实例化工具
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setReceiveTimeout(50000);
        return rabbitTemplate;
    }


    /**
     * 库存业务监听订单业务监听器
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer stackServiceListenerWithOrderServiceContainer(){
        SimpleMessageListenerContainer orderServiceListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        //指定监听队列----> 订单队列，库存队列，回调队列
        orderServiceListenerContainer.setQueues(orderQueue());

        //设置消费者数量
        orderServiceListenerContainer.setConcurrentConsumers(5);
        //设置最大消费者数量
        orderServiceListenerContainer.setMaxConcurrentConsumers(10);
        //设置签收模式---> 手动签收模式
        orderServiceListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //设置是否重回队列----> 拒绝重回队列
        orderServiceListenerContainer.setDefaultRequeueRejected(false);

        //处理order实体类对象
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
        //设置method
        messageListenerAdapter.setDefaultListenerMethod("orderMessage");
        //设置消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        //JSON对象与java对象互转
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //指定需要转换的实体类包名
        javaTypeMapper.setTrustedPackages("cn.zq0521.entity");
        //json转java
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

        //设置消息转换器
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        //将监听设置到监听容器中
        orderServiceListenerContainer.setMessageListener(messageListenerAdapter);

        return orderServiceListenerContainer;
    }


    /**
     * 回调服务监听 ------->库存服务消息
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer callbackMessageListenerStackMessageContainer(){
        SimpleMessageListenerContainer callbackMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        //指定监听队列----> 订单队列，库存队列，回调队列
        callbackMessageListenerContainer.setQueues(stockQueue());
        //设置消费者数量
        callbackMessageListenerContainer.setConcurrentConsumers(5);
        //设置最大消费者数量
        callbackMessageListenerContainer.setMaxConcurrentConsumers(10);
        //设置签收模式---> 手动签收模式
        callbackMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //设置是否重回队列----> 拒绝重回队列
        callbackMessageListenerContainer.setDefaultRequeueRejected(false);

        //处理order实体类对象
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
        //设置method
        messageListenerAdapter.setDefaultListenerMethod("stackMessage");
        //设置消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        //JSON对象与java对象互转
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //指定需要转换的实体类包名
        javaTypeMapper.setTrustedPackages("cn.zq0521.entity");
        //json转java
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

        //设置消息转换器
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        //将监听设置到监听容器中
        callbackMessageListenerContainer.setMessageListener(messageListenerAdapter);

        return callbackMessageListenerContainer;
    }


    /**
     * 回调服务监听 ------->订单服务的延迟消息
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer callbackMessageListenerOrderDelayMessageContainer(){
        SimpleMessageListenerContainer callbackMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());
        //指定监听队列----> 订单队列，库存队列，回调队列
        callbackMessageListenerContainer.setQueues(orderQueue(),stockQueue(),callbackQueue());
        //设置消费者数量
        callbackMessageListenerContainer.setConcurrentConsumers(5);
        //设置最大消费者数量
        callbackMessageListenerContainer.setMaxConcurrentConsumers(10);
        //设置签收模式---> 手动签收模式
        callbackMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //设置是否重回队列----> 拒绝重回队列
        callbackMessageListenerContainer.setDefaultRequeueRejected(false);

        //处理order实体类对象
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
        //设置method
        messageListenerAdapter.setDefaultListenerMethod("orderDelayedMessage");
        //设置消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        //JSON对象与java对象互转
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //指定需要转换的实体类包名
        javaTypeMapper.setTrustedPackages("cn.zq0521.entity");
        //json转java
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

        //设置消息转换器
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        //将监听设置到监听容器中
        callbackMessageListenerContainer.setMessageListener(messageListenerAdapter);

        return callbackMessageListenerContainer;
    }













}
