package com.bo.netty.handler;

import com.bo.netty.protobuf.UserDTO;
import com.bo.netty.protobuf.UserPOJO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.*;

/**
 * 正确
 *
 * @Author: gpb
 * @Date: 2023/4/19 14:02
 * @Description:
 */
public class NettyServerHandler_6 extends SimpleChannelInboundHandler<UserPOJO.user> {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(10);

    // QUEUE_MAP
    private static ConcurrentHashMap<Integer, BlockingQueue<UserDTO>> QUEUE_MAP = new ConcurrentHashMap<>();

    static {
        QUEUE_MAP.put(0, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(1, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(2, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(3, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(4, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(5, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(6, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(7, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(8, new LinkedBlockingQueue<>());
        QUEUE_MAP.put(9, new LinkedBlockingQueue<>());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user msg) throws Exception {
        int uid = msg.getUid();
        BlockingQueue<UserDTO> userBlockingQueue = QUEUE_MAP.computeIfAbsent(uid, k -> new LinkedBlockingQueue<>());
        UserDTO userDTO = new UserDTO();
        userDTO.setUser(msg);
        userDTO.setCtx(ctx);
        userBlockingQueue.add(userDTO);
    }

    /**
     * 消费者
     */
    public static void runConsumer() {
        QUEUE_MAP.forEach((key, value) -> {
            THREAD_POOL.execute(new Consumer(value));
        });
    }


    static class Consumer implements Runnable {
        private final BlockingQueue<UserDTO> queue;
        public Consumer(BlockingQueue<UserDTO> queue) {
            this.queue = queue;
        }
        @Override
        public void run() {
            try {
                // 阻塞消费
                while (true) {
                    UserDTO num = queue.take();
                    consume(num);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void consume(UserDTO userDTO) throws Exception {
            // 处理该UID的所有数据包的逻辑
            ChannelHandlerContext ctx = userDTO.getCtx();
            UserPOJO.user user = userDTO.getUser();
            // 模拟业务处理时间
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
            // 消费数据的代码
            System.out.println(Thread.currentThread().getName() + "正在执行：[" + user.getUid() + "] 用户的第[" + user.getIndex() + "]任务");
            ctx.writeAndFlush(Unpooled.copiedBuffer(Thread.currentThread().getName() + " 发送：hello，客户端", CharsetUtil.UTF_8));
        }
    }
}
