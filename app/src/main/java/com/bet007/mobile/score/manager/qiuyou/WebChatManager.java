package com.bet007.mobile.score.manager.qiuyou;

import com.bet007.mobile.score.model.qiuyou.ChatMessageModel;
import com.bet007.mobile.score.model.qiuyou.IChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息列表管理类
 */
public class WebChatManager {

    public List<ChatMessageModel> chatMessageModelList = new ArrayList<>();

    public List<ChatMessageModel> getList() {
        chatMessageModelList.clear();
        int size = 200;
        ChatMessageModel chatMessageModel = ChatMessageModel.getTipsMsg();;
        for (int i = 0; i < size; i++) {
            int k = i % 9;
            switch (k){
                case IChatMessage.TYPE_TIPS:
                    chatMessageModel = ChatMessageModel.getTipsMsg();
                    break;
                case IChatMessage.TYPE_FROM_ACTION:
                    chatMessageModel = ChatMessageModel.getFromActionMsg();
                    break;
                case IChatMessage.TYPE_FROM_TEXT:
                    chatMessageModel = ChatMessageModel.getFromTextMsg();

                    break;
                case IChatMessage.TYPE_FROM_RECORD:
                    chatMessageModel = ChatMessageModel.getFromRecordMsg();
                    break;
                case IChatMessage.TYPE_FROM_PIC:
                    chatMessageModel = ChatMessageModel.getFromPicMsg();
                    break;
                case IChatMessage.TYPE_TO_ACTION:
                    chatMessageModel = ChatMessageModel.getToActionMsg();
                    break;
                case IChatMessage.TYPE_TO_TEXT:
                    chatMessageModel = ChatMessageModel.getToTextMsg();
                    break;
                case IChatMessage.TYPE_TO_RECORD:
                    chatMessageModel = ChatMessageModel.getToRecordMsg();
                    break;
                case IChatMessage.TYPE_TO_PIC:
                    chatMessageModel = ChatMessageModel.getToPicMsg();
                    break;
            }
            chatMessageModelList.add(chatMessageModel);
        }
        return chatMessageModelList;
    }

}
