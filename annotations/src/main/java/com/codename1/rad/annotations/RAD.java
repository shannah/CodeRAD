/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author shannah
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface RAD {
    public String[] tag() default {};
    public String property() default "";

    /**
     * When parsing XML fragment files.  The XML tag may be parsed as an Attribute
     * or a Component.  Default is Component.
     * @return
     */
    public TagType tagType() default TagType.Component;

    /**
     * When parsing XML fragment files.  The child XML tags can be parsed as Attributes
     * or Components.  Default is Components.
     * @return
     */
    public TagType childTagType() default TagType.Component;
    
    

}
