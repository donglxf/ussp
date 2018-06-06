package com.ht.ussp.util;

public class IDCardChecker {

	/**
	 * <p>
	 * 18位身份证验证
	 * </p>
	 * 
	 * 根据〖中华人民共和国国家标准 GB
	 * 11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
	 * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
	 * 
	 * 第十八位数字(校验码)的计算方法为： 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4
	 * 2 1 6 3 7 9 10 5 8 4 2 2.将这17位数字和系数相乘的结果相加。 3.用加出来和除以11，看余数是多少？ 4.余数只可能有0
	 * 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。
	 * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
	 * 
	 * @date Oct 18, 2008
	 * @param value
	 */
	public static boolean validate18CardId(String value) {
		// 获得字段的名字

		String tempStr = value.substring(0, 17);
		String sourceCheckCode = value.substring(17, 18);
		String checkCode = "";
		int[] a = new int[17];
		int i = 0;
		try {
			while (i < 17) {
				a[i] = Integer.parseInt(tempStr.substring(i, i + 1));
				i++;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		int mod = (a[0] * 7 + a[1] * 9 + a[2] * 10 + a[3] * 5 + a[4] * 8 + a[5]
				* 4 + a[6] * 2 + a[7] * 1 + a[8] * 6 + a[9] * 3 + a[10] * 7
				+ a[11] * 9 + a[12] * 10 + a[13] * 5 + a[14] * 8 + a[15] * 4 + a[16] * 2) % 11;
		switch (mod) {
		case 10:
			checkCode = "2";
			break;
		case 9:
			checkCode = "3";
			break;
		case 8:
			checkCode = "4";
			break;
		case 7:
			checkCode = "5";
			break;
		case 6:
			checkCode = "6";
			break;
		case 5:
			checkCode = "7";
			break;
		case 4:
			checkCode = "8";
			break;
		case 3:
			checkCode = "9";
			break;
		case 2:
			checkCode = "x";
			break;
		case 1:
			checkCode = "0";
			break;
		case 0:
			checkCode = "1";
			break;
		}

		if (!sourceCheckCode.equalsIgnoreCase(checkCode)){
			return false;			
		}
		return true;
	}

	/**
	 * <p>
	 * 15位身份证验证
	 * </p>
	 * 
	 * 只做了数字验证
	 * 
	 * @param value
	 */
	public static boolean validate15CardId(String value) {
		int i = 0;
		try {
			while (i < 15) {
				if (!Character.isDigit(value.charAt(i)))
					return false;
				i++;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
}
