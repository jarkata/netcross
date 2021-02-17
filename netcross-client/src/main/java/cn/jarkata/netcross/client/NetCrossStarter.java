package cn.jarkata.netcross.client;

import cn.jarkata.netcross.wrap.MessageWrap;

public class NetCrossStarter {

    public static void main(String[] args) throws Exception {
        NetCrossClient client = new NetCrossClient("localhost", 8089);
        client.send(new MessageWrap("client-connect", "localhost:8080"));
    }

}
