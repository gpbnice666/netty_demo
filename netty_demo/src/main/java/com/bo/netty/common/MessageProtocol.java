package com.bo.netty.common;

public class MessageProtocol {
    /**
     * 消息长度
     */
    private int len;

    /**
     * 消息内容
     */
    private byte[] content;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
