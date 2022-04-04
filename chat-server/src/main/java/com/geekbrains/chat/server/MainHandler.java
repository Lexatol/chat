package com.geekbrains.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

public class MainHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>(); //создаем список всех подключившихся каналов для дальнейшей рассылки сообщенией
    private static int newClientIndex = 1;
    private String clientName;


    @Override // подключение клиента
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился: " + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент №" + newClientIndex;
        newClientIndex++;
        broadcastMessage("SERVER", "Подключился новый клиент " + clientName);
    }
    

//    @Override //срабатывает когда клиент прислал сообщение
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //Стоим первые от сети, поэтому обязательно получим байт буффер в msg
//        ByteBuf buf = (ByteBuf) msg;
//        while(buf.readableBytes() > 0) { //пока не прочитали все байты из буффера
//            System.out.print((char)buf.readByte());
//        }
//        buf.release();//освобождаем буффер, позволяем netty освободить память
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        System.out.println("Получено сообщение " + s);
        if (s.startsWith("/")) {
            if (s.startsWith("/changename")) {
                String nickName = s.split("\\s", 2)[1];
                broadcastMessage("SERVER", "Клиент " + clientName + " сменил ник на " + nickName);
                clientName = nickName;
            }
            return;
        }
        broadcastMessage(clientName, s);

    }

    public void broadcastMessage(String clientName, String message) {
        //формируем сообщение вида [клиент 1]: message
        String out = String.format("[%s]: %s\n", clientName, message);
        for (Channel ch : channels) {
            ch.writeAndFlush(out);
        }
    }

    @Override //при работе с подключившимся клиентом словили исключение, мы его обрабатываем ОБЯЗАТЕЛЬНО ПЕРЕОПРЕДЕЛЯТЬ
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Клиент " + clientName + "отвалился");
        channels.remove(ctx.channel());
        broadcastMessage("SERVER", "Клиент " + clientName + "вышел из сети");
        ctx.close();
    }
}
