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
            hasCredential = new Tag("hasCredential"),
            hasOccupation = new Tag("hasOccupation"),
            hasOfferCatalog = new Tag("hasOfferCatalog"),
            hasPOS = new Tag("hasPOS"),
            height = new Tag("height"),
            homeLocation = new Tag("homeLocation"),
            honorificPrefix = new Tag("honorificPrefix"),
            honorificSuffix = new Tag("honorificSuffix"),
            interactionStatistic = new Tag("interactionStatistic"),
            isicV4 = new Tag("isicV4"),
            jobTitle = new Tag("jobTitle"),
            knowsAbout = new Tag("knowsAbout"),
            knowsLanguage = new Tag("knowsLanguage"),
            makesOffer = new Tag("makesOffer"),
            memberOf = new Tag("memberOf"),
            naics = new Tag("naics"),
            nationality = new Tag("nationality"),
            netWorth = new Tag("netWorth"),
            owns = new Tag("owns"),
            parent = new Tag("parent"),
            performerIn = new Tag("performerIn"),
            publishingPrinciples = new Tag("publishingPrinciples"),
            relatedTo = new Tag("relatedTo"),
            seeks = new Tag("seeks"),
            sibling = new Tag("sibling"),
            sponsor = new Tag("sponsor"),
            spouse = new Tag("spouse"),
            taxID = new Tag("taxID"),
            telephone = new Tag("telephone"),
            vatID = new Tag("vatID"),
            weight = new Tag("weight"),
            workLocation = new Tag("workLocation"),
            worksFor = new Tag("worksFor");
            
                    
}
