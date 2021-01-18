package cn.zq0521.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 自定义的ChatServer 处理器
 * @Author: ZhangQiang
 * @Date: 2021/1/18 0018 11:32
 */
public class NettyChatServerHandler extends SimpleChannelInboundHandler<String> {

    // GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    // 表示channel处于在线状态，上线提示
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将客户端上线的消息，推送给其他在线的客户端
        // 该方法会将channelGroup中所有channel遍历，并发送消息
        channelGroup.writeAndFlush("[客户端]：" + channel.remoteAddress() + " 上线了 " + sdf.format(new Date()) + "\n");
        // 将当前channel加入到channelGroup
        channelGroup.add(channel);
        // 在服务端的控制台打印出该客户端上线的消息
        System.out.println("[客户端]：" + channel.remoteAddress() + " 上线了 " + sdf.format(new Date()) + "\n");
        System.out.println("在线人数： " + channelGroup.size());
    }

    // 表示 channel处于不活动状态，提示离线
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String clientQuitMsg = "[客户端]：" + channel.remoteAddress() + " 下线了 " + sdf.format(new Date()) + "\n";
        channelGroup.writeAndFlush(clientQuitMsg);
        // 服务端控制台打印客户端下线的消息
        System.out.println(clientQuitMsg);
        System.out.println("在线人数：" + channelGroup.size());

    }

    // 读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取当前channel
        Channel channel = ctx.channel();
        // 构建公用消息体
        String sendMessage = "[客户端]：" + channel.remoteAddress() + " 时间：" + sdf.format(new Date()) + " 发送了消息 :" + msg;

        // 遍历公共channelGroup,根据不同情况，推送不同消息
        channelGroup.forEach(ch -> {
            if (channel != ch) {  //判断是否是当前的channel,转发消息
                ch.writeAndFlush(sendMessage);
            } else {  //自己的消息做回显就可以了
                ch.writeAndFlush("[自己 ]" + channel.remoteAddress() + " 时间：" + sdf.format(new Date()) + " 发送了消息 :" + msg);
            }
        });

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
