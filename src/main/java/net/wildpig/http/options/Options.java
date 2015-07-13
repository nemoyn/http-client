package net.wildpig.http.options;

import java.util.HashMap;
import java.util.Map;

/**
 * @FileName Options.java
 * @Description: 
 *
 * @Date Jul 7, 2015 
 * @author YangShengJun
 * @version 1.0
 * 
 */
public class Options {

	public static final long CONNECTION_TIMEOUT = 120000;
	private static final long SOCKET_TIMEOUT = 150000;
	public static final int MAX_TOTAL = 200;
	public static final int MAX_PER_ROUTE = 20;
	
	private static Map<Option, Object> options = new HashMap<Option, Object>();
	
	private static boolean customClientSet = false;
	
	public static void customClientSet() {
		customClientSet = true;
	}
	
	public static void setOption(Option option, Object value) {
		if ((option == Option.CONNECTION_TIMEOUT || option == Option.SOCKET_TIMEOUT) && customClientSet) {
			throw new RuntimeException("You can't set custom timeouts when providing custom client implementations. Set the timeouts directly in your custom client configuration instead.");
		}
		options.put(option, value);
	}
	
	public static Object getOption(Option option) {
		return options.get(option);
	}

	static {
		init();
	}
	
	public static void init() {
		// Load timeouts
		options.put(Option.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
		options.put(Option.SOCKET_TIMEOUT, SOCKET_TIMEOUT);
		
		// Load limits
		options.put(Option.MAX_TOTAL, MAX_TOTAL);
		options.put(Option.MAX_PER_ROUTE, MAX_PER_ROUTE);

	}
	
}
