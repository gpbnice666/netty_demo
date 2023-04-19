package com.bo.netty.handler;

import com.bo.netty.common.TaskManager;
import com.bo.netty.protobuf.UserPOJO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.*;

/**
 * 错误
 */
public class NettyServerHandler_5 extends SimpleChannelInboundHandler<UserPOJO.user> {

    private ExecutorService executorService;

    private Boolean flag = Boolean.TRUE;

    // 使用ConcurrentHashMap存放每个UID对应的任务队列
    private ConcurrentHashMap<Integer, LinkedBlockingQueue<Runnable>> taskMap = TaskManager.getInstance().getTaskMap();

    public NettyServerHandler_5(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user user) throws Exception {
        int uid = user.getUid();

        if (!taskMap.containsKey(uid)) {
            // 如果该UID还没有相应的任务队列，则创建一个新的队列并加入到map中
            taskMap.put(uid, new LinkedBlockingQueue<>());
        }
        taskMap.get(uid).put(() -> {
            // 线程池执行任务
            System.out.println(Thread.currentThread().getName() + "正在处理" + user);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        taskMap.get(1).put(() -> {

        });
    }

    public void runTask() {
        while (flag) {

        }
    }
}
