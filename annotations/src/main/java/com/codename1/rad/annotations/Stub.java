package com.codename1.rad.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface Stub {
    /**
     * The qualified name of the class that generated this stub.  This can be used to do a full pass
     * @return
     */
    public String generatedBy() default "";
}
