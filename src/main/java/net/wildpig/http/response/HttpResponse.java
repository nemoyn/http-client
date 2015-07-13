package net.wildpig.http.response;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.wildpig.http.Headers;
import net.wildpig.http.utils.ResponseUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;


/**
 * @FileName HttpResponse.java
 * @Description: 
 *
 * @Date Jul 7, 2015 
 * @author YangShengJun
 * @version 1.0
 * 
 */
public class HttpResponse<T> {

	private int statusCode;
	private String statusText;
	private Headers headers = new Headers();
	private InputStream rawBody;
	private T body;
	private String strBody;
	
	//public HttpResponse(CloseableHttpResponse response, Class<T> responseClass) {
	@SuppressWarnings("unchecked")
	public HttpResponse(org.apache.http.HttpResponse response, Class<T> responseClass) {
		HttpEntity responseEntity = response.getEntity();
		
		Header[] allHeaders = response.getAllHeaders();
		for(Header header : allHeaders) {
			String headerName = header.getName().toLowerCase();
			List<String> list = headers.get(headerName);
			if (list == null) list = new ArrayList<String>();
			list.add(header.getValue());
			headers.put(headerName, list);
		}
		StatusLine statusLine = response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		this.statusText = statusLine.getReasonPhrase();

		response.setHeader("connection", "close");
		response.setHeader("Connection", "close");
		if (responseEntity != null) {
			String charset = "UTF-8";
			
			Header contentType = responseEntity.getContentType();
			if (contentType != null) {
				String responseCharset = ResponseUtils.getCharsetFromContentType(contentType.getValue());
				if (responseCharset != null && !responseCharset.trim().equals("")) {
					charset = responseCharset;
				}
			}
		
			try {
				byte[] rawBody;
				try {
					InputStream responseInputStream = responseEntity.getContent();
					if (ResponseUtils.isGzipped(responseEntity.getContentEncoding())) {
						responseInputStream = new GZIPInputStream(responseEntity.getContent());
					}
					rawBody = ResponseUtils.getBytes(responseInputStream);
				} catch (IOException e2) {
					throw new RuntimeException(e2);
				}
				//InputStream inputStream = new ByteArrayInputStream(rawBody);
				//this.rawBody = inputStream;

				if ( JSON.class.isAssignableFrom(responseClass)) {
					String jsonString = new String(rawBody, charset).trim();
					this.body = (T) getJson(jsonString);
				} else if (String.class.equals(responseClass)) {
					this.body = (T) new String(rawBody, charset);
				} else if (byte[].class.equals(responseClass)) {
					this.body = (T) rawBody;
				//} else if (InputStream.class.equals(responseClass)) {
				//	this.body = (T) this.rawBody;
				} else {
					throw new Exception("Unknown result type. Only String, JSON and InputStream and byte[] are supported.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}	
		}
		
		try {
			EntityUtils.consume(responseEntity);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public int getStatus() {
		return statusCode;
	}
	
	public String getStatusText() {
		return statusText;
	}

	public Headers getHeaders() {
		return headers;
	}

	public InputStream getRawBody() {
		return rawBody;
	}

	public T getBody() {
		return body;
	}

	private JSON getJson(String json){
		JSON jsonObj;
		if(json==null){
			return null;
		}
			
		if(json.startsWith("[")){
			jsonObj = JSONArray.parseArray(json);
		}else if(json.startsWith("{")){
			jsonObj = JSONObject.parseObject(json);
		}else{
			System.err.println("it is not json string");;
			return null;
		}
		return jsonObj;
	}

	public String getStrBody() {
		return strBody;
	}

	public void setStrBody(String strBody) {
		this.strBody = strBody;
	}
}
