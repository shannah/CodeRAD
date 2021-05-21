package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.ImageContainerPropertyView;
import com.codename1.rad.propertyviews.LabelPropertyView;
import com.codename1.rad.schemas.Thing;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.image.ImageContainer;
import com.codename1.ui.Label;

import java.util.Map;

@RAD(tag={"imageContainerPropertyView", "radImageContainer", "radImage"})
public class ImageContainerPropertyViewBuilder extends PropertyViewBuilder<ImageContainer> {
    private ImageContainer imageContainer;
    private String storageFile;
    private String file;
    private double aspectRatio;
    public ImageContainerPropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public ImageContainerPropertyViewBuilder imageContainer(@Inject ImageContainer imageContainer) {
        this.imageContainer = imageContainer;
        return this;
    }

    public ImageContainerPropertyViewBuilder storageFile(String storageFile) {
        this.storageFile = storageFile;
        return this;
    }

    public ImageContainerPropertyViewBuilder file(String file) {
        this.file = file;
        return this;
    }


    public ImageContainerPropertyViewBuilder aspect(double aspectRatio) {
        this.aspectRatio = aspectRatio;
        return this;
    }

    @Override
    public ImageContainerPropertyView build() {
        if (fieldNode == null) {
            tag(Thing.thumbnailUrl);
        }
        if (imageContainer == null) {
            if (file != null) {
                imageContainer = ImageContainer.createToFileSystem(fieldNode.getPropertySelector(getContext().getEntity()), file);
            } else if (storageFile != null) {
                imageContainer = ImageContainer.createToStorage(fieldNode.getPropertySelector(getContext().getEntity()), storageFile);
            } else {
                imageContainer = ImageContainer.createToStorage(fieldNode.getPropertySelector(getContext().getEntity()));
            }
            if (aspectRatio > 0) {
                imageContainer.setAspectRatio(aspectRatio);
            }
        }

        return new ImageContainerPropertyView(imageContainer, getContext().getEntity(), fieldNode);
    }

    @Override
    public ImageContainerPropertyView getComponent() {
        return (ImageContainerPropertyView)super.getComponent();
    }
}
