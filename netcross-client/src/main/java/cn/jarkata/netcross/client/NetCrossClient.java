package cn.jarkata.netcross.client;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.netcross.client.handle.NetCrossProxyHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NetCrossClient {

    private final Logger logger = LoggerFactory.getLogger(NetCrossClient.class);

    private final InetSocketAddress remoteSocketAddress;
    private final NetCrossProxyHandler proxyHandler;

    public NetCrossClient(String host, int port) {
        this.remoteSocketAddress = new InetSocketAddress(host, port);
        proxyHandler = new NetCrossProxyHandler();
    }

    public ByteBuf send(ByteBuf buffer) throws Exception {
        connected();
        Channel channel = proxyHandler.getChannel();
        channel.writeAndFlush(buffer);
        return proxyHandler.getResponseData();
    }


    private void connected() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1, new NamedThreadFactory("client")));
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(), proxyHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(remoteSocketAddress).sync();
        channelFuture.addListener((listener) -> {
            logger.info("连接成功{}", listener.isSuccess());
        });
    }
}
