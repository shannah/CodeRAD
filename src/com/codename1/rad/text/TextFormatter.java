/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.ParseException;


/**
 * Interface for objects that can format text.
 * @author shannah
 */
public interface TextFormatter {
    public String format(String text);
    public String parse(String text) throws ParseException;
    public boolean supportsParse();
}
