package net.wildpig.http;

import net.wildpig.http.options.Option;
import net.wildpig.http.options.Options;
import net.wildpig.http.response.HttpResponse;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHelper {

	private static Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);
	
	private static final String CONTENT_TYPE = "content-type";
	// private static final String ACCEPT_ENCODING_HEADER = "accept-encoding";
	// private static final String USER_AGENT_HEADER = "user-agent";
	// private static final String USER_AGENT = "gvhttps";
	//private static HttpClient client;
	private static CloseableHttpClient client;

	static {
		//createHttpClient();
	}

	public static void createHttpClient() {

		// Create common default configuration
		RequestConfig clientConfig = RequestConfig.custom()
				.setConnectTimeout(((Long) Options.getOption(Option.CONNECTION_TIMEOUT)).intValue())
				.setSocketTimeout(((Long) Options.getOption(Option.SOCKET_TIMEOUT)).intValue())
				.setConnectionRequestTimeout(((Long) Options.getOption(Option.SOCKET_TIMEOUT)).intValue()).build();

		//PoolingHttpClientConnectionManager syncConnectionManager = new PoolingHttpClientConnectionManager(1,  java.util.concurrent.TimeUnit.MINUTES);
		PoolingHttpClientConnectionManager syncConnectionManager = new PoolingHttpClientConnectionManager();
		syncConnectionManager.setMaxTotal((Integer) Options.getOption(Option.MAX_TOTAL));
		syncConnectionManager.setDefaultMaxPerRoute((Integer) Options.getOption(Option.MAX_PER_ROUTE));

		ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy(){

	
			public long getKeepAliveDuration(final org.apache.http.HttpResponse response, final HttpContext context) {
		
		        long keepAlive = super.getKeepAliveDuration(response, context);
		        logger.trace("\n\nHttpClientHelper, keepAlive:{}", keepAlive);
//		        if (keepAlive == -1) {
//		            // Keep connections alive 5 seconds if a keep-alive value
//		            // has not be explicitly set by the server
//		            keepAlive = 5000;
//		        }
//		        return keepAlive;
				return -1;
		    }

		};
		// Create clients
		client = HttpClientBuilder.create().setKeepAliveStrategy(keepAliveStrat).setDefaultRequestConfig(clientConfig).setConnectionManager(syncConnectionManager).build();
		// TODO to add monitor thread

	}

	public static <T> HttpResponse<T> getJson(String url, Class<T> resBodyType) throws Exception {
		HttpGet request = new HttpGet(url);

		request.setHeader(CONTENT_TYPE, "application/json");
		request.setHeader("Connection", "close");
		org.apache.http.HttpResponse response;

		CloseableHttpClient client=null;
		try {
			client= HttpClients.createDefault();
			response = client.execute(request);
			HttpResponse<T> httpResponse = new HttpResponse<T>(response, resBodyType);
			request.releaseConnection();
			return httpResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			request.releaseConnection();
			if(client!=null) client.close();
		}
	}


	public static <T> HttpResponse<T> delete(String url, Class<T> resBodyType) throws Exception {
		HttpDelete request = new HttpDelete(url);

		request.setHeader(CONTENT_TYPE, "application/json");
		request.setHeader("Connection", "close");
		org.apache.http.HttpResponse response;

		CloseableHttpClient client=null;
		try {
			client= HttpClients.createDefault();
			response = client.execute(request);
			HttpResponse<T> httpResponse = new HttpResponse<T>(response, resBodyType);
			request.releaseConnection();
			return httpResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			request.releaseConnection();
			if(client!=null) client.close();
		}
	}

	public static HttpResponse head(String url) throws Exception {
		HttpHead request = new HttpHead(url);

		request.setHeader(CONTENT_TYPE, "application/json");
		request.setHeader("Connection", "close");
		org.apache.http.HttpResponse response;

		CloseableHttpClient client=null;
		try {
			client= HttpClients.createDefault();
			response = client.execute(request);
			HttpResponse httpResponse = new HttpResponse(response,null);
			request.releaseConnection();
			return httpResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			request.releaseConnection();
			if(client!=null) client.close();
		}
	}

	public static <T> HttpResponse<T> postJson(String url, String jsonStr, Class<T> resBodyType) throws Exception {
		HttpPost request = new HttpPost(url);
		StringEntity stringEntity = new StringEntity(jsonStr, Consts.UTF_8);
		request.setEntity(stringEntity);
		request.setHeader(CONTENT_TYPE, "application/json");
		request.setHeader("Connection", "close");
		org.apache.http.HttpResponse response;

		CloseableHttpClient client=null;
		try {
			client= HttpClients.createDefault();
			response = client.execute(request);
			HttpResponse<T> httpResponse = new HttpResponse<T>(response, resBodyType);
			request.releaseConnection();
			return httpResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			request.releaseConnection();
			if(client!=null) client.close();
		}
	}

	public static <T> HttpResponse<T> postBin(String url, byte[] data, Class<T> resBodyType) throws Exception {
		HttpPost request = new HttpPost(url);
		ByteArrayEntity byteEntity = new ByteArrayEntity(data);
		request.setEntity(byteEntity);
		request.setHeader(CONTENT_TYPE, "application/octet-stream");
		request.setHeader("Connection", "close");
		request.setHeader("connection", "close");
		org.apache.http.HttpResponse response;
		CloseableHttpClient client=null;
		try {
			client= HttpClients.createDefault();
			response = client.execute(request);
			HttpResponse<T> httpResponse = new HttpResponse<T>(response, resBodyType);
			request.releaseConnection();
			//client.getConnectionManager().shutdown();
			return httpResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			request.releaseConnection();
			if(client!=null) client.close();
		}
	}

}
