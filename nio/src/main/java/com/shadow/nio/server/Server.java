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
            serverSocketChannel.bind(new InetSocketAddress(9998)); //绑定端口
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            int select = 0;
            try {
                select = selector.select(1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (select == 0) {
                System.out.println("服务器等待了1喵");
                continue;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    try {
                        SocketChannel accept = serverSocketChannel.accept();
                        accept.register(selector, SelectionKey.OP_ACCEPT, ByteBuffer.allocate(1024));
                        accept.register(selector, SelectionKey.OP_READ);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    try {
                        channel.read(buffer);
                        buffer.flip();
                        String read = new String(buffer.array(), 0, buffer.limit());
                        System.out.println(read);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
