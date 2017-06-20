package com.bet007.mobile.score.model.qiuyou;


import android.os.Parcel;
import android.os.Parcelable;

import com.handmark.pulltorefresh.library.BaseModelInfo;

/**
 * 聊天消息model
 */
public class ChatMessageModel extends BaseModelInfo implements IChatMessage, Parcelable {

    public static final Creator<ChatMessageModel> CREATOR = new Creator<ChatMessageModel>() {
        @Override
        public ChatMessageModel createFromParcel(Parcel source) {
            return new ChatMessageModel(source);
        }

        @Override
        public ChatMessageModel[] newArray(int size) {
            return new ChatMessageModel[size];
        }
    };
    private int id;// 消息ID，发送消息时先生成
    private long messageId;// 服务端的消息ID
    private long createTime;// 创建时间
    private boolean isRead;// 是否已读
    private int stateCode;// 状态码
    /* 如果senderId为空，则发送者为group，如果receiverId为空，则接收者为group */
    private int groupId; //群组ID
    private String groupName;// 群组名称
    private String groupAvatar;// 群组头像
    private int senderId;//发送者ID
    private String senderName;// 发送者名称
    private String senderAvatar;// 发送者头像地址
    private int receiverId;//发送者ID
    private String receiverName;// 发送者名称
    private String receiverAvatar;// 发送者头像地址
    /**
     * 消息体类型，如：IChatMessage.TYPE_TIPS
     */
    private int messageType;
    private String contentText; //文本内容，可用于通知消息、提示消息、事件消息、文本消息、图片消息原图URL、语音消息文件URL
    private int eventMsgUserId; //事件消息的内容体的用户ID
    private int duration; //语音消息时间长，单位：秒
    private int thumbWidth; //缩略图宽
    private int thumbHeight;//缩略图高

    public ChatMessageModel() {
        messageId = System.currentTimeMillis();
    }

    public ChatMessageModel(boolean isFrom) {

    }

    protected ChatMessageModel(Parcel in) {
        this.id = in.readInt();
        this.messageId = in.readLong();
        this.createTime = in.readLong();
        this.isRead = in.readByte() != 0;
        this.stateCode = in.readInt();
        this.groupId = in.readInt();
        this.groupName = in.readString();
        this.groupAvatar = in.readString();
        this.senderId = in.readInt();
        this.senderName = in.readString();
        this.senderAvatar = in.readString();
        this.receiverId = in.readInt();
        this.receiverName = in.readString();
        this.receiverAvatar = in.readString();
        this.messageType = in.readInt();
        this.contentText = in.readString();
        this.eventMsgUserId = in.readInt();
        this.duration = in.readInt();
        this.thumbWidth = in.readInt();
        this.thumbHeight = in.readInt();
        this.itemType = in.readInt();
    }

    public static ChatMessageModel getTipsMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 99;
        chatMessageModel.receiverName = "99";
        chatMessageModel.messageType = TYPE_TIPS;
        chatMessageModel.contentText = "武球王有打了个飞机";
        return chatMessageModel;
    }

    public static ChatMessageModel getFromActionMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 99;
        chatMessageModel.receiverName = "99";
        chatMessageModel.messageType = TYPE_FROM_ACTION;
        chatMessageModel.contentText = "卡卡参加了模拟比分";
        return chatMessageModel;
    }

    public static ChatMessageModel getFromTextMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 100;
        chatMessageModel.receiverName = "100";
        chatMessageModel.messageType = TYPE_FROM_TEXT;
        chatMessageModel.contentText = "足球比赛中";//，最好的得分机会莫过于空门了。但有许多情况，不知是球员心理波动还是场地因素，面对空门机会他们却把握不住。";
        return chatMessageModel;
    }

    public static ChatMessageModel getFromRecordMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 101;
        chatMessageModel.receiverName = "101";
        chatMessageModel.messageType = TYPE_FROM_RECORD;
        chatMessageModel.contentText = "足球比赛中";
        chatMessageModel.setRead(false);
        chatMessageModel.setDuration(0);
        return chatMessageModel;
    }

    public static ChatMessageModel getFromPicMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 102;
        chatMessageModel.receiverName = "102";
        chatMessageModel.messageType = TYPE_FROM_PIC;
        chatMessageModel.contentText = "足球比赛中";
        chatMessageModel.setThumbWidth(200);
        chatMessageModel.setThumbHeight(300);
        chatMessageModel.setStateCode(IChatMessage.STATE_SUCCEED);
        return chatMessageModel;
    }

    public static ChatMessageModel getToActionMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 199;
        chatMessageModel.receiverName = "199";
        chatMessageModel.messageType = TYPE_TO_ACTION;
        chatMessageModel.contentText = "利物浦参加了欧冠决赛";
        return chatMessageModel;
    }

    public static ChatMessageModel getToTextMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_TO;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 1999;
        chatMessageModel.receiverName = "1999";
        chatMessageModel.messageType = TYPE_TO_TEXT;
        chatMessageModel.contentText = "首先提名“空门不进帝”罗比尼奥。职业生涯后期，罗比尼奥的信心有了很大的下滑，导致屡屡酿成空门不进的后果。有的偏了，有的高了，即使到了中超也有过面对空门却打在边网上的尴尬。";
        return chatMessageModel;
    }

    public static ChatMessageModel getToRecordMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 1101;
        chatMessageModel.receiverName = "1101";
        chatMessageModel.messageType = TYPE_TO_RECORD;
        chatMessageModel.contentText = "足球比赛中";
        chatMessageModel.setRead(true);
        chatMessageModel.setDuration(25);
        return chatMessageModel;
    }

    public static ChatMessageModel getToPicMsg() {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.itemType = MESSAGE_FROM;
        chatMessageModel.createTime = System.currentTimeMillis();
        chatMessageModel.senderId = 1102;
        chatMessageModel.receiverName = "1102";
        chatMessageModel.messageType = TYPE_TO_PIC;
        chatMessageModel.contentText = "足球比赛中";
        chatMessageModel.setThumbWidth(100);
        chatMessageModel.setThumbHeight(150);
        chatMessageModel.setStateCode(IChatMessage.STATE_GOING);
        return chatMessageModel;
    }

    public int getDirection(){
        return itemType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupAvatar() {
        return groupAvatar;
    }

    public void setGroupAvatar(String groupAvatar) {
        this.groupAvatar = groupAvatar;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAvatar() {
        return receiverAvatar;
    }

    public void setReceiverAvatar(String receiverAvatar) {
        this.receiverAvatar = receiverAvatar;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getEventMsgUserId() {
        return eventMsgUserId;
    }

    public void setEventMsgUserId(int eventMsgUserId) {
        this.eventMsgUserId = eventMsgUserId;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.messageId);
        dest.writeLong(this.createTime);
        dest.writeByte(this.isRead ? (byte) 1 : (byte) 0);
        dest.writeInt(this.stateCode);
        dest.writeInt(this.groupId);
        dest.writeString(this.groupName);
        dest.writeString(this.groupAvatar);
        dest.writeInt(this.senderId);
        dest.writeString(this.senderName);
        dest.writeString(this.senderAvatar);
        dest.writeInt(this.receiverId);
        dest.writeString(this.receiverName);
        dest.writeString(this.receiverAvatar);
        dest.writeInt(this.messageType);
        dest.writeString(this.contentText);
        dest.writeInt(this.eventMsgUserId);
        dest.writeInt(this.duration);
        dest.writeInt(this.thumbWidth);
        dest.writeInt(this.thumbHeight);
        dest.writeInt(this.itemType);
    }
}
