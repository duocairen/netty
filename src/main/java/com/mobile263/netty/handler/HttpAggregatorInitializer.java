package com.mobile263.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 代码清单 11-3 自动聚合 HTTP 的消息片段
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
@Component
public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private LogicHandler logicHandler;
    @Autowired
    private ExceptionHandler exceptionHandler;
    @Autowired
    private RemindConnHandler remindConnHandler;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (false) {
            //如果是客户端，则添加 HttpClientCodec
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            //如果是服务器，则添加 HttpServerCodec
            pipeline.addLast("codec", new HttpServerCodec());
        }
        //将最大的消息大小为 512 KB 的 HttpObjectAggregator 添加到 ChannelPipeline
        pipeline.addLast("aggregator",
                new HttpObjectAggregator(512 * 1024));
        pipeline.addLast(logicHandler);
        pipeline.addLast(exceptionHandler);
        pipeline.addFirst(remindConnHandler);
    }

}
