package cn.jarkata.netcross.server;

/**
 * Hello world!
 */
public class ServerStater {

    public static void main(String[] args) throws Exception {
        NetCrossServer starter = new NetCrossServer(8089);
        starter.start();
    }
}
