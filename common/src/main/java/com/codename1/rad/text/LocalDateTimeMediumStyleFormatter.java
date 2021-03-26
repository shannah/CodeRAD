/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.L10NManager;
import com.codename1.l10n.ParseException;
import java.util.Date;

/**
 * A formatter for formatting dates as date/time in current locale in medium format.
 * 
 * NOTE: Does not support parsing.
 * @author shannah
 */
public class LocalDateTimeMediumStyleFormatter implements DateFormatter {

    @Override
    public String format(Date date) {
        return L10NManager.getInstance().formatDateTimeMedium(date);
    }

    @Override
    public Date parse(String date) throws ParseException {
        throw new ParseException("Failed to parse date.  This formatter doesn't support parsing.", 0);
    }

    @Override
    public boolean supportsParse() {
        return false;
    }
    
}
