package in.partake.base;

public class KeyValuePair extends Pair<String, String> {	
	public KeyValuePair(String key, String value) {
	    super(key, value);
	}
		
	public String getKey() {
	    return getFirst();
	}
	
	public String getValue() {
		return getSecond();
	}	
}
