package net.octopvp.octocore.common.object.builder;

import io.sentry.protocol.Message;

public class SentryMessageBuilder {
    private String message;

    public SentryMessageBuilder setMessage(String msg){
        this.message = msg;
        return this;
    }
    public Message build(){
        Message message = new Message();
        message.setMessage(this.message);
        return message;
    }
}
