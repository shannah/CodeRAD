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
package com.codename1.rad.io;

import com.codename1.io.JSONParser;

import com.codename1.util.AsyncResource;
import com.codename1.util.EasyThread;
import com.codename1.xml.Element;
import com.codename1.xml.XMLParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import com.codename1.rad.models.Entity;

/**
 * An service that provides JSON/XML parsing asynchronously on a background thread.
 * @author shannah
 */
public class ParsingService {
    private EasyThread thread;
    private boolean autoCloseStreams = true;
    private int priority;
    
    /**
     * Stops the background thread.  It will be automatically started again if required.
     */
    public void stop() {
        if (thread != null) {
            thread.kill();
            thread = null;
        }
    }
    
    /**
     * Sets the thread priority.
     * @param priority 
     * @see Thread#setPriority(int) 
     */
    public void setPriority(int priority) {
        this.priority = priority;
        if (thread != null) {
            thread.setPriority(priority);
        }
        
    }
    
    private void start() {
        if (thread == null) {
            thread = EasyThread.start("ResultParserService");
            if (priority != 0) {
                thread.setPriority(priority);
            }
        }
    }
    
    
    
    /**
     * Parses JSON content using the provided parser.
     * @param <T> 
     * @param content JSON content to parse.
     * @param parser Parser to use to parse the content.
     * @param entity The entity to "fill" with parsed content.
     * @return 
     */
    public <T extends Entity> AsyncResource<T> parseJSON(Reader content, ResultParser parser, T entity) {
        AsyncResource<T> out = new AsyncResource<T>();
        start();
        thread.run(()-> {
            try {
                parser.parseJSON(content, entity);
                if (autoCloseStreams) content.close();
                out.complete(entity);
                
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    public AsyncResource<Map> parseJSON(String content, JSONParser parser) {
        AsyncResource<Map> out = new AsyncResource<Map>();
        start();
        thread.run(()-> {
            try {
                
                Map m = parser.parseJSON(new StringReader(content));

                out.complete(m);
                
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
    }
    
    public AsyncResource<Map> parseJSON(InputStream content, JSONParser parser) {
        AsyncResource<Map> out = new AsyncResource<Map>();
        start();
        thread.run(()-> {
            try {
                
                Map m = parser.parseJSON(new InputStreamReader(content, "UTF-8"));
                if (autoCloseStreams) content.close();
                out.complete(m);
                
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
    }
    
    private JSONParser newJSONParser() {
        JSONParser out = new JSONParser();
        out.setUseBooleanInstance(true);
        out.setIncludeNullsInstance(true);
        return out;
    }
    
    public AsyncResource<Map> parseJSON(Reader content) {
        return parseJSON(content, newJSONParser());
    }
    public AsyncResource<Map> parseJSON(String content) {
        return parseJSON(content, newJSONParser());
    }
    public AsyncResource<Map> parseJSON(InputStream content) {
        return parseJSON(content, newJSONParser());
    }
    
    public AsyncResource<Map> parseJSON(Reader content, JSONParser parser) {
        AsyncResource<Map> out = new AsyncResource<Map>();
        start();
        thread.run(()-> {
            try {
                
                Map m = parser.parseJSON(content);
                if (autoCloseStreams) content.close();
                out.complete(m);
                
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
    }
    
    /**
     * Parses JSON content using the provided parser.
     * @param <T> 
     * @param content JSON content to parse.
     * @param parser Parser to use to parse the content.
     * @param entity The entity to "fill" with parsed content.
     * @return 
     */
    public <T extends Entity> AsyncResource<T> parseJSON(InputStream content, ResultParser parser, T entity) {
        AsyncResource<T> out = new AsyncResource<T>();
        start();
        thread.run(()-> {
            try {
                parser.parseJSON(content, entity);
                if (autoCloseStreams) content.close();
                out.complete(entity);
                
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    /**
     * Parses JSON content using the provided parser.
     * @param <T> 
     * @param content JSON content to parse.
     * @param parser Parser to use to parse the content.
     * @param entity The entity to "fill" with parsed content.
     * @return 
     */
    public <T extends Entity> AsyncResource<T> parseJSON(String content, ResultParser parser, T entity) {
        AsyncResource<T> out = new AsyncResource<T>();
        start();
        thread.run(()-> {
            try {
                System.out.println("About to parse JSON on easythread");
                parser.parseJSON(content, entity);
                System.out.println("Finished parsing JSON on easythread");
                out.complete(entity);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    /**
     * Parses XML content using the provided parser.
     * @param <T> 
     * @param content XML content to parse.
     * @param parser Parser to use to parse the content.
     * @param entity The entity to "fill" with parsed content.
     * @return 
     */
    public <T extends Entity> AsyncResource<T> parseXML(String content, ResultParser parser, T entity) {
        AsyncResource<T> out = new AsyncResource<T>();
        start();
        thread.run(()-> {
            try {
                parser.parseXML(content, entity);
                
                out.complete(entity);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    /**
     * Parses XML content using the provided parser.
     * @param <T> 
     * @param content XML content to parse.
     * @param parser Parser to use to parse the content.
     * @param entity The entity to "fill" with parsed content.
     * @return 
     */
    public <T extends Entity> AsyncResource<T> parseXML(InputStream content, ResultParser parser, T entity) {
        AsyncResource<T> out = new AsyncResource<T>();
        start();
        thread.run(()-> {
            try {
                parser.parseXML(content, entity);
                if (autoCloseStreams) content.close();
                out.complete(entity);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    /**
     * Parses XML content using the provided parser.
     * @param <T> 
     * @param content XML content to parse.
     * @param parser Parser to use to parse the content.
     * @param entity The entity to "fill" with parsed content.
     * @return 
     */
    public <T extends Entity> AsyncResource<T> parseXML(Reader content, ResultParser parser, T entity) {
        AsyncResource<T> out = new AsyncResource<T>();
        start();
        thread.run(()-> {
            try {
                parser.parseXML(content, entity);
                if (autoCloseStreams) content.close();
                out.complete(entity);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    public  AsyncResource<Element> parseXML(Reader content) {
        return parseXML(content, new XMLParser());
    }
    public  AsyncResource<Element> parseXML(Reader content, XMLParser parser) {
        AsyncResource<Element> out = new AsyncResource<Element>();
        start();
        thread.run(()-> {
            try {
                Element el = parser.parse(content);
                if (autoCloseStreams) content.close();
                out.complete(el);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    public  AsyncResource<Element> parseXML(InputStream content, XMLParser parser) {
        AsyncResource<Element> out = new AsyncResource<Element>();
        start();
        thread.run(()-> {
            try {
                Element el = parser.parse(new InputStreamReader(content, "UTF-8"));
                if (autoCloseStreams) content.close();
                out.complete(el);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    public  AsyncResource<Element> parseXML(InputStream content) {
        return parseXML(content, new XMLParser());
    }
    public  AsyncResource<Element> parseXML(String content, XMLParser parser) {
        AsyncResource<Element> out = new AsyncResource<Element>();
        start();
        thread.run(()-> {
            try {
                Element el = parser.parse(new StringReader(content));
                
                out.complete(el);
            } catch (Throwable ex) {
                out.error(ex);
            }
        });
        return out;
        
    }
    
    public AsyncResource<Element> parseXML(String content) {
        return parseXML(content, new XMLParser());
    }
}
