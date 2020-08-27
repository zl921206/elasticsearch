package com.kamluen.elasticsearch;

import com.kamluen.common.utils.JSONUtil;
import com.kamluen.elasticsearch.utils.PinYinUtils;

public class Test {

    @org.junit.Test
    public void test() {
        String type = "1";
        String str = "";
        int ii = 0;
        test(type, str);
        System.out.println("main方法输出str: "  + str);

        User user = null;

        testUser(type, user);

        System.out.println("test方法输出user: "  + JSONUtil.toCompatibleJson(user));

        user = new User();
        testUser(type, user);

        System.out.println("test方法输出user: "  + JSONUtil.toCompatibleJson(user));
    }

    public static void test(String type, String str){
        switch (type) {
            // 表示传入值为中文
            case "0":
                str = "0";
                break;
            // 表示传入值为字母
            case "1":
                str = "1";
                break;
            // 表示传入值为数字
            case "2":
                str = "2";
                break;
        }
        System.out.println("test方法输出str: "  + str);
    }

    public static void testUser(String type, User user){
        if(null == user){
            user = new User();
        }
        switch (type) {
            // 表示传入值为中文
            case "0":
                user.setAge(16);
                user.setUserId("123456");
                user.setUserName("zhangsan");
                break;
            // 表示传入值为字母
            case "1":
                user.setAge(17);
                user.setUserId("654321");
                user.setUserName("lisi");
                break;
            // 表示传入值为数字
            case "2":
                user.setAge(16);
                user.setUserId("098765");
                user.setUserName("wangwu");
                break;
        }
        System.out.println("test方法输出user: "  + JSONUtil.toCompatibleJson(user));
    }

    @org.junit.Test
    public void convertTest() {
        String string = "宝燵控股";
        string = PinYinUtils.getPingYin(string);
        System.out.println("输出转换之后的字符串：" + string);
        string = "*aBcD*efBb*";
        string = PinYinUtils.convertLowToUp(string);
        System.out.println("输出string: " + string);

        string = "\"*中国aBcD*e世\"界Bb*";
        string = PinYinUtils.convertLowToUp(string);
        System.out.println("输出string: " + string);
        string = PinYinUtils.convertUpToLow(string);
        System.out.println("输出string: " + string);

        string = "３６１度360温度";
        string = PinYinUtils.getFirstSpell(string);
        System.out.println("输出string: " + string);

        string = "大  冷Ｂ";
        string = PinYinUtils.getFirstSpell(string);
        System.out.println("输出string: " + string);

    }

    @org.junit.Test
    public void threadTest() {
        System.out.println("thread test start......");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 01......" + Thread.currentThread().getState());
        }
        ).start();
        new Thread(() ->
        {
            System.out.println("thread 02...01...");
            try {
                Thread.sleep(2000);
                convertTest();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("thread 02...02...");
        }
        ).start();
        new Thread(() -> System.out.println("thread 03......")).start();
        try {
            Thread.sleep(5000);
            System.out.println("thread test end......");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
