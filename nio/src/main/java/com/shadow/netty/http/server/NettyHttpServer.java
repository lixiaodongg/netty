package com.shadow.netty.http.server;

import com.shadow.netty.http.serverhandler.ChildHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyHttpServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap bootstrap;
    private final static int PORT = 7000;

    /**
     * 创建两个线程组
     * <p>
     * BOSS_GROUP负责处理连接请求;
     * WORKER_GROUP负责处理业务;
     * 两个都是无限循环
     * 两者含有的子线程个数，默认是CPU的核数*2
     */
    public NettyHttpServer() {
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
    }

    /**
     * 配置一些启动项
     * 使用链式编程
     */
    public void config() {

        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用nioServerSocket作为服务器通道的实现
                    .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列连接的个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动的连接状态
                    .childHandler(new ChildHandler());
            System.out.println("服务器准备好了..");
        } catch (Exception e) {
            System.out.println("配置失败");
        }
    }

    private void start() {
        try {
            //启动服务器并绑定端口
            ChannelFuture future = bootstrap.bind(PORT).sync();
            System.out.println("服务器启动成功");
            //对关闭通道进行监听
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            System.out.println("启动失败");
        } finally {
            bossGroup.shutdownGracefully(); //优雅的关闭
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyHttpServer server = new NettyHttpServer();
        server.config();
        server.start();
    }
}
