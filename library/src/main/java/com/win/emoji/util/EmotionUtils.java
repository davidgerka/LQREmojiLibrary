
package com.win.emoji.util;


import android.util.Log;

import com.lqr.emoji.R;

import java.util.LinkedHashMap;


/**
 * @description :表情加载类,可自己添加多种表情，分别建立不同的map存放和不同的标志符即可
 */
public class EmotionUtils {

    /**
     * 表情类型标志符
     */
    public static final int EMOTION_CLASSIC_TYPE = 0x0001;//经典表情

    /**
     * key-表情文字;
     * value-表情图片资源
     */
    public static LinkedHashMap<String, Integer> EMPTY_MAP;
    public static LinkedHashMap<String, Integer> EMOTION_CLASSIC_MAP;


    static {
        EMPTY_MAP = new LinkedHashMap<>();
        EMOTION_CLASSIC_MAP = new LinkedHashMap<>();

        EMOTION_CLASSIC_MAP.put("/kb", R.drawable.smiley_0);
        EMOTION_CLASSIC_MAP.put("/ch", R.drawable.smiley_1);
        EMOTION_CLASSIC_MAP.put("/zj", R.drawable.smiley_2);
        EMOTION_CLASSIC_MAP.put("/qiao", R.drawable.smiley_3);
        EMOTION_CLASSIC_MAP.put("/kl", R.drawable.smiley_4);
        EMOTION_CLASSIC_MAP.put("/shuai", R.drawable.smiley_5);
        EMOTION_CLASSIC_MAP.put("/zhem", R.drawable.smiley_6);
        EMOTION_CLASSIC_MAP.put("/hanx", R.drawable.smiley_7);
        EMOTION_CLASSIC_MAP.put("/db", R.drawable.smiley_8);
        EMOTION_CLASSIC_MAP.put("/fendou", R.drawable.smiley_9);
        EMOTION_CLASSIC_MAP.put("/zhm", R.drawable.smiley_10);
        EMOTION_CLASSIC_MAP.put("/yiw", R.drawable.smiley_11);
        EMOTION_CLASSIC_MAP.put("/xu", R.drawable.smiley_12);
        EMOTION_CLASSIC_MAP.put("/yun", R.drawable.smiley_13);
        EMOTION_CLASSIC_MAP.put("/lh", R.drawable.smiley_14);
        EMOTION_CLASSIC_MAP.put("/jk", R.drawable.smiley_15);
        EMOTION_CLASSIC_MAP.put("/kun", R.drawable.smiley_16);
        EMOTION_CLASSIC_MAP.put("/jie", R.drawable.smiley_17);
        EMOTION_CLASSIC_MAP.put("/am", R.drawable.smiley_18);
        EMOTION_CLASSIC_MAP.put("/baiy", R.drawable.smiley_19);
        EMOTION_CLASSIC_MAP.put("/ka", R.drawable.smiley_20);
        EMOTION_CLASSIC_MAP.put("/tx", R.drawable.smiley_21);
        EMOTION_CLASSIC_MAP.put("/tu", R.drawable.smiley_22);
        EMOTION_CLASSIC_MAP.put("/zk", R.drawable.smiley_23);
        EMOTION_CLASSIC_MAP.put("/lengh", R.drawable.smiley_24);
        EMOTION_CLASSIC_MAP.put("/kuk", R.drawable.smiley_25);
        EMOTION_CLASSIC_MAP.put("/ng", R.drawable.smiley_26);
        EMOTION_CLASSIC_MAP.put("/jy", R.drawable.smiley_27);
        EMOTION_CLASSIC_MAP.put("/cy", R.drawable.smiley_28);
        EMOTION_CLASSIC_MAP.put("/tp", R.drawable.smiley_29);
        EMOTION_CLASSIC_MAP.put("/fn", R.drawable.smiley_30);
        EMOTION_CLASSIC_MAP.put("/gg", R.drawable.smiley_31);
        EMOTION_CLASSIC_MAP.put("/dk", R.drawable.smiley_32);
        EMOTION_CLASSIC_MAP.put("/shui", R.drawable.smiley_33);
        EMOTION_CLASSIC_MAP.put("/bz", R.drawable.smiley_34);
        EMOTION_CLASSIC_MAP.put("/hx", R.drawable.smiley_35);
        EMOTION_CLASSIC_MAP.put("/ll", R.drawable.smiley_36);
        EMOTION_CLASSIC_MAP.put("/dy", R.drawable.smiley_37);
        EMOTION_CLASSIC_MAP.put("/fd", R.drawable.smiley_38);
        EMOTION_CLASSIC_MAP.put("/se", R.drawable.smiley_39);
        EMOTION_CLASSIC_MAP.put("/pz", R.drawable.smiley_40);
        EMOTION_CLASSIC_MAP.put("/:)", R.drawable.smiley_41);
        EMOTION_CLASSIC_MAP.put("/ty", R.drawable.smiley_42);
        EMOTION_CLASSIC_MAP.put("/yl", R.drawable.smiley_43);
        EMOTION_CLASSIC_MAP.put("/bb", R.drawable.smiley_44);
        EMOTION_CLASSIC_MAP.put("/piaoch", R.drawable.smiley_45);
        EMOTION_CLASSIC_MAP.put("/zq", R.drawable.smiley_46);
        EMOTION_CLASSIC_MAP.put("/dao", R.drawable.smiley_47);
        EMOTION_CLASSIC_MAP.put("/zhd", R.drawable.smiley_48);
        EMOTION_CLASSIC_MAP.put("/shd", R.drawable.smiley_49);
        EMOTION_CLASSIC_MAP.put("/dg", R.drawable.smiley_50);
        EMOTION_CLASSIC_MAP.put("/xs", R.drawable.smiley_51);
        EMOTION_CLASSIC_MAP.put("/xin", R.drawable.smiley_52);
        EMOTION_CLASSIC_MAP.put("/wen", R.drawable.smiley_53);
        EMOTION_CLASSIC_MAP.put("/dx", R.drawable.smiley_54);
        EMOTION_CLASSIC_MAP.put("/mg", R.drawable.smiley_55);
        EMOTION_CLASSIC_MAP.put("/xig", R.drawable.smiley_56);
        EMOTION_CLASSIC_MAP.put("/pj", R.drawable.smiley_57);
        EMOTION_CLASSIC_MAP.put("/lq", R.drawable.smiley_58);
        EMOTION_CLASSIC_MAP.put("/pp", R.drawable.smiley_59);
        EMOTION_CLASSIC_MAP.put("/kf", R.drawable.smiley_60);
        EMOTION_CLASSIC_MAP.put("/fan", R.drawable.smiley_61);
        EMOTION_CLASSIC_MAP.put("/zt", R.drawable.smiley_62);
        EMOTION_CLASSIC_MAP.put("/wq", R.drawable.smiley_63);
        EMOTION_CLASSIC_MAP.put("/kk", R.drawable.smiley_64);
        EMOTION_CLASSIC_MAP.put("/yx", R.drawable.smiley_65);
        EMOTION_CLASSIC_MAP.put("/qq", R.drawable.smiley_66);
        EMOTION_CLASSIC_MAP.put("/xia", R.drawable.smiley_67);
        EMOTION_CLASSIC_MAP.put("/kel", R.drawable.smiley_68);
        EMOTION_CLASSIC_MAP.put("/cd", R.drawable.smiley_69);
        EMOTION_CLASSIC_MAP.put("/bs", R.drawable.smiley_70);
        EMOTION_CLASSIC_MAP.put("/hq", R.drawable.smiley_71);
        EMOTION_CLASSIC_MAP.put("/yhh", R.drawable.smiley_72);
        EMOTION_CLASSIC_MAP.put("/zhh", R.drawable.smiley_73);
        EMOTION_CLASSIC_MAP.put("/huaix", R.drawable.smiley_74);
        EMOTION_CLASSIC_MAP.put("/qd", R.drawable.smiley_75);
        EMOTION_CLASSIC_MAP.put("/gz", R.drawable.smiley_76);
        EMOTION_CLASSIC_MAP.put("/bq", R.drawable.smiley_77);
        EMOTION_CLASSIC_MAP.put("/hd", R.drawable.smiley_78);
        EMOTION_CLASSIC_MAP.put("/shl", R.drawable.smiley_79);
        EMOTION_CLASSIC_MAP.put("/bu", R.drawable.smiley_80);
        EMOTION_CLASSIC_MAP.put("/ws", R.drawable.smiley_81);
        EMOTION_CLASSIC_MAP.put("/aini", R.drawable.smiley_82);
        EMOTION_CLASSIC_MAP.put("/ruo", R.drawable.smiley_83);
        EMOTION_CLASSIC_MAP.put("/cj", R.drawable.smiley_84);
        EMOTION_CLASSIC_MAP.put("/qiang", R.drawable.smiley_85);
        EMOTION_CLASSIC_MAP.put("/qt", R.drawable.smiley_86);
        EMOTION_CLASSIC_MAP.put("/yb", R.drawable.smiley_87);
        EMOTION_CLASSIC_MAP.put("/gy", R.drawable.smiley_88);
        EMOTION_CLASSIC_MAP.put("/lw", R.drawable.smiley_89);


    }

    /**
     * 根据名称获取当前表情图标R值
     *
     * @param EmotionType 表情类型标志符
     * @param imgName     名称
     * @return
     */
    public static int getImgByName(int EmotionType, String imgName) {
        Integer integer = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                integer = EMOTION_CLASSIC_MAP.get(imgName);
                break;
            default:
                Log.e("", "the emojiMap is null!! Handle Yourself");
                break;
        }
        return integer == null ? -1 : integer;
    }

    /**
     * 根据类型获取表情数据
     *
     * @param EmotionType
     * @return
     */
    public static LinkedHashMap<String, Integer> getEmojiMap(int EmotionType) {
        LinkedHashMap EmojiMap = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                EmojiMap = EMOTION_CLASSIC_MAP;
                break;
            default:
                EmojiMap = EMPTY_MAP;
                break;
        }
        return EmojiMap;
    }
}
