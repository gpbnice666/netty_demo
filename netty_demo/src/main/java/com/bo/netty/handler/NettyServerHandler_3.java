package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 有问题
 * @Author: gpb
 * @Date: 2023/4/18 17:00
 * @Description:
 */
public class NettyServerHandler_3 extends SimpleChannelInboundHandler<UserPOJO.user> {

    private ConcurrentHashMap<Integer, Thread> threadMap = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Override
    protected  void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user) throws Exception {
        int uid = user.getUid();
        Thread thread = threadMap.computeIfAbsent(uid, k -> new Thread(() -> {
            try {
                // 处理该UID的所有数据包的逻辑
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
                // 处理消息
                System.out.println(Thread.currentThread().getName() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
                ctx.writeAndFlush(Unpooled.copiedBuffer(Thread.currentThread().getName() + " 发送：hello，客户端", CharsetUtil.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        executorService.submit(() -> thread.run());
    }

}
