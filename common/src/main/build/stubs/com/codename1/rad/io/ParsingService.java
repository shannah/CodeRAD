package com.codename1.rad.io;


/**
 *  An service that provides JSON/XML parsing asynchronously on a background thread.
 *  @author shannah
 */
public class ParsingService {

	public ParsingService() {
	}

	/**
	 *  Stops the background thread.  It will be automatically started again if required.
	 */
	public void stop() {
	}

	/**
	 *  Sets the thread priority.
	 *  @param priority 
	 *  @see Thread#setPriority(int) 
	 */
	public void setPriority(int priority) {
	}

	/**
	 *  Parses JSON content using the provided parser.
	 *  @param <T> 
	 *  @param content JSON content to parse.
	 *  @param parser Parser to use to parse the content.
	 *  @param entity The entity to "fill" with parsed content.
	 *  @return 
	 */
	public <any> parseJSON(java.io.Reader content, ResultParser parser, com.codename1.rad.models.Entity entity) {
	}

	public <any> parseJSON(String content, JSONParser parser) {
	}

	public <any> parseJSON(java.io.InputStream content, JSONParser parser) {
	}

	public <any> parseJSON(java.io.Reader content) {
	}

	public <any> parseJSON(String content) {
	}

	public <any> parseJSON(java.io.InputStream content) {
	}

	public <any> parseJSON(java.io.Reader content, JSONParser parser) {
	}

	/**
	 *  Parses JSON content using the provided parser.
	 *  @param <T> 
	 *  @param content JSON content to parse.
	 *  @param parser Parser to use to parse the content.
	 *  @param entity The entity to "fill" with parsed content.
	 *  @return 
	 */
	public <any> parseJSON(java.io.InputStream content, ResultParser parser, com.codename1.rad.models.Entity entity) {
	}

	/**
	 *  Parses JSON content using the provided parser.
	 *  @param <T> 
	 *  @param content JSON content to parse.
	 *  @param parser Parser to use to parse the content.
	 *  @param entity The entity to "fill" with parsed content.
	 *  @return 
	 */
	public <any> parseJSON(String content, ResultParser parser, com.codename1.rad.models.Entity entity) {
	}

	/**
	 *  Parses XML content using the provided parser.
	 *  @param <T> 
	 *  @param content XML content to parse.
	 *  @param parser Parser to use to parse the content.
	 *  @param entity The entity to "fill" with parsed content.
	 *  @return 
	 */
	public <any> parseXML(String content, ResultParser parser, com.codename1.rad.models.Entity entity) {
	}

	/**
	 *  Parses XML content using the provided parser.
	 *  @param <T> 
	 *  @param content XML content to parse.
	 *  @param parser Parser to use to parse the content.
	 *  @param entity The entity to "fill" with parsed content.
	 *  @return 
	 */
	public <any> parseXML(java.io.InputStream content, ResultParser parser, com.codename1.rad.models.Entity entity) {
	}

	/**
	 *  Parses XML content using the provided parser.
	 *  @param <T> 
	 *  @param content XML content to parse.
	 *  @param parser Parser to use to parse the content.
	 *  @param entity The entity to "fill" with parsed content.
	 *  @return 
	 */
	public <any> parseXML(java.io.Reader content, ResultParser parser, com.codename1.rad.models.Entity entity) {
	}

	public <any> parseXML(java.io.Reader content) {
	}

	public <any> parseXML(java.io.Reader content, XMLParser parser) {
	}

	public <any> parseXML(java.io.InputStream content, XMLParser parser) {
	}

	public <any> parseXML(java.io.InputStream content) {
	}

	public <any> parseXML(String content, XMLParser parser) {
	}

	public <any> parseXML(String content) {
	}
}
