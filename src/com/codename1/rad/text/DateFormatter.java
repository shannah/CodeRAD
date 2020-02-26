/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.ParseException;
import java.util.Date;

/**
 * Interface for objects that can format and parse dates.
 * @author shannah
 */
public interface DateFormatter {
    public String format(Date date);
    public Date parse(String date) throws ParseException;
    public boolean supportsParse();
}
