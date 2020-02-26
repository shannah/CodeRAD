/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.UI;
import com.codename1.ui.CN;
import com.codename1.ui.EncodedImage;

/**
 * An image that is loaded from the classpath.
 * @author shannah
 */
public class ClasspathImage extends AsyncImage {
    private String imagePath;
    
    public ClasspathImage(String imagePath) {
        this.imagePath = imagePath;
        UI.runOnImageProcessingThread(()->{
            try {
                complete(EncodedImage.create(CN.getResourceAsStream(imagePath)));
            } catch (Throwable t) {
                error(new AsyncExecutionException(t));
            }
        });
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
}
