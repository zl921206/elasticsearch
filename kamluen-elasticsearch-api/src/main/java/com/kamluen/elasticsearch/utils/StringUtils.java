package com.kamluen.elasticsearch.utils;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串相关工具类
 */
public class StringUtils {

    private static Pattern EngOrDec = Pattern.compile("^[\\da-zA-Z]+$");

    private static Pattern PhoneNumber = Pattern.compile("^[1](([3|5|8][\\d])|([4][5,6,7,8,9])|([6][5,6])|([7][3,4,5,6,7,8])|([9][8,9]))[\\d]{8}$");




    /**
     * 判断字符串是否为数字
     *
     * @param String str
     * @return
     */
    public static boolean isNumber(String str) {

        for (int i = str.length(); --i >= 0;) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    /**
     *  判断是否是手机号
     * @param str
     * @return
     */
    public static boolean isPhoneNumber(String str){
        //手机号应为11位数
        if (str.length() != 11){
            return false;
        }
        return PhoneNumber.matcher(str).matches();
    }

    /**
     * 判断字符串是否为中文
     *
     * @param str
     * @return
     */
    public static boolean isChineseChar(String str) {

        boolean temp = false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            temp = true;
        }
        return temp;
    }

    /**
     * 判断字符串中是否全部为英文字母
     *
     * @param s
     * @return
     */
    public static boolean isLetter(String s) {
        Matcher matcher = EngOrDec.matcher(s);
        if(matcher.find()){
            return true;
        } else{
            return false;
        }
    }

    public static boolean isNEmpty(Collection<?> collection){
        if((collection != null) && (collection.size() > 0)) {
            return true;
        }
        return false;
    }

    public static boolean isNEmpty(String str){
        if((str != null) && (str.length() > 0) && !"null".equals(str) && !"".equals(str)){
            return true;
        }
        return false;
    }

    public static boolean isNEmpty(Object[] objArr){
        if ((objArr != null) && (objArr.length > 0)) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Object obj){
        if (obj == null || null == obj || "null".equals(obj)) {
            return true;
        }
        return false;
    }

    /**
     * 字符串不为空
     */
    public static boolean isNotNull(String value) {
        return hasLength(value);
    }

    /**
     * 对象不为空
     */
    public static boolean isNotNull(Object value) {
        return hasLength(value);
    }

    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0 && !"null".equals(str.toLowerCase()));
    }

    public static boolean hasLength(Object obj) {
        return (obj != null && (obj + "").length() > 0 && !"null".equals((obj + "").toLowerCase()));
    }

    /**
     * @Description:替换空格，换行符，回车符等
     * @param @param capitalize
     * @param @param str
     * @param @return
     * @return String
     */
    public static String replace(String str,String partner) {
        //Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Pattern p = Pattern.compile(partner);
        if(isNEmpty(str)){
            Matcher m = p.matcher(str);
            return m.replaceAll("");
        }
        return null;
    }

    public static String encodeUnicode(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 3);
        for (char c : s.toCharArray()) {
            if (c < 256) {
                sb.append(c);
            } else {
                sb.append("\\u");
                sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
                sb.append(Character.forDigit((c) & 0xf, 16));
            }
        }
        return sb.toString();
    }
}
