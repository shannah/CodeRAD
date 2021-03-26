package com.codename1.rad.processing;


/**
 *  NOTE: This class is based on the core {@link com.codename1.processing.Result} class, but has been modified to improve performance,
 *  fix bugs, and add support for additional syntaxes.  We chose to "fork" the {@link com.codename1.processing.Result} rather than
 *  fix it because legacy software may depend on the old behaviour, even if it is considered a bug.  The safe solution, was to
 *  create a new class.
 *  
 *  An evaluator for a very small expression language to extract primitive types
 *  from structured information. This implementation is layered over the
 *  {@link com.codename1.io.JSONParser} and {@link com.codename1.xml.XMLParser} classes. This
 *  expression language allows applications to extract information from
 *  structured data returned by web services with minimal effort. You can read more about it {@link com.codename1.rad.processing here}.
 * 
 *  The expression language works a lot like a very small subset of XPath - the
 *  expression syntax uses the / character for sub-elements and square brackets
 *  for arrays.
 * 
 *  Some sample expressions:
 * 
 *  
 *   Simple expression, get the title of the first photo element::
 *   `/photos/photo[1]/title`
 * 
 *   Globally find the first name of a person with a last name of 'Coolman'::
 *   `//person[lastname='Coolman']/firstName`
 * 
 *   Get the latitude value of the second last result element::
 *   `/results[last()-1]/geometry/bounds/northeast/lat`
 * 
 *   Get the names of players from Germany::
 *   `/tournament/player[@nationality='Germany']/name`
 * 
 *   Get the purchase order numbers of any order with a lineitem worth over $5::
 *   `//order/lineitem[price > 5]/../@ponum`
 *  
 *  etc
 *  
 * 
 *  @author Eric Coolman (2012-03 - derivative work from original Sun source).
 */
public class Result {

	public static final String JSON = "json";

	public static final String XML = "xml";

	public static final char SEPARATOR = 47;

	public static final char ARRAY_START = 91;

	public static final char ARRAY_END = 93;

	/**
	 *  Create an evaluator object from a structured content document (XML, JSON,
	 *  etc) as a string.
	 * 
	 *  @param content structured content document as a string.
	 *  @param format an identifier for the type of content passed (ie. xml,
	 *  json, etc).
	 *  @return Result a result evaluator object
	 *  @throws IllegalArgumentException thrown if null content or format is
	 *  passed.
	 *  @since 7.0
	 */
	public static Result fromContent(String content, String format) {
	}

	/**
	 *  Create an evaluator object from a structured content document (XML, JSON,
	 *  etc) input stream. Normally you would use this method within a content
	 *  request implementation, for example:
	 * 
	 *  [source,java]
	 *  ----
	 *  ConnectionRequest request = new ConnectionRequest() {
	 *  	protected void readResponse(InputStream input) throws IOException {
	 *  		Result evaluator = Result.fromContent(input, Result.JSON);
	 *  		// ... evaluate the result here
	 *  	}
	 *  	// ... etc
	 *  };
	 *  ----
	 * 
	 * 
	 * 
	 * 
	 *  @param content structured content document as a string.
	 *  @param format an identifier for the type of content passed (ie. xml,
	 *  json, etc).
	 *  @return Result a result evaluator object
	 *  @throws IllegalArgumentException thrown if null content or format is
	 *  passed.
	 *  @since 7.0
	 */
	public static Result fromContent(java.io.InputStream content, String format) {
	}

	/**
	 *  Create an evaluator object from a structured content document (XML, JSON,
	 *  etc) input stream. Normally you would use this method within a content
	 *  request implementation, for example:
	 * 
	 *  [source,java]
	 *  ----
	 *  ConnectionRequest request = new ConnectionRequest() {
	 *  	protected void readResponse(InputStream input) throws IOException {
	 *  		Result evaluator = Result.fromContent(input, Result.JSON);
	 *  		// ... evaluate the result here
	 *  	}
	 *  	// ... etc
	 *  };
	 *  ----
	 * 
	 * 
	 * 
	 *  @param content structured content document as a string.
	 *  @param format an identifier for the type of content passed (ie. xml,
	 *  json, etc).
	 *  @return Result a result evaluator object
	 *  @throws IllegalArgumentException thrown if null content or format is
	 *  passed.
	 *  @since 7.0
	 */
	public static Result fromContent(java.io.Reader content, String format) {
	}

