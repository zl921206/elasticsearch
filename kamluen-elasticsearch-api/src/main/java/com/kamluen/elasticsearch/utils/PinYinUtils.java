package com.kamluen.elasticsearch.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音工具类
 *
 * @author zhanglei
 * @date 2018-11-14
 */
public class PinYinUtils {

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        String output = "";

        try {
            for (int i = 0; i < input.length; i++) {
                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    if (temp != null) {
                        output += temp[0];
                    } else {
                        output += java.lang.Character.toString(input[i]);
                    }
                } else
                    output += java.lang.Character.toString(input[i]);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * 获取汉字字符串拼音首字母，其他字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE); // 大小写设置属性
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    } else {
                        /**
                         * 当字符没有识别，即temp==null，则将传入的字符串累加
                         */
                        pybf.append(arr[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().trim();
    }

    /**
     * 将小写字母转换为大写
     *
     * @param str
     * @return
     */
    public static String convertLowToUp(String str) {
        /*
         *  根据 char 的工具类 Character
         */
        char[] chars = str.toCharArray();
        for (int i = 0, length = chars.length; i < length; i++) {
            char c = chars[i];
            //判断字母是不是大写，如果是大写变为小写
//            if (Character.isUpperCase(c)){
//                chars[i] = Character.toLowerCase(c);
//                continue;
//            }
            //如果为小写，变为大写
            chars[i] = Character.toUpperCase(c);
        }
        return new String(chars);
    }

    /**
     * 将大写字母转换为小写
     *
     * @param str
     * @return
     */
    public static String convertUpToLow(String str) {
        /*
         *  根据 char 的工具类 Character
         */
        char[] chars = str.toCharArray();
        for (int i = 0, length = chars.length; i < length; i++) {
            char c = chars[i];
            //如果为大写，变为小写
            chars[i] = Character.toLowerCase(c);
        }
        return new String(chars);
    }
}
