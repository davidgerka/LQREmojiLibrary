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

    public void generateDataList(){
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
            chatMessageModel.setMessageId(i);
            chatMessageModelList.add(chatMessageModel);
        }

        long id = System.currentTimeMillis();
        chatMessageModel = ChatMessageModel.getToRecordMsg();
        chatMessageModel.setDuration(6);
        chatMessageModel.setMessageId(id);
        chatMessageModel.setContentText("/storage/emulated/0/liveScore/voice/kaka-gerrard-1497866081032.amr");
        chatMessageModelList.add(chatMessageModel);
        chatMessageModel = ChatMessageModel.getFromRecordMsg();
        chatMessageModel.setDuration(3);
        chatMessageModel.setMessageId(id + 1);
        chatMessageModel.setContentText("/storage/emulated/0/liveScore/voice/kaka-gerrard-1497866090787.amr");
        chatMessageModelList.add(chatMessageModel);
        chatMessageModel = ChatMessageModel.getToRecordMsg();
        chatMessageModel.setDuration(8);
        chatMessageModel.setMessageId(id + 2);
        chatMessageModel.setContentText("/storage/emulated/0/liveScore/voice/kaka-gerrard-1497866095762.amr");
        chatMessageModelList.add(chatMessageModel);

    }

    public List<ChatMessageModel> getList() {
        return chatMessageModelList;
    }

    boolean from = true;

    public void addRecordToList(String recordPath, int duration){
        ChatMessageModel model = ChatMessageModel.getToRecordMsg();
        from = !from;
        if(from){
            model = ChatMessageModel.getFromRecordMsg();
        }
        model.setDuration(duration);
        model.setContentText(recordPath);
        chatMessageModelList.add(model);
    }

    public void addTextToList(String text){
        ChatMessageModel model = ChatMessageModel.getToTextMsg();
        from = !from;
        if(from){
            model = ChatMessageModel.getFromTextMsg();
        }
        model.setContentText(text);
        chatMessageModelList.add(model);
    }

}
