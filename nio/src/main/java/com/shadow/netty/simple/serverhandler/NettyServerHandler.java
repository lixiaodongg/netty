package com.shadow.netty.simple.serverhandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

import static io.netty.util.CharsetUtil.UTF_8;

/**
 * 自定义handler需要继承netty的规范
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * ctx：上下文对象
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //将耗时长的操作放到任务队列中执行
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10000);
                System.out.println("耗时长的操作执行完毕");
                ctx.writeAndFlush( Unpooled.copiedBuffer("耗时长的操作执行完毕", UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        System.out.println("当前线程名字：" + Thread.currentThread().getName());
        System.out.println("ctx=" + ctx);
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是：" + byteBuf.toString(Charset.defaultCharset()));
        System.out.println("客户端地址是：" + ctx.channel().remoteAddress());
    }

    /**
     * 数据读取完毕
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ByteBuf byteBuf = Unpooled.copiedBuffer("来自服务端的消息:hello,client", UTF_8);

        ctx.writeAndFlush(byteBuf);
    }

    /**
     * 异常，一般需要关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
