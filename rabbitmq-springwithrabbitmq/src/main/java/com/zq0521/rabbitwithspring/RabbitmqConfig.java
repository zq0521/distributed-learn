package com.zq0521.rabbitwithspring;

import com.zq0521.conveter.TulingImageConverter;
import com.zq0521.conveter.TulingWordConverter;
import com.zq0521.messagedelegate.TulingMsgDelegate;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * spring整合rabbitmq的整合版本
 * Create by zq on 2020/09/07
 */
@Configuration
public class RabbitmqConfig {

    /**
     * 创建连接工厂
     *
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("192.168.1.155:5672");
        cachingConnectionFactory.setVirtualHost("tuling");
        cachingConnectionFactory.setUsername("zhangsan");
        cachingConnectionFactory.setPassword("123456");
        cachingConnectionFactory.setConnectionTimeout(10000);
        cachingConnectionFactory.setCloseTimeout(10000);
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //spring容器自动加载
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


    //=====================================申明三个交换机====================================================================
    @Bean
    public TopicExchange topicExchange() {
        TopicExchange topicExchange = new TopicExchange("tuling.topic.exchange", true, false);
        return topicExchange;
    }


    @Bean
    public DirectExchange directExchange() {
        DirectExchange directExchange = new DirectExchange("tuling.direct.exchange", true, false);
        return directExchange;
    }


    @Bean
    public FanoutExchange fanoutExchange() {
        FanoutExchange fanoutExchange = new FanoutExchange("tuling.faout.exchange", true, false);
        return fanoutExchange;
    }


    //=====================================申明队列====================================================================
    @Bean
    public Queue testTopicQueue() {
        Queue queue = new Queue("testTopicQueue", true, false, false, null);
        return queue;
    }

    @Bean
    public Queue testTopicQueue2() {
        Queue queue = new Queue("testTopicQueue2", true, false, false, null);
        return queue;
    }

    @Bean
    public Queue testDirectQueue() {
        Queue queue = new Queue("testDirectQueue", true, false, false, null);
        return queue;
    }

    @Bean
    public Queue testFanoutQueue() {
        Queue queue = new Queue("testfaoutQueue", true, false, false, null);
        return queue;
    }

    @Bean
    public Queue orderQueue() {
        Queue queue = new Queue("orderQueue", true, false, false, null);
        return queue;
    }

    @Bean
    public Queue addressQueue() {
        Queue queue = new Queue("addressQueue", true, false, false, null);
        return queue;
    }

    @Bean
    public Queue fileQueue() {
        Queue queue = new Queue("fileQueue", true, false, false, null);
        return queue;
    }


    //=====================================申明绑定====================================================================
    @Bean
    public Binding topicQueueBinding() {
        return BindingBuilder.bind(testTopicQueue()).to(topicExchange()).with("topic.#");
    }

    @Bean
    public Binding topicQueueBinding2() {
        return BindingBuilder.bind(testTopicQueue2()).to(topicExchange()).with("topic.key.#");
    }

    @Bean
    public Binding directQueueBinding() {
        return BindingBuilder.bind(testDirectQueue()).to(directExchange()).with("direct.key");
    }

    @Bean
    public Binding fanoutQueueBinding() {
        return BindingBuilder.bind(testFanoutQueue()).to(fanoutExchange());
    }

    @Bean
    public Binding orderQueueBinding() {
        return BindingBuilder.bind(orderQueue()).to(directExchange()).with("rabbitmq.order");
    }

    @Bean
    public Binding addressQueueBinding() {
        return BindingBuilder.bind(addressQueue()).to(directExchange()).with("rabbitmq.address");
    }

    @Bean
    public Binding fileQueueBinding() {
        return BindingBuilder.bind(fileQueue()).to(directExchange()).with("rabbitmq.file");
    }

    /**
     * 发送消息的工具类
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setReceiveTimeout(50000);
        return rabbitTemplate;
    }


    /**
     * 简单的消息监听容器
     *
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer() {
        //简单消费者容器
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory());

        //指定需要监听的队列
        simpleMessageListenerContainer.setQueues(testTopicQueue(), testDirectQueue(), testTopicQueue2(), orderQueue(), addressQueue(), fileQueue());
        //设置消费者的数量,
        simpleMessageListenerContainer.setConcurrentConsumers(5);
        //设置最大消费者数量
        simpleMessageListenerContainer.setMaxConcurrentConsumers(10);
        //设置签收模式
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //设置拒绝重回队列
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);


        /**
         * 模式1：
         * 设置使用默认的监听方法处理消息
         * 这里是使用的 TulingMsgDelegate.handleMessage()进行监听消息的
         */
   /*     MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new TulingMsgDelegate());

        //将监听设置到监听的容器中去
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);*/


        /**
         * 模式2：
         * 不适用默认的监听方法
         * 使用自定义的监听方法进行处理
         */
       /* MessageListenerAdapter messageListenerAdapter2 = new MessageListenerAdapter(new TulingMsgDelegate());
        //指定监听方法，指定的是 TulingMsgDelegate.consumerMsg()方法
        messageListenerAdapter2.setDefaultListenerMethod("consumerMsg");
        //将监听设置到监听的容器中去
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter2);*/


        /**
         * 模式3：
         * 不同的队列，由不同的方法进行处理
         */
      /*  MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new TulingMsgDelegate());

        //设置不同的队列，由不同的方法进行消费
        Map<String,String> queueMaps = new HashMap<>();
        queueMaps.put("testTopicQueue","consumerTopicQueue");
        queueMaps.put("testTopicQueue2","consumerTopicQueue2");
        messageListenerAdapter.setQueueOrTagToMethodName(queueMaps);
        //将监听设置到监听的容器中去
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);*/


        /**
         * 模式4：
         * 处理JSON
         */
       /* MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new TulingMsgDelegate());
        //指定处理JSON的方法，方法是 TulingMsgDelegate.consumerJsonMessage();
        messageListenerAdapter.setDefaultListenerMethod("consumerJsonMessage");
        //构造一个JSON消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        //将消息设置进messageConverter
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        //将监听设置到监听的容器中去
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);*/


        /**
         * 模式5：
         * 处理java对象
         */
      /*  MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new TulingMsgDelegate());
        //设置处理java对象的方法
        messageListenerAdapter.setDefaultListenerMethod("consumerJavaObjMessage");
        //设置消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();

        //JSON对象与JAVA对象相互转换的转换器
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        //设置包名
        javaTypeMapper.setTrustedPackages("com.zq0521.entity");
        //json转java
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);

        //设置消息转换器
        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
        //将监听设置到监听的容器中去
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);*/


        /**
         * 模式6：
         * 处理文件和图片
         */
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new TulingMsgDelegate());
        //设置处理图片的方法
        messageListenerAdapter.setDefaultListenerMethod("consumerFileMessage");

        //设置转换器
        ContentTypeDelegatingMessageConverter messageConverter = new ContentTypeDelegatingMessageConverter();
        //将我们自定义的转换器传入其中
        messageConverter.addDelegate("img/png", new TulingImageConverter());
        messageConverter.addDelegate("img/jpg", new TulingImageConverter());
        messageConverter.addDelegate("application/word", new TulingWordConverter());
        messageConverter.addDelegate("word", new TulingWordConverter());

        messageListenerAdapter.setMessageConverter(messageConverter);

        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);


        return simpleMessageListenerContainer;
    }


}
