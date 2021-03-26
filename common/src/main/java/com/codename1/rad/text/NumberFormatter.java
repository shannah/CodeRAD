/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.ParseException;

/**
 * Interface for an object that can format and parse numbers.
 * @author shannah
 */
public interface NumberFormatter {
    public String format(Number number);
    
    public Number parse(String number) throws ParseException;
}
