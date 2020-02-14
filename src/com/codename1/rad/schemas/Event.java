/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.schemas;

import com.codename1.rad.models.Tag;

/**
 *
 * @author shannah
 */
public interface Event extends Thing {
    public static final Tag about = new Tag(),
            actor = new Tag(),
            aggregateRating = new Tag(),
            attendee = new Tag(),
            audience = new Tag(),
            composer = new Tag(),
            contributor = new Tag(),
            director = new Tag(),
            doorTime = new Tag(),
            duration = new Tag(),
            endDate = new Tag(),
            eventSchedule = new Tag(),
            eventStatus = new Tag(),
            funder = new Tag(),
            inLanguage = new Tag(),
            isAccessibleForFree = new Tag(),
            location = new Tag(),
            maximumAttendeeCapacity = new Tag(),
            offers = new Tag(),
            organizer = new Tag(),
            performer = new Tag(),
            previousStartDate = new Tag(),
            recordedIn = new Tag(),
            remainingAttendeeCapacity = new Tag(),
            review = new Tag(),
            sponsor = new Tag(),
            startDate = new Tag(),
            subEvent = new Tag(),
            superEvent = new Tag(),
            translator = new Tag(),
            typicalAgeRange = new Tag(),
            workFeatured = new Tag(),
            workPerformed = new Tag();
}
