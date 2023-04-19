//package com.bo.netty.handler;
//
//import com.bo.netty.protobuf.UserPOJO;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.protobuf.ProtobufDecoder;
//import io.netty.handler.codec.protobuf.ProtobufEncoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//
//import java.util.Map;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class NettyServer {
//    public static void main(String[] args) throws InterruptedException {
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//
//        try {
//            ServerBootstrap bootstrap = new ServerBootstrap()
//                    .group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
//                            pipeline.addLast(new ProtobufDecoder(UserPOJO.user.getDefaultInstance()));
//                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
//                            pipeline.addLast(new ProtobufEncoder());
//                            pipeline.addLast(new ExecutionHandler(new ThreadPoolExecutor(
//                                    10,
//                                    10,
//                                    0L,
//                                    TimeUnit.MILLISECONDS,
//                                    new LinkedBlockingQueue<>(),
//                                    new ThreadFactoryBuilder().setNameFormat("worker-%d").build()
//                            ), true, true));
//                            pipeline.addLast(new ServerHandler());
//                        }
//                    });
//
//            Channel channel = bootstrap.bind(8080).sync().channel();
//            channel.closeFuture().sync();
//        } finally {
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//    private static class ServerHandler extends SimpleChannelInboundHandler<UserPOJO.user> {
//        private static final Map<Integer, Integer> uidToThreadIndexMap = new ConcurrentHashMap<>();
//        private final AtomicInteger sequence = new AtomicInteger();
//
//        @Override
//        protected void channelRead0(ChannelHandlerContext ctx, UserPOJO.user msg) throws Exception {
//            int uid = msg.getUid();
//            int index = msg.getIndex();
//
//            int threadIndex = uidToThreadIndexMap.computeIfAbsent(uid, k -> ThreadLocalRandom.current().nextInt(10));
//            int taskId = sequence.incrementAndGet();
//
//            Runnable task = () -> {
//                System.out.println("Task " + taskId + " processing UID " + uid + " INDEX " + index);
//            };
//
//            ctx.executor().submit(threadIndex, task);
//        }
//    }
//}
