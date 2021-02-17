package cn.jarkata.netcross.client.handle;

import cn.jarkata.netcross.client.NetCrossClient;
import cn.jarkata.netcross.wrap.MessageWrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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

    private String message;

    public NetCrossProxyHandler() {
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //文本传输协议
        this.message = ((ByteBuf) msg).toString(StandardCharsets.UTF_8);
        logger.info("响应之后的数据：{}", message);
        String firstLine = message.split("\\n")[0];
        //发送单纯的http请求
        if (firstLine.startsWith("GET") || firstLine.startsWith("POST") || firstLine.startsWith("HTTP")) {
            count.countDown();
            return;
        }
        MessageWrap messageWrap = MessageWrap.valueOf(this.message);
        String head = messageWrap.getHead();
        if (head.startsWith("server-request")) {
            NetCrossClient client = new NetCrossClient("localhost", 8080);
            String response = client.send(Unpooled.copiedBuffer(messageWrap.getBody().getBytes(StandardCharsets.UTF_8)));
            ctx.writeAndFlush(Unpooled.copiedBuffer(new MessageWrap("client-response", response).toString().getBytes(StandardCharsets.UTF_8)));
        } else if (head.startsWith("server-response")) {
            count.countDown();
        }
    }

    public String getMessage() throws InterruptedException {
        count.await();
        return this.message;
    }

    public Channel getChannel() throws Exception {
        return this.channel;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("HandlerException:" + ctx, cause);
    }
}
