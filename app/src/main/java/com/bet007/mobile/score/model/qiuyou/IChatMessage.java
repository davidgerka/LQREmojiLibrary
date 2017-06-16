package com.bet007.mobile.score.model.qiuyou;

/**
 *
 */
public interface IChatMessage {


    //itemType来区分是接收还是发送消息
    public static final int MESSAGE_FROM = 1;// 接收消息
    public static final int MESSAGE_TO = 2;// 发送消息

    public static final int TYPE_TIPS = 0;// 系统消息，如时间，某某人加入群
    public static final int TYPE_FROM_ACTION = 1;// 事件、活动，如某某参与了模拟记分
    public static final int TYPE_FROM_TEXT = 2;// 普通文本信息
    public static final int TYPE_FROM_PIC = 3;// 图像
    public static final int TYPE_FROM_RECORD = 4;// 语音
    public static final int TYPE_TO_ACTION = 5;// 事件、活动，如某某参与了模拟记分
    public static final int TYPE_TO_TEXT = 6;// 普通文本信息
    public static final int TYPE_TO_PIC = 7;// 图像
    public static final int TYPE_TO_RECORD = 8;// 语音

    //消息状态
    public static final int STATE_CREATED = 0;// 消息创建成功
    public static final int STATE_GOING = 1;// 发送中、接收中
    public static final int STATE_SUCCEED = 2;// 成功
    public static final int STATE_FAILED = 3;// 失败

    int getDirection(); //接收还是发送
    int getId();
    long getMessageId();
    long getCreateTime();
    int getStateCode();
    int getGroupId();
    String getGroupName();
    String getGroupAvatar();
    int getSenderId();
    String getSenderName();
    String getSenderAvatar();
    int getReceiverId();
    String getReceiverName();
    String getReceiverAvatar();
    int getMessageType();
    String getContentText();
    int getEventMsgUserId();
    int getDuration();
    int getThumbWidth();
    int getThumbHeight();
    boolean isRead();


}
