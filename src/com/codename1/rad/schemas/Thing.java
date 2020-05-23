/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * From https://schema.org/Person
 * @author shannah
 */
public interface Thing {
    public static final Tag additionalType = new Tag("additionalType"),
            alternateName = new Tag("alternateName"),
            description = new Tag("description"),
            disambiguatingDescription = new Tag(),
            identifier = new Tag("identifier"),
            image = new Tag("image"),
            mainEntityOfPage = new Tag(),
            name = new Tag("name"),
            potentialAction = new Tag("potentialAction"),
            sameAs = new Tag("sameAs"),
            subjectOf = new Tag("subjectOf"),
            url = new Tag("url"),
            thumbnailUrl = new Tag("thumbnailUrl");
            
}
