package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: gpb
 * @Date: 2023/4/18 16:35
 * @Description:
 */
public class NettyServerHandler_2 extends SimpleChannelInboundHandler<UserPOJO.user> {

    private ExecutorService executor;
    private Object[] locks;

    public NettyServerHandler_2() {
        executor = Executors.newFixedThreadPool(10);
        locks = new Object[10];
        for (int i = 0; i < 10; i++) {
            locks[i] = new Object();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user) throws Exception {
        int uid = user.getUid();
        // 创建任务并提交到线程池中
        executor.submit(() -> {
            handle(ctx, user);
        });
    }

    private void handle(ChannelHandlerContext ctx, UserPOJO.user user) {
        int uid = user.getUid();
        synchronized (locks[uid]) {
            // 处理消息
            System.out.println(Thread.currentThread().getName() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
            ctx.writeAndFlush(Unpooled.copiedBuffer(Thread.currentThread().getName() + " 发送：hello，客户端", CharsetUtil.UTF_8));
        }
    }
}
