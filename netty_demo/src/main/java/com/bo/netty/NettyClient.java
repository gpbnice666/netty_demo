package com.bo.netty;

import com.bo.netty.handler.NettyClientHandler;
import com.bo.netty.protobuf.UserPOJO;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.util.CharsetUtil;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
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
