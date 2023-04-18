package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 有问题
 *
 * @Author: gpb
 * @Date: 2023/4/18 16:19
 * @Description:
 */
public class NettyServerHandler_1 extends SimpleChannelInboundHandler<UserPOJO.user> {
    private final AtomicInteger count = new AtomicInteger(0);
    private final Executor executor = new ThreadPoolExecutor(
            10,
            10,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> new Thread(r, "MyServerHandler-" + count.incrementAndGet())
    );

    private final ConcurrentHashMap<Integer, Object> uidLocks = new ConcurrentHashMap<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user) throws Exception {
        int uid = user.getUid();
        // 处理业务逻辑
        executor.execute(() -> {
            synchronized (uidLocks) {
                Object lock = uidLocks.computeIfAbsent(uid, k -> new Object());
                synchronized (lock) {
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(Thread.currentThread().getName() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
                    ctx.writeAndFlush(Unpooled.copiedBuffer(Thread.currentThread().getName() + " 发送：hello，客户端", CharsetUtil.UTF_8));
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
