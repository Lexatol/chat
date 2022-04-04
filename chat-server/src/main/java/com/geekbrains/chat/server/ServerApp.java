package com.geekbrains.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerApp {

    private static final int PORT = 8189;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//отвечает за подключающихся клиентов
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // пул обработка данных
        try {
            ServerBootstrap b = new ServerBootstrap(); //предназначен для настройки сервака
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { //в сокет канале лежит вся информация
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new MainHandler());

                        }
                    });
            ChannelFuture future = b.bind(PORT).sync(); //сервак стартует на порту 8189 (b.bind()), sync - запуск задачи
            //future нужно чтобы узнавать информацию о серваке
            future.channel().closeFuture().sync();//блокирующая операция, сидим и ждем пока не остановят сервер
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
