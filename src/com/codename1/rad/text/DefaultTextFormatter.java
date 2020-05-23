/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.text;

import com.codename1.l10n.ParseException;

/**
 *
 * @author shannah
 */
public class DefaultTextFormatter implements TextFormatter {
    private FormatCallback formatCallback;
    private ParseCallback parseCallback;
    
    public DefaultTextFormatter(FormatCallback callback) {
        formatCallback = callback;
    }
    
    public DefaultTextFormatter(FormatCallback callback, ParseCallback parseCb) {
        this.formatCallback = callback;
        this.parseCallback = parseCb;
    }
    
    @Override
    public String format(String text) {
        if (formatCallback != null) {
            return formatCallback.format(text);
        }
        return text;
    }

    @Override
    public String parse(String text) throws ParseException {
        if (formatCallback != null) {
            if (parseCallback == null) {
                throw new ParseException("Parsing not supported by this formatter", 0);
            } else {
                return parseCallback.parse(text);
            }
        } else {
            if (parseCallback != null) {
                return parseCallback.parse(text);
            } else {
                return text;
            }
        }
        
    }

    @Override
    public boolean supportsParse() {
        return parseCallback != null || formatCallback == null;
    }
    
    public static interface FormatCallback {
        public String format(String string);
    }
    
    public static interface ParseCallback {
        public String parse(String string) throws ParseException;
    }
    
}
