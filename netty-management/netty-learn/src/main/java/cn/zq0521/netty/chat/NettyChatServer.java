package cn.zq0521.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Description: 基于netty的简易聊天室
 * @Author: ZhangQiang
 * @Date: 2021/1/18 0018 11:25
 */
public class NettyChatServer {

    public static void main(String[] args) throws Exception {
        // 创建连接线程组和工作线程组
        NioEventLoopGroup acceptGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(4);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(acceptGroup, workerGroup)  //指定服务端的两个线程组
                    .channel(NioServerSocketChannel.class) // 设置channel类型为NioServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {   //指定初始化的ChannelInitializer<SocketChannel>

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 指定解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            // 指定解码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //指定自定义的业务处理handler
                            pipeline.addLast(new NettyChatServerHandler());
                        }
                    });

            System.out.println("聊天室服务端启动....");
            // 启动服务器（并绑定端口），bind()是异步操作，sync()方法是等待异步操作执行完毕
            ChannelFuture cf = bootstrap.bind(9000).sync();
            // 监听通道关闭
            cf.channel().closeFuture().sync();

        } finally {
            acceptGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
