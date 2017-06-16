package com.bet007.library.chatsceneinputlibrary.listener;

/**
 * 表情emoji选择、贴图选择接口
 */
public interface IEmotionSelectedListener {
    void onEmojiSelected(String key);

    void onStickerSelected(String categoryName, String stickerName, String stickerBitmapPath);
}
