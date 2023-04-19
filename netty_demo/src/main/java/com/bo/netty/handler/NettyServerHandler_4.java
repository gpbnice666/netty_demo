package com.bo.netty.handler;

import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 错误
 * @Author: gpb
 * @Date: 2023/4/19 11:40
 * @Description:
 */
public class NettyServerHandler_4 extends SimpleChannelInboundHandler<UserPOJO.user> {

    private Map<Integer, List<UserPOJO.user>> dataMap = new ConcurrentHashMap<>();
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user msg) throws Exception {
        // 将同一个UID的数据放到同一个List中
        int uid = msg.getUid();
        List<UserPOJO.user> dataList = dataMap.computeIfAbsent(uid, k -> new ArrayList<>());
        synchronized (dataList) {
            dataList.add(msg);
        }
        // 使用线程池处理同一个UID的数据
        executor.execute(() -> {
            synchronized (dataList) {
                for (UserPOJO.user user : dataList) {
                    // 处理该UID的所有数据包的逻辑
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(500));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // 处理消息
                    System.out.println(Thread.currentThread().getName() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
                    ctx.writeAndFlush(Unpooled.copiedBuffer(Thread.currentThread().getName() + " 发送：hello，客户端", CharsetUtil.UTF_8));
                }
                dataList.clear();
            }
        });
    }
}
