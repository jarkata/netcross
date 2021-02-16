package cn.jarkata.netcross.server;

import cn.jarkata.commons.concurrent.NamedThreadFactory;
import cn.jarkata.netcross.server.handler.TCPProxyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

public class NetCrossServer {

    private final int port;
    private final ServerBootstrap serverBootstrap;

    public NetCrossServer(int port) {
        this.port = port;
        this.serverBootstrap = new ServerBootstrap();
        init();
    }

    private void init() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1, new NamedThreadFactory("boss-group"));
        EventLoopGroup workLoopGroup = new NioEventLoopGroup(12, new NamedThreadFactory("work-group"));
        serverBootstrap.group(bossLoopGroup, workLoopGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(30, 30, 30));
                        pipeline.addLast(new LoggingHandler());
                        pipeline.addLast(new TCPProxyHandler());
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }));
    }

    public void start() throws Exception {
        ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(port)).sync();
        channelFuture.addListener((listener) -> {
            System.out.println("启动结果：" + listener.isSuccess());
        });
    }
}
