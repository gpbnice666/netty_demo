package com.bo.numToRMB;

import java.util.Scanner;

public class NumToRMB {

    // 数字对应的汉字
    private static final String[] CN_NUM = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    // 整数部分单位
    private static final String[] CN_INTEGER_UNIT = {"", "拾", "佰", "仟"};
    // 小数部分单位
    private static final String[] CN_DECIMAL_UNIT = {"角", "分"};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入一个金额：");
        double money = scanner.nextDouble();
        String rmb = convertToRMB(money);
        System.out.println(rmb);
        scanner.close();
    }

    private static String convertToRMB(double money) {
        if (money == 0) {
            return "零元整";
        }
        long integerPart = (long) Math.abs(money); // 整数部分
        int decimalPart = (int) ((Math.abs(money) - integerPart + 0.001) * 100); // 小数部分
        String result = "";
        // 处理整数部分
        for (int i = 0; integerPart > 0; i++) {
            int digit = (int) (integerPart % 10);
            if (digit != 0 || i == 0) {
                result = CN_NUM[digit] + CN_INTEGER_UNIT[i] + result;
            }
            integerPart /= 10;
        }
        result += "元";
        // 处理小数部分
        for (int i = 0; decimalPart > 0 && i < CN_DECIMAL_UNIT.length; i++) {
            int digit = decimalPart % 10;
            if (digit != 0) {
                result += CN_NUM[digit] + CN_DECIMAL_UNIT[i];
            }
            decimalPart /= 10;
        }
        if (result.endsWith("角")) {
            result += "零分";
        }
        if (!result.contains("分")) {
            result += "整";
        }
        return result;
    }
}
