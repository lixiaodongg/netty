package com.shadow.netty.simple.client;

import com.shadow.netty.simple.clienthandler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private EventLoopGroup eventExecutors;
    private Bootstrap bootstrap;
    private final static String IP = "localhost";
    private final static int PORT = 9997;

    NettyClient() {
        eventExecutors = new NioEventLoopGroup();
    }

    public void config() {
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ChannelPipeline pipeline = sc.pipeline();
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        System.out.println("客户端准备好了");
    }

    public void start() {
        try {
            ChannelFuture channelFuture = bootstrap.connect(IP, PORT).sync();
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("连接失败");
            e.printStackTrace();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        client.config();
        client.start();
    }
}
