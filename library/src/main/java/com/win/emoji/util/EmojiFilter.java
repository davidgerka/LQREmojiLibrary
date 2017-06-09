package com.win.emoji.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.lang.Character.UnicodeBlock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 禁止输入emoji表情的过滤器 
 * 采用正则表达式来处理
 */
public class EmojiFilter implements InputFilter {

	public EmojiFilter() {
		super();
	}

    /*重新整理的*/
    Pattern emoji = Pattern.compile(
            "[\uD83C\uDC00-\uD83C\uDFFF]|[\uD83D\uDC00-\uD83D\uDFFF]|" +
                    "[\u2000-\u200F]|[\u2020-\u2023]|[\u2028-\u2031]|[\u203B-\u2052]|[\u2054-\u206F]|[\u20D0-\u20F0]|"+
                    "[\u2100-\u2102]|[\u2104-\u2109]|[\u2111-\u2115]|[\u2117-\u2118]|[\u2120-\u2125]|[\u2190-\u21FF]|" +
                    "[\u2300-\u23FF]|" +
                    "[\u2460-\u24FF]|[\u25A0-\u27FF]|[\u2900-\u297F]|[\u2B00-\u2BFF]|" +
                    "[\u3000]|[\u3004-\u3006]|[\u3020]|[\u302A-\u303F]|[\u3200-\u32FF]|" +
                    "[\u0080-\u00A1]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    /*旧的*/
    Pattern oldEmoji = Pattern.compile(
			"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|" +
                    "[\u25fb-\u27ff]|" +
                    "[\u2b50-\u2b55]|" +
                    "[\u2194-\u2199]|[\u23e9-\u23ea]|[\u2328]|[\u25b6]|[\u25c0]|[\u20e3]|[\u2b05-\u2b07]|[\u2b1b]|" +
                    "[\u2b1c]|[\u2122]|[\ud83e\udd10-\ud83e\udd18]|[\ud83e\udd80-\ud83e\udd84]|[\ud83e\uddc0]",
			Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);



	
	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        //字符串永远都是unicode编码的
		String unicode = utf8ToUnicode(source.toString());
//		MyLog.i("yyyyyyyyyyyyyyyyyyyy-----unicode = "+unicode+"----source = "+source);
		Matcher emojiMatcher = emoji.matcher(source);
		if (emojiMatcher.find()) {
//			return "";
        }
//        MyLog.e("-----must filter emoj emotion unicode = "+unicode);
        return null;
	}
	
	/**
     * utf-8 转unicode
     * 
     * @param inStr
     * @return String
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toUpperCase());
            }
        }
        return sb.toString();
    }

}
