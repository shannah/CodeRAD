/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.ui.UI;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;

/**
 *
 * @author shannah
 */
public class ResourceImage extends AsyncImage {
    private Resources res;
    private String imageName;
    
    public ResourceImage(Resources res, String imageName) {
        this.res = res;
        this.imageName = imageName;
        UI.runOnImageProcessingThread(()->{
            try {
                Image im = res.getImage(imageName);
                complete(EncodedImage.createFromImage(im, false));
            } catch (Throwable t) {
                error(new AsyncExecutionException(t));
            }
        });
    }
    
    public Resources getResources() {
        return res;
    }
    
    public String getImageName() {
        return imageName;
    }
}
