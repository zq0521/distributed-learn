package cn.zq0521.netty.base;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @Description: 自定义的NettyClientHandler
 * @Author: ZhangQiang
 * @Date: 2021/1/18 0018 9:49
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当客户端连接服务器完成就会触发该方法
     * 连接完成时，自动发送一条消息给服务端
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("HelloServer", CharsetUtil.UTF_8);
        ctx.writeAndFlush(buf);
    }

    /**
     * 当通道有读取事件时，会触发该方法，即服务端发送数据给客户端
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("收到服务端的消息：" + buf.toString(CharsetUtil.UTF_8));
        System.out.printf("服务端的地址：" + ctx.channel().remoteAddress());
        super.channelRead(ctx, msg);
    }

    /**
     * 发生异常时，通常会关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("通道发送异常，关闭通道，堆栈异常如下：%n");
        cause.printStackTrace();
        ctx.close();
    }


}
