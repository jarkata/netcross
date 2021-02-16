package cn.jarkata.netcross.client.handle;

import cn.jarkata.netcross.wrap.MessageWrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@ChannelHandler.Sharable
public class NetCrossProxyHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(NetCrossProxyHandler.class);

    private final CountDownLatch count = new CountDownLatch(1);

    private Channel channel;

    private String responseData;

    public NetCrossProxyHandler() {
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        this.responseData = ((ByteBuf) msg).toString(StandardCharsets.UTF_8);
        logger.info("解析之后的数据：{}", MessageWrap.valueOf(this.responseData));
//
//        String message = responseData.toString(StandardCharsets.UTF_8);
//        logger.info("ClientResponse::{}", responseData.toString(StandardCharsets.UTF_8));
//        if (message.indexOf("HTTP/1.1") > 0) {
//            NetCrossClient client = new NetCrossClient("localhost", 8080);
//            ByteBuf buf = client.send(responseData);
//            logger.info("HTTP响应：{}", buf.toString(StandardCharsets.UTF_8));
//            ctx.writeAndFlush(buf);
//        }
        count.countDown();
    }

    public String getResponseData() throws InterruptedException {
        count.await();
        return this.responseData;
    }

    public Channel getChannel() throws Exception {
        return this.channel;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("HandlerException:" + ctx, cause);
    }
}
