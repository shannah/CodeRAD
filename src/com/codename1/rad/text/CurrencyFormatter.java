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
public class CurrencyFormatter implements NumberFormatter {

    @Override
    public String format(Number number) {
        return L10NManager.getInstance().formatCurrency(number.doubleValue());
    }

    @Override
    public Number parse(String number) throws ParseException {
        return L10NManager.getInstance().parseCurrency(number);
    }
    
}
