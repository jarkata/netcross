package cn.jarkata.netcross.wrap;

import java.io.Serializable;

public class MessageWrap implements Serializable {
    private final String head;
    private final String body;

    public MessageWrap(String head, String body) {
        this.head = head;
        this.body = body;
    }

    public String getHead() {
        return head;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return head + '|' + body + "|#|";
    }

    public static MessageWrap valueOf(String message) {
        String[] messageSplit = message.split("\\|");
        if (messageSplit.length < 2) {
            throw new IllegalArgumentException("响应报文不合法");
        }
        String head = messageSplit[0];
        String body = messageSplit[1];
        return new MessageWrap(head, body);
    }
}
