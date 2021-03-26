package com.codenameone;

public class FileChooserNativeImpl {
    public boolean isSupported() {
        return true;
    }
    
    public boolean showNativeChooser(String accept, boolean multi) {
        return false;
    }

}
