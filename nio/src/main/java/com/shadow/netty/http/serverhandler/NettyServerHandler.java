package com.shadow.netty.http.serverhandler;

import com.shadow.netty.http.utils.Utils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 自定义handler需要继承netty的规范
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息：" + msg.text());

        TextWebSocketFrame response = new TextWebSocketFrame(ctx.channel().remoteAddress() + "[" + Utils.now() + "]\n" + msg.text());

        group.writeAndFlush(response);
    }


    /**
     * 添加
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String id = ctx.channel().id().asLongText();
        System.out.println(Utils.now() + "连接了一个用户,ID为：" + id);
        group.add(channel); //添加到group中
        String msg =  ctx.channel().remoteAddress() + Utils.now() + "加入了聊天";
        group.writeAndFlush(new TextWebSocketFrame(msg));
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String id = ctx.channel().id().asLongText();
        System.out.println(Utils.now() + id + "离开了");
        String leave = ctx.channel().remoteAddress() + Utils.now() + "离开了";
        System.out.println("group——size：" + group.size());
        group.writeAndFlush(new TextWebSocketFrame(leave));
    }

    /**
     * 数据读取完毕
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 异常，一般需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 通道活跃
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 通道不活跃
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

}
