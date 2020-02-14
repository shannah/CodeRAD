/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.UI;
import com.codename1.io.File;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Util;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;

/**
 *
 * @author shannah
 */
public class FileSystemImage extends AsyncImage {
    private File file;
    
    public FileSystemImage(File file) {
        this.file = file;
        UI.runOnImageProcessingThread(() -> {
            FileSystemStorage fs = FileSystemStorage.getInstance();
            try {
                complete(EncodedImage.create(fs.openInputStream(file.getAbsolutePath())));
            } catch (Exception ex) {
                if (!isDone()) {
                    FileSystemImage.this.error(new AsyncExecutionException(ex));
                }
            }
        });
    }
    
    public File getFile() {
        return file;
    }
}
