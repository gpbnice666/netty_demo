package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: gpb
 * @Date: 2023/4/18 11:52
 * @Description:
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<UserPOJO.user> {


    private final ConcurrentHashMap<Integer, ExecutorService> uidThreadMap = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user)  {
/*        executorService.execute(()->{
            synchronized (user){

            }
        });*/
        int uid = user.getUid();
        ExecutorService executorService = uidThreadMap.computeIfAbsent(uid, k -> Executors.newSingleThreadExecutor());
        executorService.execute(() -> process(user));
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端", CharsetUtil.UTF_8));
    }


    private void process(UserPOJO.user user) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 处理数据
        System.out.println(Thread.currentThread().getName() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
    }

    /***
     * 这个方法会在发生异常时触发
     * exceptionCaught() 事件处理方法是当出现 Throwable 对象才会被调用，即当 Netty 由于 IO
     * 错误或者处理器在处理事件时抛出的异常时。在大部分情况下，捕获的异常应该被记录下来 并且把关联的 channel
     * 给关闭掉。然而这个方法的处理方式会在遇到不同异常的情况下有不 同的实现，
     * 比如你可能想在关闭连接之前发送一个错误码的响应消息。
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 出现异常就关闭
        ctx.close();
    }

    //    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg instanceof ByteBuf) {
//            ByteBuf buf = (ByteBuf) msg;
//            // 解析字节数组
//            int messageType = buf.readInt();
//            byte[] data = new byte[buf.readableBytes()];
//            buf.readBytes(data);
//            IMessage message = ProtobufUtil.deserialize(messageType, data);
//
//            // 处理消息
//            if (message instanceof MyMessage) {
//                MyMessage myMessage = (MyMessage) message;
//                // 加入任务队列
//                executorService.submit(() -> {
//                    // 处理消息，保证同一个UID的数据同时只占用一个线程
//                    synchronized (uids) {
//                        int uid = myMessage.getUid();
//                        if (!uids.containsKey(uid)) {
//                            uids.put(uid, new Object());
//                        }
//                        synchronized (uids.get(uid)) {
//                            handleMyMessage(myMessage);
//                        }
//                    }
//                });
//            }
//        }
//    }
//
//    private void handleMyMessage(MyMessage message) {
//        // 处理消息
//    }
}