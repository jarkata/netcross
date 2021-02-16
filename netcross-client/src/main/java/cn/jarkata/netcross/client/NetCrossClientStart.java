package cn.jarkata.netcross.client;

import cn.jarkata.netcross.wrap.MessageWrap;

import java.util.HashMap;
import java.util.Map;

public class NetCrossClientStart {

    public static void main(String[] args) throws Exception {
        NetCrossClient client = new NetCrossClient("localhost", 8089);
        Map<String, String> map = new HashMap<>();
        map.put("side", "client");
        map.put("host", "localhost:8080");
//        ByteBuf response = client.send(Unpooled.copiedBuffer("client|localhost:8080|".getBytes(StandardCharsets.UTF_8)));
        client.send(new MessageWrap("client", "1235"));

//        System.out.println("Response:" + response.toString(StandardCharsets.UTF_8));
    }

}
