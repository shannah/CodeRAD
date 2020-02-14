/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.L10NManager;
import com.codename1.l10n.ParseException;
import com.codename1.util.DateUtil;
import java.util.Date;

/**
 *
 * @author shannah
 */
public class TimeAgoDateFormatter implements DateFormatter {
    private DateUtil dateUtil = new DateUtil();

    @Override
    public String format(Date date) {
        return dateUtil.getTimeAgo(date);
    }

    @Override
    public Date parse(String date) throws ParseException {
        throw new ParseException("Failed to parse date "+date+". Date parsing not supported with this formatter", 0);
    }

    @Override
    public boolean supportsParse() {
        return false;
    }
    
}
