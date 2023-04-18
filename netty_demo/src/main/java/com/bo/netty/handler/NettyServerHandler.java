package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: gpb
 * @Date: 2023/4/18 11:52
 * @Description:
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<UserPOJO.user> {


    private final ConcurrentHashMap<Integer, ExecutorService> uidThreadMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user)  {
        int uid = user.getUid();
        ExecutorService executorService = uidThreadMap.computeIfAbsent(uid, k -> Executors.newSingleThreadExecutor());
        executorService.execute(() -> process(user));
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端", CharsetUtil.UTF_8));
    }


    private void process(UserPOJO.user user) {
        // 处理数据
        System.out.println(Thread.currentThread() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
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
}