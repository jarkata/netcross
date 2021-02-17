package cn.jarkata.netcross.client;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.netcross.client.handle.NetCrossProxyHandler;
import cn.jarkata.netcross.wrap.MessageWrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class NetCrossClient {

    private final Logger logger = LoggerFactory.getLogger(NetCrossClient.class);

    private final InetSocketAddress remoteSocketAddress;
    private final NetCrossProxyHandler proxyHandler;


    public NetCrossClient(String host, int port) {
        this(new InetSocketAddress(host, port));
    }

    public NetCrossClient(InetSocketAddress remoteSocketAddress) {
        this.remoteSocketAddress = remoteSocketAddress;
        proxyHandler = new NetCrossProxyHandler();
    }

    public String send(ByteBuf buffer) throws Exception {
        connected();
        Channel channel = proxyHandler.getChannel();
        channel.writeAndFlush(buffer);
        return proxyHandler.getMessage();
    }

    public String send(MessageWrap messageWrap) throws Exception {
        connected();
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.directBuffer();
        Channel channel = proxyHandler.getChannel();
        channel.writeAndFlush(buffer.writeBytes(messageWrap.toString().getBytes(StandardCharsets.UTF_8)));
        return proxyHandler.getMessage();
    }

    public void connected() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1, new NamedThreadFactory("client")));
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LoggingHandler());
                        pipeline.addLast(new IdleStateHandler(15, 15, 15));
                        pipeline.addLast(proxyHandler);
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(remoteSocketAddress).sync();
        channelFuture.addListener((listener) -> logger.info("连接成功{}", listener.isSuccess()));
    }
}
