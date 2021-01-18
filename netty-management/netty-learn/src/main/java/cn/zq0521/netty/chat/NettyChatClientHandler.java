package cn.zq0521.netty.chat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Description: TODO
 * @Author: ZhangQiang
 * @Date: 2021/1/18 0018 10:36
 */
public class NettyChatClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim());
        ctx.flush();
    }

    // 服务端端口连接
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        ctx.close();
        System.out.println("服务端断开连接,关闭通道");
    }

    // 异常处理，关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
