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
        String head = message.split("\\|")[0];
        String body = message.split("\\|")[1];
        return new MessageWrap(head, body);
    }
}
