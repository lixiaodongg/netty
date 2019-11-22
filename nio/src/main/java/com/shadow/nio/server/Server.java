package com.shadow.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务器类
 */
public class Server {

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public Server() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(8888)); //绑定端口
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器启动成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while (true) {
            int select = 0;
            try {
                select = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (select == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    try {
                        accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (key.isValid() && key.isReadable()) {
                    readMsg(key);
                }
                iterator.remove(); //避免重复
            }
        }
    }

    private void readMsg(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int len = channel.read(buffer);
            String msg = new String(buffer.array(), 0, len);
            System.out.println("from client:" + msg);
            broadCast(msg, channel);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                key.cancel();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void broadCast(String msg, SocketChannel channel) throws IOException {
        for (SelectionKey key : selector.keys()) {
            Channel each = key.channel();
            if (each instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                socketChannel.write(buffer);
            }
        }
    }

    private void accept() throws IOException {
        SocketChannel accept = serverSocketChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        System.out.println(accept.getRemoteAddress() + "上线了");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
