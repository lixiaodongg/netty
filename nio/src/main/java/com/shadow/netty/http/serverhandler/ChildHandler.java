package com.shadow.netty.http.serverhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChildHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 使用HTTP编解码 HttpServerCodec
     * 以块方式写数据，ChunkedWriteHandler
     * http数据在传输过程中是分段的，需要一个可以将数据聚合的处理器 HttpObjectAggregator
     * 对应的websocket，数据传输是以帧（frame）的形式传输
     * 浏览器请求时，ws://localhost:7000/chat
     * WebSocketServerProtocolHandler 功能是讲HTTP协议升级为ws协议，保持长连接
     */


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        System.out.println(socketChannel.remoteAddress() + "连接了");

        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
        pipeline.addLast(new NettyServerHandler()); //自定义，处理业务逻辑
    }
}
