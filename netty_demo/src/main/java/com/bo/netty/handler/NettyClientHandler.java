package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: gpb
 * @Date: 2023/4/18 13:45
 * @Description:
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    AtomicInteger atomicInteger = new AtomicInteger(0);


    /**
     * 当通道就绪的时候会触发
     *
     * @param ctx 上下文对象，管道pipeline 通道 channel 地址
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        atomicInteger.compareAndSet(atomicInteger.get(),
                atomicInteger.get() + 1);
        UserPOJO.user user = UserPOJO.user.newBuilder().setUid(new Random().nextInt(10)).setIndex(atomicInteger.get()).build();
        ctx.writeAndFlush(user);
        super.channelActive(ctx);

//        for (int i = 0; i < 1000; i++) {
//            int uid = ThreadLocalRandom.current().nextInt(10);
//            UserPOJO.user user = UserPOJO.user.newBuilder()
//                    .setUid(uid)
//                    .setIndex(i)
//                    .build();
//            ctx.writeAndFlush(user);
//            channel.writeAndFlush(message);
//        }

    }

    /**
     * 当通道有数据的的是会触发
     *
     * @param ctx 上下文对象，管道pipeline 通道 channel 地址
     * @param msg 消息
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("服务器回复的消息：" + byteBuf.toString(CharsetUtil.UTF_8));
        if (atomicInteger.get() < 1000) {
            atomicInteger.compareAndSet(atomicInteger.get(),
                    atomicInteger.get() + 1);
            UserPOJO.user user = UserPOJO.user.newBuilder().setUid(new Random().nextInt(10)).setIndex(atomicInteger.get()).build();
            ctx.writeAndFlush(user);
        }
        super.channelRead(ctx, msg);
    }
}
