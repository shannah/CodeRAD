/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.text;

import com.codename1.l10n.DateFormat;
import com.codename1.l10n.ParseException;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.util.StringUtil;
import java.util.ArrayList;
import java.util.Date;

/**
 * A Date formatter that includes many different date formats.  It will try to parse
 * dates using formats until it finds one that works.
 * @author shannah
 */
public class AllFormatsDateFormatter implements DateFormatter {
    private static ArrayList<DateFormat> dateFormats = new ArrayList<>();
    private static DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static {
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        dateFormats.add(new SimpleDateFormatExt("yyyy-MM-dd'T'HH:mm:ssXXX"));
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"));
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        dateFormats.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"));
        dateFormats.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z"));
        dateFormats.add(new SimpleDateFormat("MM/dd/yyyy"));
        dateFormats.add(new SimpleDateFormat("yyyy-MM-dd"));

    }

    @Override
    public boolean supportsParse() {
        return true;
    }
    
    

    @Override
    public String format(Date date) {
        return outputFormat.format(date);
    }

    @Override
    public Date parse(String date) throws ParseException {
        for (DateFormat fmt : dateFormats) {
            try {
                return fmt.parse(date);
            } catch (ParseException pse) {}
        }
        throw new ParseException("Failed to parse date "+date, 0);
    }
    
    private static class SimpleDateFormatExt extends SimpleDateFormat {

    boolean convertTimezone = false;
    
    public SimpleDateFormatExt(String pattern){
        super();
        this.applyPattern(pattern);
    }
    
    @Override
    public void applyPattern(String pattern) {
        if ( pattern.indexOf("XXX") == pattern.length()-3){
            convertTimezone = true;
            pattern = pattern.substring(0, pattern.length()-3)+"Z";
            
        } else {
            convertTimezone = false;
        }
        
        super.applyPattern(pattern); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    public Date parse(String source) throws ParseException {
        if ( convertTimezone ){
            int len = source.length();
            if ( len >= 6 ){
                String base = source.substring(0, len-6);
                String tz = source.substring(len-6);
                tz = StringUtil.replaceAll(tz, ":", "");
                source = base+tz;
            }
        }
        return super.parse(source); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
    
}
