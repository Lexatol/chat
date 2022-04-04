package com.geekbrains.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Network {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private SocketChannel channel;

    public Network(Callback onMessageReceivedCallback) {
        //запускаем в отдельном потоке
        Thread thread = new Thread(()->{
            //к клиенту никто не подключается, поэтому ему нужен только один пул потоков, только на обработку
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                socketChannel.pipeline().addLast(
                                        new StringDecoder(),
                                        new StringEncoder(),
                                        new ClientHandler(onMessageReceivedCallback));
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync(); //чтобы канал клиента не закрылся сразу ставим ожидание и ждем команты на зактрытия
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }

    public void close() {
        channel.close();
    }
}
