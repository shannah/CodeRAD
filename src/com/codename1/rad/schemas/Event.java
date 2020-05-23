/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 * From https://schema.org/Event
 * @author shannah
 */
public interface Event extends Thing {
    public static final Tag about = new Tag("about"),
            actor = new Tag("actor"),
            aggregateRating = new Tag("aggregateRating"),
            attendee = new Tag("attendee"),
            audience = new Tag("audience"),
            composer = new Tag("composer"),
            contributor = new Tag("contributor"),
            director = new Tag("director"),
            doorTime = new Tag("doorTime"),
            duration = new Tag("duration"),
            endDate = new Tag("endDate"),
            eventSchedule = new Tag("eventSchedule"),
            eventStatus = new Tag("eventStatus"),
            funder = new Tag("funder"),
            inLanguage = new Tag("inLanguage"),
            isAccessibleForFree = new Tag("isAccessibleForFree"),
            location = new Tag("location"),
            maximumAttendeeCapacity = new Tag("maximumAttendeeCapacity"),
            offers = new Tag("offers"),
            organizer = new Tag("organizer"),
            performer = new Tag("performer"),
            previousStartDate = new Tag("previousStartDate"),
            recordedIn = new Tag("recordedIn"),
            remainingAttendeeCapacity = new Tag("remainingAttendeeCapacity"),
            review = new Tag("review"),
            sponsor = new Tag("sponsor"),
            startDate = new Tag("startDate"),
            subEvent = new Tag("subEvent"),
            superEvent = new Tag("superEvent"),
            translator = new Tag("translator"),
            typicalAgeRange = new Tag("typicalAgeRange"),
            workFeatured = new Tag("workFeatured"),
            workPerformed = new Tag("workPerformed");
}
