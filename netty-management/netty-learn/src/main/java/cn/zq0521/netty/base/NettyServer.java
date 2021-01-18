package cn.zq0521.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description: Netty服务端
 * @Author: ZhangQiang
 * @Date: 2021/1/18 0018 9:06
 */
public class NettyServer {

    public static void main(String[] args) {
        // 创建两个线程组,分别命名：bossGroup和workerGroup，这两个线程组默认的子线程个数为：cpu核数*2,也可以自己指定子线程初始化数量
        // bossGroup：只负责处理连接请求（对外提供服务的）的线程组
        // workerGroup: 真正处理业务请求的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 使用链式编程来配置参数
            bootstrap.group(bossGroup, workerGroup)  //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel作为服务器的通道实现
                    // 初始化服务器连接队列的大小，服务端处理客户端连接的请求是顺序处理的，所以同一时间只能处理1个客户端连接
                    // 多个客户端同时来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 创建通道初始化对象，设置初始化参数

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 对workerGroup的SocketChannel设置处理（一般我们的业务处理逻辑就是写在这些自定义的handler中的）
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("netty server start...");

            // 绑定一个端口并且同步，生成一个ChannelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            // 启动服务器（并绑定端口），bind()是异步操作，sync()方法是等待异步操作执行完毕
            ChannelFuture cf = bootstrap.bind(9000).sync();

            // 如果上面的bootStrap.bind(9000)不使用sync()方法的话，就需要使用异步线程的监听器进行监听异步线程是否启动完成
           /* cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("监听端口9000成功");
                    } else {
                        System.out.println("监听端口9000失败");
                    }
                }
            });*/

            // 对通道的关闭进行监听，closeFuture是异步执行，需要监听通道的关闭
            // 通过sync()方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成
            cf.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

}
