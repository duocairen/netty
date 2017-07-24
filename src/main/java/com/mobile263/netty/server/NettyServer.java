package com.mobile263.netty.server;

import com.mobile263.netty.handler.HttpAggregatorInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * a simple realization of http server with netty
 *
 * @author hank
 * @create 2017-07-18 9:54
 **/
@Component
public class NettyServer {

    private static final String EPOLL = "epoll";
    private static final String NIO = "nio";

    private Channel serverChannel = null;
    private EventLoopGroup parentGroup = null;
    private EventLoopGroup childGroup = null;
    private Class channelClass = null;

    @Value("${netty.port}")
    private int port;
    @Value("${netty.io}")
    private String nettyIO;

    @Autowired
    private HttpAggregatorInitializer initializer;

    @PostConstruct
    public void start() {

        //init EventLoopGroup and ServerSocketChannel type according to configuration.
        if (EPOLL.equalsIgnoreCase(nettyIO)) {
            parentGroup = new EpollEventLoopGroup();
            childGroup = new EpollEventLoopGroup();
            channelClass = EpollServerSocketChannel.class;
        } else if (NIO.equalsIgnoreCase(nettyIO)) {
            parentGroup = new NioEventLoopGroup();
            childGroup = new NioEventLoopGroup();
            channelClass = NioServerSocketChannel.class;
        } else {
            parentGroup = new OioEventLoopGroup();
            childGroup = new OioEventLoopGroup();
            channelClass = OioServerSocketChannel.class;
        }

        Runnable starter = () -> {
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(parentGroup, childGroup).channel(channelClass)
                        .localAddress(new InetSocketAddress(port))
                        .childHandler(initializer);
                ChannelFuture future = bootstrap.bind().sync();
                System.out.println("server starts to listen on " + future.channel().localAddress());
                serverChannel = future.channel();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(starter).start();

    }


    @PreDestroy
    public void shutdown() {
        System.out.println("server is shutting down...");
        if (serverChannel != null) {
            serverChannel.close();
            try {
                parentGroup.shutdownGracefully().sync();
                childGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("server shutdown gracefully.");
        }
    }


}
