/*
 * For demonstration purposes, this object simply wraps the raw
 *  message string. Another example could be using the org.json
 *  library and parsing the raw message string into a JSONObject
 *  and handling it from there. That way json messages could be
 *  sent and published and automatically parsed for you.
 */
public class TestObject {
	private final String value;
	
	public TestObject(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
