package cn.jarkata.netcross.client.cache;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ChannelCache {

    private static final AtomicReference<ChannelHandlerContext> browsChannel = new AtomicReference<>();

    private static final AtomicReference<ChannelHandlerContext> clientProxyChannel = new AtomicReference<>();

    public ChannelCache() {
    }

    public static void setClientProxyChannel(ChannelHandlerContext channel) {
        Objects.requireNonNull(channel, "channel不可以为空");
        clientProxyChannel.set(channel);
    }

    public static void setBrowsChannel(ChannelHandlerContext channel) {
        Objects.requireNonNull(channel, "channel不可以为空");
        browsChannel.set(channel);
    }

    public static ChannelHandlerContext getBrowsChannel() {
        return browsChannel.get();
    }

    public static ChannelHandlerContext getClientProxyChannel() {
        return clientProxyChannel.get();
    }
}
