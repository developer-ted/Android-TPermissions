package com.tedkim.android.permission.utils;

import java.util.List;
import java.util.Map;

/**
 * Permission utils
 * Created by Ted
 */
public class PermissionUtils {
    public static boolean isEmpty(Object s) {
        if (s == null) {
            return true;
        }
        if ((s instanceof String) && (((String) s).trim().length() == 0)) {
            return true;
        }
        if (s instanceof Map) {
            return ((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return ((List<?>) s).isEmpty();
        }
        return s instanceof Object[] && (((Object[]) s).length == 0);
    }
}
