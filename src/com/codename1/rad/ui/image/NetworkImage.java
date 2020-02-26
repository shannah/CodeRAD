/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.io.Util;
import com.codename1.ui.EncodedImage;

/**
 * An image that is loaded over the network.
 * @author shannah
 */
public class NetworkImage extends AsyncImage {
    private String url;
    
    public NetworkImage(String url) {
        this.url = url;
        Util.downloadImageToCache(url).ready(img->{
            try {
                complete(EncodedImage.createFromImage(img, false));
            } catch (Exception ex) {
                error(new AsyncExecutionException(ex));
            }
        });
        
    }
    
    public String getURL() {
        return url;
    }
    
}
