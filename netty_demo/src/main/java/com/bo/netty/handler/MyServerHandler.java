package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 有问题
 */
public class MyServerHandler extends SimpleChannelInboundHandler<UserPOJO.user> {

    private final ExecutorService executorService;
    private final Map<Integer, Integer> uidToThreadId = new ConcurrentHashMap<>();

    public MyServerHandler() {
        executorService = new ThreadPoolExecutor(10, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user) throws Exception {
        // 处理消息
        int uid = user.getUid();
        int threadId = uidToThreadId.computeIfAbsent(uid, k -> ThreadLocalRandom.current().nextInt(10));
        executorService.submit(() -> handle(ctx, user, threadId));
    }

    private void handle(ChannelHandlerContext ctx, UserPOJO.user user, int threadId) {
        // 处理消息
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 处理数据
        System.out.println(Thread.currentThread() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "] 任务");
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端,我是：" + threadId, CharsetUtil.UTF_8));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
