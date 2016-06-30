package org.mybatis.spring.cache;


/**
 * 
 * @author lindezhi
 * 2016年6月29日 下午5:34:02
 */
public class StringUtils {
	
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }
    
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

}
