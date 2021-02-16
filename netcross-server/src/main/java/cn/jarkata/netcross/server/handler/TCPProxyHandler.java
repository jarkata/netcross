package cn.jarkata.netcross.server.handler;

import cn.jarkata.netcross.wrap.MessageWrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TCPProxyHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(TCPProxyHandler.class);

    private static final Map<String, ChannelHandlerContext> cache = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel={}", ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            MessageWrap decode = MessageWrap.valueOf(buf.toString(StandardCharsets.UTF_8));
            System.out.println(decode);
            logger.info("ContextChannel={}", ctx);
            logger.info("Buffer:{}", decode);

//            String message = buf.toString(StandardCharsets.UTF_8);
//            if (message.startsWith("client")) {
//                String host = message.split("\\|")[1];
//                cache.put(host, ctx);
//                ctx.writeAndFlush(Unpooled.copiedBuffer("success".getBytes(StandardCharsets.UTF_8)));
//            } else {
//                ChannelHandlerContext channelHandlerContext = cache.get("localhost:8080");
//                logger.info("Ctx:{}", channelHandlerContext);
//                SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();
//                logger.info("RemoteSocketAddress={}", socketAddress);
//                channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(message.getBytes(StandardCharsets.UTF_8)));
//            }
            ctx.writeAndFlush(msg);
        } finally {
            if (ReferenceCountUtil.refCnt(buf) > 0) {
                ReferenceCountUtil.release(buf);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("异常：" + ctx, cause);
    }

    /**
     * 重组HTTP报文
     *
     * @param buf http报文
     * @return 报文体
     * @throws IOException
     */
    private String remakeBody(ByteBuf buf) throws IOException {
        byte[] dist = new byte[buf.readableBytes()];
        buf.readBytes(dist);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(dist)));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("Host")) {
                builder.append("Host:www.oschina.net");
            } else {
                builder.append(line);
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
