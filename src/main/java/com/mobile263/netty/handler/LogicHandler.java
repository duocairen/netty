package com.mobile263.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * LogicHandler
 *
 * @author hank
 * @create 2017-07-20 10:00
 **/
@ChannelHandler.Sharable
@Component
public class LogicHandler extends ChannelInboundHandlerAdapter {

    @Value("${netty.log}")
    private boolean log;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        FullHttpRequest request = (FullHttpRequest) msg;

        if (log) {
            String body = request.content().toString(CharsetUtil.UTF_8);
            System.out.println("request path:" + request.uri());
            System.out.println("request method: " + request.method().name());
            HttpHeaders headers = request.headers();
            for (String name : headers.names()) {
                System.out.println(name + "：" + headers.get(name));
            }
            System.out.println("request body:" + body);
        }

        //FullHttpRequest为ReferenceCounted类型，记得释放
        ReferenceCountUtil.release(msg);

        String result = "{\"code\":0,\"desc\":\"success\"}";
        ByteBuf buf = Unpooled.copiedBuffer(result, CharsetUtil.UTF_8);
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
                //记得添加content-length，否则浏览器端不知响应内容完毕一直等待
                .set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        ctx.channel().writeAndFlush(response);
    }
}
