package cn.jarkata.netcross.server.handler;

import cn.jarkata.netcross.client.NetCrossClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TcpProxyHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(TcpProxyHandler.class);
    private final NetCrossClient netCrossClient;

    public TcpProxyHandler() {
        netCrossClient = new NetCrossClient("www.oschina.net", 80);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
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
            logger.info("ResponseContext:{}", builder.toString());
            ByteBuf byteBuf = netCrossClient.send(Unpooled.copiedBuffer(builder.toString().getBytes(StandardCharsets.UTF_8)));
            ctx.writeAndFlush(byteBuf);
        } finally {
            if (ReferenceCountUtil.refCnt(buf) > 0) {
                ReferenceCountUtil.release(buf);
            }
        }
    }
}
