package cn.jarkata.netcross.server.handler;

import cn.jarkata.netcross.server.cache.ChannelCache;
import cn.jarkata.netcross.wrap.MessageWrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class TCPProxyHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(TCPProxyHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        logger.info("ContextChannel={}", ctx);
        try {
            String message = buf.toString(StandardCharsets.UTF_8);
            String firstLine = message.split("\\n")[0];
            if (firstLine.startsWith("GET") || firstLine.startsWith("POST")) {
                ChannelCache.setBrowsChannel(ctx);
                //
                ChannelHandlerContext clientChannel = ChannelCache.getClientProxyChannel();
                logger.info("Ctx:{}", clientChannel);
                SocketAddress socketAddress = clientChannel.channel().remoteAddress();
                logger.info("RemoteSocketAddress={}", socketAddress);
                MessageWrap messageWrap = new MessageWrap("server-request", message);
                writeAndFlush(clientChannel, messageWrap);
                return;
            }
            MessageWrap decode = MessageWrap.valueOf(message);
            logger.info("Buffer:{}", decode);
            String head = decode.getHead();
            //客户端
            if (head.startsWith("client-connect")) {
                ChannelCache.setClientProxyChannel(ctx);
                writeAndFlush(ctx, new MessageWrap("server-response", "success"));
            } else if (head.startsWith("client-response")) {
                String body = decode.getBody();
                ChannelHandlerContext browsChannel = ChannelCache.getBrowsChannel();
                writeAndFlush(browsChannel, body.getBytes(StandardCharsets.UTF_8));
                writeAndFlush(ctx, new MessageWrap("server-response", "success"));
            }

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


    public void writeAndFlush(ChannelHandlerContext channel, MessageWrap messageWrap) {
        writeAndFlush(channel, messageWrap.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void writeAndFlush(ChannelHandlerContext channel, byte[] message) {
        ByteBuf buffer = null;
        try {
            buffer = PooledByteBufAllocator.DEFAULT.directBuffer();
            buffer.writeBytes(message);
            channel.writeAndFlush(buffer).addListener((listener) -> {
                boolean success = listener.isSuccess();
                logger.info("Result={}", success);
                Throwable cause = listener.cause();
                logger.info("原因：", cause);
            });
        } finally {

        }
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
