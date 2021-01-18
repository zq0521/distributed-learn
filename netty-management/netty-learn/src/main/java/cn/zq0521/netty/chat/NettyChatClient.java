package cn.zq0521.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @Description: 使用netty实现聊天室客户端
 * @Author: ZhangQiang
 * @Date: 2021/1/18 0018 10:36
 */
public class NettyChatClient {

    public static void main(String[] args) throws Exception {
        // 配置客户端的一个线程组
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        // 设置客户端启动类
        Bootstrap bootstrap = new Bootstrap();
        // 启动类配置文件
        try {
            bootstrap.group(group) //将线程组配置到启动类中
                    .channel(NioSocketChannel.class)  //设置channel类型为NioSocketChannel类型
                    .handler(new ChannelInitializer<SocketChannel>() {   //添加handler处理器，固定写法，都是使用new ChannelInitializer<SocketChannel>()
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 设置字符转换
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            // 添加自定义的事件处理器
                            pipeline.addLast(new NettyChatClientHandler());
                        }
                    });

            System.out.println("chat client start...");
            // 使用bootStrap连接服务端
            ChannelFuture future = bootstrap.connect("127.0.0.1", 9000).sync();
            Channel channel = future.channel();
            System.out.println("==========" + channel.localAddress() + "=============");
            // 客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                // 通过channel发送消息至服务端
                channel.writeAndFlush(message);
            }
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
