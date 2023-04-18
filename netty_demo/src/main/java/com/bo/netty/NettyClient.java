package com.bo.netty;

import com.bo.netty.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * 1，使用Netty搭建一个服务器。
 * 2，使用Netty搭建一个客户端。
 * 3，客户端发送字符串，服务器端打印。
 * 4，了解google的protobuf工具库。
 * 6，客户端使用protobuf发送一个数据，包含两个字段：uid=10以内的随机，index=自增长整数。
 * 7，服务器解析出这个数据包并打印内容。
 * 8，客户端同时发送1000个包含上述两个字段的随机数据的数据包，服务器能成功接收并打印。
 * 9，服务器使用线程池，可以并行的处理这1000条数据。
 * 10，保证同一个UID的数据同时只占用一个线程。
 * @Author: gpb
 * @Date: 2023/4/18 11:54
 * @Description:
 */
public class NettyClient {

    public static void main(String[] args) throws Exception {
        // 线程工作组
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        // 客户端启动类
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch)  {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ProtobufEncoder());
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        try {
            System.out.println("客户端启动，端口：9090");
            ChannelFuture sync = bootstrap.connect("127.0.0.1",9090).sync();
            sync.channel().closeFuture().sync();
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
