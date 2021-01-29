package com.ccit19.merdog_doctor.chat;

public class Message {
    private String text;
    private String memberData;
    private String date;
    private String message_type;
    private boolean belongsToCurrentUser;

    public Message(String text, String name, String date, String message_type, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberData = name;
        this.date = date;
        this.message_type = message_type;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public String getdate() {
        return date;
    }

    public String getMemberData() {
        return memberData;
    }

    public String getMessageType() {
        return message_type;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