	/**
	 *  Create an evaluator object from a parsed XML DOM.
	 * 
	 *  @param content a parsed XML DOM.
	 *  @return Result a result evaluator object
	 *  @throws IllegalArgumentException thrown if null content is passed.
	 *  @since 7.0
	 */
	public static Result fromContent(Element content) {
	}

	/**
	 *  Returns a hashcode value for the object.
	 * 
	 *  @see Object#hashCode()
	 */
	public int hashCode() {
	}

	/**
	 *  Indicates whether some other object is "equal to" this one.
	 * 
	 *  @see Object#equals(Object)
	 */
	public boolean equals(Object other) {
	}

	/**
	 *  Convert the object to a formatted structured content document. For
	 *  example, an XML or JSON document.
	 * 
	 *  @return a structured content document as a string
	 */
	public String toString() {
	}

	/**
	 *  Get a boolean value from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *  "settings" : [
	 *  {
	 *      "toggle" : "true",
	 *      ... etc
	 *  }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  boolean value = result.getAsBoolean("/settings[0]/toggle");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public boolean getAsBoolean(String path) {
	}

	/**
	 *  Get an integer value from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *  "settings"
	 *  {
	 *      "connection"
	 *      {
	 *           "max_retries" : "20",
	 *           ... etc
	 *      }
	 *  }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  int value = result.getAsInteger("//connection/max_retries");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalException on error traversing the document, ie. traversing
	 *  into an array without using subscripts.
	 */
	public int getAsInteger(String path) {
	}

	/**
	 *  Get a long value from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,java]
	 *  ----
	 *  {
	 *  "settings"
	 *  {
	 *      "connection"
	 *      {
	 *           "timeout_milliseconds" : "100000",
	 *           ... etc
	 *      }
	 *  }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  long value = result.getAsLong("/settings/connection/timeout_milliseconds");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public long getAsLong(String path) {
	}

	/**
	 *  Get a double value from the requested path.
	 * 
	 *  For example: **JSON**:
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *   "geometry" : {
	 *     "bounds" : {
	 *       "northeast" : {
	 *         "lat" : 42.94959820,
	 *         "lng" : -81.24873959999999
	 *        },
	 *        "southwest" : {
	 *          "lat" : 42.94830,
	 *          "lng" : -81.24901740000001
	 *        }
	 *     },
	 *     "location" : {
	 *       "lat" : 42.94886990,
	 *       "lng" : -81.24876030
	 *     },
	 *     "location_type" : "RANGE_INTERPOLATED",
	 *     "viewport" : {
	 *       "northeast" : {
	 *          "lat" : 42.95029808029150,
	 *          "lng" : -81.24752951970851
	 *       },
	 *       "southwest" : {
	 *          "lat" : 42.94760011970850,
	 *           "lng" : -81.25022748029151
	 *       }
	 *    }
	 *    // etc
	 *  ----
	 * 
	 *  **Expression:**
	 *  
	 *  [source,java]
	 *  ----
	 *  double neBoundsLat = result.getAsDouble("//bounds/northeast/lat");
	 *  double neBoundsLong = result.getAsDouble("//bounds/northeast/lng");
	 *  double swBoundsLat = result.getAsDouble("//bounds/southwest/lat");
	 *  double swBoundsLong = result.getAsDouble("//bounds/southwest/lng");
	 * 
	 *  double memberDiscount = result.getAsDouble("pricing.members.members");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public double getAsDouble(String path) {
	}

	/**
	 *  Get a string value from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *  "profile"
	 *  {
	 *      "location"
	 *      {
	 *           "city" : "London",
	 *           "region" : "Ontario",
	 *           "country" : "Canada",
	 *           ... etc
	 *      },
	 *  }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  String city = result.getAsDouble("//city");
	 *  String province = result.getAsDouble("//location//region");
	 *  String country = result.getAsDouble("profile//location//country");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public String getAsString(String path) {
	}

	/**
	 *  Get the object value from the requested path. This method may return a
	 *  Map, List, String, or null.
	 * 
	 *  @param path
	 *  @return the object at the given path, or null.
	 *  @throws IllegalArgumentException
	 */
	public Object get(String path) {
	}

