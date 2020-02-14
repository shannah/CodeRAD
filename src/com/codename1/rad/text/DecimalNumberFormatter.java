/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.L10NManager;
import com.codename1.l10n.ParseException;

/**
 *
 * @author shannah
 */
public class DecimalNumberFormatter implements NumberFormatter {

    private int decimalPlaces;
    
    public DecimalNumberFormatter(int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }
    
    @Override
    public String format(Number number) {
        return L10NManager.getInstance().format(number.doubleValue(), decimalPlaces);
    }

    @Override
    public Number parse(String number) throws ParseException {
        return L10NManager.getInstance().parseDouble(number);
    }
    
}
