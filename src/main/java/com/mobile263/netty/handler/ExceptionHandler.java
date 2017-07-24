package com.mobile263.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

/**
 * ExceptionHandler
 *
 * @author hank
 * @create 2017-07-18 11:33
 **/
@Component
@ChannelHandler.Sharable
public class ExceptionHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出现异常："+cause.getMessage());
        cause.printStackTrace();
        System.out.println("关闭连接...");
        ctx.channel().close().sync();
        System.out.println("连接已关闭");
    }
}
