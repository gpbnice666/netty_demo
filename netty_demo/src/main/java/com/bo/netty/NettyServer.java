package com.bo.netty;

import com.bo.netty.common.MessageDecoder;
import com.bo.netty.common.MessageEncoder;
import com.bo.netty.handler.NettyServerHandler;
import com.bo.netty.protobuf.UserPOJO;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.util.CharsetUtil;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: gpb
 * @Date: 2023/4/18 11:47
 * @Description:
 */
public class NettyServer {


    public static void main(String[] args) {
        // 用于接收客户端连接的线程工作组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于对接收客户端连接读写操作的线程工作中
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 服务端启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup) // 绑定两个工作线程组
                    .channel(NioServerSocketChannel.class) // 设置NIO的模型
                    .option(ChannelOption.SO_BACKLOG,1024) // 设置tcp缓冲区大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024) // 设置发送数据的缓冲大小
                    .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 为通道进行初始化 数据传输过来的时候会进行拦截和执行
                            ChannelPipeline pipeline = ch.pipeline();
                            // protobuf数据 设置编码格式
                            pipeline.addLast(new ProtobufDecoder(UserPOJO.user.getDefaultInstance()));
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("服务器启动，端口：9090");
            // 绑定端口启动
            ChannelFuture sync = serverBootstrap.bind(9090).sync();
            // 释放
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 接收客户端的线程组进行释放
            bossGroup.shutdownGracefully();
            // 用于处理客户端读取的线程组进行释放
            workerGroup.shutdownGracefully();
        }
    }
}
