package cn.jarkata.netcross.server.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ChannelCache {

    private static final AtomicReference<ChannelHandlerContext> browsChannel = new AtomicReference<>();

    private static final AtomicReference<ChannelHandlerContext> clientProxyChannel = new AtomicReference<>();

    private static final Map<String, ChannelHandlerContext> cache = new ConcurrentHashMap<>();

    public ChannelCache() {
    }

    public static void setClientProxyChannel(ChannelHandlerContext channel) {
        Objects.requireNonNull(channel, "channel不可以为空");
        clientProxyChannel.set(channel);
        cache.put("ClientProxy", channel);
    }

    public static void setBrowsChannel(ChannelHandlerContext channel) {
        Objects.requireNonNull(channel, "channel不可以为空");
        browsChannel.set(channel);
        cache.put("BrowsChannel", channel);
    }

    public static ChannelHandlerContext getBrowsChannel() {
        return cache.get("BrowsChannel");
    }

    public static ChannelHandlerContext getClientProxyChannel() {
        return cache.get("ClientProxy");
    }
}
