package com.codename1.rad.util;

import com.codename1.io.Log;
import com.codename1.util.SuccessCallback;

public class NonNull {
    public static <T> T nonNull(T o, T defaultVal) {
        return o == null ? defaultVal : (T)o;
    }
    public static <T> T[] nonNullEntries(T[] o, T[] defaultVal) {
        if (o == null || o.length == 0 || o[0] == null) return defaultVal;
        return o;
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
    
    public static boolean empty(Object val) {
        return val == null || val.toString().isEmpty();
    }

    public static interface Function<T> {
        public T run();
    }

    public <T> T suppressErrors(Class<T> returnType, Function<T> func) {
        try {
            return func.run();
        } catch (Exception ex) {
            Log.e(ex);
            return null;
        }
    }
}
