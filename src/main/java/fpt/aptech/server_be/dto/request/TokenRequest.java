package fpt.aptech.server_be.dto.request;

public class TokenRequest {

    private String channelName;
    private int uid;

    // Getters and setters
    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
