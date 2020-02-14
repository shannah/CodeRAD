/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.image;

import com.codename1.rad.models.ContentType;
import com.codename1.rad.models.Property.Name;
import com.codename1.compat.java.util.Objects;
import com.codename1.io.File;
import com.codename1.io.Storage;
import com.codename1.ui.CN;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
import com.codename1.util.AsyncResource;

/**
 *
 * @author shannah
 */
public abstract class AsyncImage extends AsyncResource<Image> {
    
    public static final ContentType CONTENT_TYPE = new ContentType<AsyncImage>(new Name("Image"), AsyncImage.class) {
        
        private boolean findInArray(String needle, String[] haystack) {
            int len = haystack.length;
            for (int i=0; i<len; i++) {
                if (Objects.equals(needle, haystack[i])) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean canConvertFrom(ContentType otherType) {
            // Not sure exactly what to do here since it depends on the data contents
            // on whether it can convert.
            // So we'll just say it can convert anything to an image.
            return true;
            
        }
        
        @Override
        public AsyncImage from(ContentType otherType, Object data) {
            if (data == null) {
                return null;
            }
            if (data instanceof Image) {
                return new WrappedImage((Image)data);
            }
            if (data instanceof AsyncImage) {
                return (AsyncImage)data;
            }
            String str = String.valueOf(data);
            
            if (str.startsWith("http://") || str.startsWith("https://")) {
                return new NetworkImage(str);
            }
            if (str.startsWith("file://")) {
                return new FileSystemImage(new File(str));
            }
            
            if (CN.getResourceAsStream(str) != null) {
                return new ClasspathImage(str);
            }
            if (Storage.getInstance().exists(str)) {
                return new StorageImage(str);
            }
            if (findInArray(str, Resources.getGlobalResources().getImageResourceNames())) {
                return new ResourceImage(Resources.getGlobalResources(), str);
            }
            
                
            
            throw new IllegalArgumentException("NO content type conversions found from "+otherType+" to "+this+ " for "+data);
        }

        @Override
        public <V> V to(ContentType<V> otherType, AsyncImage data) {
            if (data == null) {
                return null;
            }
            if (otherType.getRepresentationClass() == String.class) {
                if (data instanceof ClasspathImage) {
                    return (V)((ClasspathImage)data).getImagePath();
                }
                if (data instanceof FileSystemImage) {
                    return (V)((FileSystemImage)data).getFile().getAbsolutePath();
                }
                if (data instanceof NetworkImage) {
                    return (V)((NetworkImage)data).getURL();
                }
                if (data instanceof ResourceImage) {
                    return (V)((ResourceImage)data).getImageName();
                }
                if (data instanceof StorageImage) {
                    return (V)((StorageImage)data).getStorageKey();
                }
                if (data instanceof WrappedImage) {
                    return null;
                }
            } 
            throw new IllegalArgumentException("Unsupported type converting from "+this+" to "+otherType);
        }

        @Override
        public boolean canConvertTo(ContentType otherType) {
            return otherType.getRepresentationClass() == String.class;
        }
        
        
        
        
        
    };
    
}
