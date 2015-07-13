

package net.wildpig.http;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * @FileName JSONHelper.java
 * @Description: 
 *
 * @Date Jul 8, 2015 
 * @author YangShengJun
 * @version 1.0
 * 
 */
public class JSONHelper {
	public static <T> String format(T json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String formatedJson=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		return formatedJson;
	}
	
	public static <T> void formatPrint(T json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		String formatedJson=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		System.out.println(formatedJson);
	}
}
