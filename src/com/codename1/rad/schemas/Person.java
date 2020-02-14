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
public interface Person extends Thing {
    public static final Tag additionalName = new Tag("additionalName");
    public static final Tag address = new Tag("address");
    public static final Tag affiliation = new Tag("affiliation");
    public static final Tag alumniOf = new Tag("alumniOf");
    public static final Tag award = new Tag("award");
    public static final Tag birthDate = new Tag("birthDate");
    public static final Tag birthPlace = new Tag("birthPlace");
    public static final Tag brand = new Tag("brand");
    public static final Tag callSign = new Tag("callSign");
    public static final Tag children = new Tag("children"),
            colleague = new Tag("colleague"),
            contactPoint = new Tag("contactPoint"),
            deathDate = new Tag("deathDate"),
            duns = new Tag("duns"),
            email = new Tag("email"),
            familyName = new Tag("familyName"),
            faxNumber = new Tag("faxNumber"),
            follows = new Tag("follows"),
            funder = new Tag("funder"),
            gender = new Tag("gender"),
            givenName = new Tag("givenName"),
            globalLocationNumber = new Tag("globalLocationNumber"),
            hasCredential = new Tag(),
            hasOccupation = new Tag(),
            hasOfferCatalog = new Tag(),
            hasPOS = new Tag(),
            height = new Tag(),
            homeLocation = new Tag(),
            honorificPrefix = new Tag(),
            honorificSuffix = new Tag(),
            interactionStatistic = new Tag(),
            isicV4 = new Tag(),
            jobTitle = new Tag(),
            knowsAbout = new Tag(),
            knowsLanguage = new Tag(),
            makesOffer = new Tag(),
            memberOf = new Tag(),
            naics = new Tag(),
            nationality = new Tag(),
            netWorth = new Tag(),
            owns = new Tag(),
            parent = new Tag(),
            performerIn = new Tag(),
            publishingPrinciples = new Tag(),
            relatedTo = new Tag(),
            seeks = new Tag(),
            sibling = new Tag(),
            sponsor = new Tag(),
            spouse = new Tag(),
            taxID = new Tag(),
            telephone = new Tag(),
            vatID = new Tag(),
            weight = new Tag(),
            workLocation = new Tag(),
            worksFor = new Tag();
            
                    
}
