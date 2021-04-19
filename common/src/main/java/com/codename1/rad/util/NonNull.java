package com.codename1.rad.util;

import com.codename1.util.SuccessCallback;

public class NonNull {
    public static <T> T nonNull(T o, T defaultVal) {
        return o == null ? defaultVal : (T)o;
    }

    public static <T> boolean with(Object o, Class<T> type, SuccessCallback<T> callback) {
        if (o == null || type == null) return false;
        if (type.isAssignableFrom(o.getClass())) {
            callback.onSucess((T)o);
            return true;
        }
        return false;
    }

    public static <T> boolean with(T o, SuccessCallback<T> callback) {
        if (o == null) return false;
        callback.onSucess(o);
        return true;
    }
}
