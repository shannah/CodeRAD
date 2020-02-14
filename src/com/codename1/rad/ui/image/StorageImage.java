/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.UI;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Storage;
import com.codename1.ui.EncodedImage;

/**
 *
 * @author shannah
 */
public class StorageImage extends AsyncImage {
    private String storageKey;
    
    public StorageImage(String storageKey) {
        this.storageKey = storageKey;
        UI.runOnImageProcessingThread(() -> {
            Storage fs = Storage.getInstance();
            try {
                complete(EncodedImage.create(fs.createInputStream(storageKey)));
            } catch (Exception ex) {
                if (!isDone()) {
                    error(new AsyncExecutionException(ex));
                }
            }
        });
    }
    
    public String getStorageKey() {
        return storageKey;
    }
}