	/**
	 *  Get the size of an array at the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *     "results" : [
	 *        {
	 *          "address_components" : [
	 *            {
	 *              "long_name" : "921-989",
	 *              "short_name" : "921-989",
	 *              "types" : [ "street_number" ]
	 *            },
	 *            {
	 *              "long_name" : "Country Club Crescent",
	 *              "short_name" : "Country Club Crescent",
	 *              "types" : [ "route" ]
	 *            },
	 *            {
	 *              "long_name" : "Ontario",
	 *              "short_name" : "ON",
	 *              "types" : [ "administrative_area_level_1", "political" ]
	 *            },
	 *            ... etc
	 *        }
	 *   }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  int size = result.getSizeOfArray("/results[0]/address_components");
	 *  int size2 = result.getSizeOfArray("results");
	 *  int size3 = result.getSizeOfArray("/results[0]/address_components[2]/types");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public int getSizeOfArray(String path) {
	}

	/**
	 *  Get an array of string values from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *     "results" : [
	 *        {
	 *          "address_components" : [
	 *            {
	 *              "long_name" : "921-989",
	 *              "short_name" : "921-989",
	 *              "types" : [ "street_number" ]
	 *            },
	 *            {
	 *              "long_name" : "Country Club Crescent",
	 *              "short_name" : "Country Club Crescent",
	 *              "types" : [ "route" ]
	 *            },
	 *            {
	 *              "long_name" : "Ontario",
	 *              "short_name" : "ON",
	 *              "types" : [ "administrative_area_level_1", "political" ]
	 *            },
	 *            ... etc
	 *        }
	 *   }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  String types[] = result
	 *  		.getAsStringArray("/results[0]/address_components[2]/types");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public String[] getAsStringArray(String path) {
	}

	/**
	 *  Get an array of values from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  ----
	 *  {
	 *     "results" : [
	 *        {
	 *          "address_components" : [
	 *            {
	 *              "long_name" : "921-989",
	 *              "short_name" : "921-989",
	 *              "types" : [ "street_number" ]
	 *            },
	 *            {
	 *              "long_name" : "Country Club Crescent",
	 *              "short_name" : "Country Club Crescent",
	 *              "types" : [ "route" ]
	 *            },
	 *            {
	 *              "long_name" : "Ontario",
	 *              "short_name" : "ON",
	 *              "types" : [ "administrative_area_level_1", "political" ]
	 *            },
	 *            ... etc
	 *        }
	 *   }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  String types[] = result
	 *  		.getAsStringArray("/results[0]/address_components[2]/types");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 *  @throws NumberFormatException if the value at path can not be converted
	 *  to an integer.
	 */
	public int[] getAsIntegerArray(String path) {
	}

	/**
	 *  Get an array of values from the requested path.
	 *  [source,java]
	 *  ----
	 *  String types[] = result
	 *  		.getAsStringArray("/results[0]/address_components[2]/types");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 *  @throws NumberFormatException if the value at path can not be converted
	 *  to a long.
	 */
	public long[] getAsLongArray(String path) {
	}

	/**
	 *  Get an array of values from the requested path.
	 *  
	 *  [source,java]
	 *  ----
	 *  String types[] = result
	 *  		.getAsStringArray("/results[0]/address_components[2]/types");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 *  @throws NumberFormatException if the value at path can not be converted
	 *  to a double.
	 */
	public double[] getAsDoubleArray(String path) {
	}

	/**
	 *  Get an array of values from the requested path.
	 *  
	 *  [source,java]
	 *  ----
	 *  String types[] = result
	 *  		.getAsStringArray("/results[0]/address_components[2]/types");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public boolean[] getAsBooleanArray(String path) {
	}

	/**
	 *  Get a List of values from the requested path.
	 * 
	 *  For example: **JSON**
	 * 
	 *  [source,json]
	 *  {
	 *     "results" : [
	 *        {
	 *          "address_components" : [
	 *            {
	 *              "long_name" : "921-989",
	 *              "short_name" : "921-989",
	 *              "types" : [ "street_number" ]
	 *            },
	 *            {
	 *              "long_name" : "Country Club Crescent",
	 *              "short_name" : "Country Club Crescent",
	 *              "types" : [ "route" ]
	 *            },
	 *            ... etc
	 *        }
	 *   }
	 *  ----
	 * 
	 *  **Expression**
	 * 
	 *  [source,java]
	 *  ----
	 *  List addressComponents = result.getAsList("/results[0]/address_components");
	 *  result = Result.fromContent(addressComponents);
	 *  String longName = result.getAsString("[1]/long_name");
	 *  ----
	 * 
	 *  @param path Path expression to evaluate
	 *  @return the value at the requested path
	 *  @throws IllegalArgumentException on error traversing the document, ie.
	 *  traversing into an array without using subscripts.
	 */
	public java.util.List getAsArray(String path) {
	}

	public void mapNamespaceAlias(String namespaceURI, String alias) {
	}
}
