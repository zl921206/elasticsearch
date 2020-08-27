package com.kamluen.elasticsearch.utils;

/**
 * 股票相关工具类
 */
public class StkUtils {

    /**
     * 获取股票末尾市场标识
     * @param stkCode
     * @return
     */
    public static String determineMarketCode(String stkCode) {
        if (stkCode == null) {
            return "";
        }
        int idx = stkCode.lastIndexOf('.');
        if (idx <= 0) {
            return "";
        }
        String mkt = stkCode.substring(idx+1);
        return mkt;
    }

    /**
     * 判断客户端输入参数为数字，汉字还是字母
     *
     * @param condition
     * @return
     */
    public static String judgeCondition(String condition) {
        // 判断是否为中文
        if (StringUtils.isChineseChar(condition) == true) {
            return "0";
        }
        /**
         *  以下 2 跟 1的位置不可对调，因为需要2过滤后，才能查1
         */
        // 判断是否为数字
        if (StringUtils.isNumber(condition) == true) {
            return "2";
        }
        // 判断是否为字母
        if (StringUtils.isLetter(condition) == true) {
            return "1";
        }
        return "";
    }

    /**
     * 如果客户端传入的字符串为英文，全部转为字母大写
     *
     * @param condition
     * @return
     */
    public static String toUpperCase(String condition) {

        boolean b = StringUtils.isLetter(condition);
        if (b == true) {
            condition = condition.toUpperCase();
        }
        return condition;
    }
}
