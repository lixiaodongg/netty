package com.shadow.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private SocketChannel socketChannel;
    private Selector selector;
    private String userName;
    private Scanner scanner = new Scanner(System.in);
    private ExecutorService service = Executors.newSingleThreadExecutor();

    public Client() {

        try {
            InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8888);
            socketChannel = SocketChannel.open(socketAddress);
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(userName + "is ok");
            service.execute(new ClientThead(socketChannel, selector, userName));
            while (true) {
                String input = scanner.nextLine();
                if (input != null) {
                    sendMsg(input);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        msg += userName + "说：" + msg;
        ByteBuffer wrap = ByteBuffer.wrap(msg.getBytes());
        try {
            socketChannel.write(wrap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ClientThead implements Runnable {

        private SocketChannel socketChannel;
        private Selector selector;
        private String userName;

        public ClientThead(SocketChannel socketChannel, Selector selector, String userName) {
            this.socketChannel = socketChannel;
            this.selector = selector;
            this.userName = userName;
        }

        @Override
        public void run() {
            while (true) {
                readMsg();
            }
        }

        public void readMsg() {
            int read;
            try {
                read = selector.select();
                if (read > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isValid() && key.isReadable()) {
                            readMsg(key);
                        }
                        iterator.remove(); //避免重复
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void readMsg(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = channel.read(buffer);
            String msg = new String(buffer.array(), 0, len);
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
