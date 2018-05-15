package com.ht.ussp.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 摘要：HttpClient 工具类(单例)
 * @author xyt
 * @version 1.0
 * @Date 2016年12月13日
 */
public class HttpClientUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private static PoolingHttpClientConnectionManager connMgr;
	private static  RequestConfig requestConfig = null;
	private int statusCode = 0;
	private	int CONNECT_TIMEOUT = 10000;  			//设置连接超时时间10秒
	private int SOCKET_TIMEOUT = 30000;  			//设置数据传输超时时间30秒

	static{
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
	}
	
	private HttpClientUtil() { 
	}
	private static class HttpClientInstance {
		private static final HttpClientUtil instance = new HttpClientUtil();
	}
	public static HttpClientUtil getInstance() {
		return HttpClientInstance.instance;
	}
	
	/**
	 * HttpClient的POST请求
	 * @param url 请求的地址
	 * @param params 请求的键值参数
	 * @param headers 头部参数
	 * @return 把地址返回的JSON转为Map
	 */
	public String doPostWithMap(String url, Map<String, String> params, Map<String,String> headers) {
		logger.debug("doPost->url:{},param:{}",new Object[]{url,params});
		return doPostWithMap(url, params, headers, "utf-8");
	}
	
	/**
	 * HttpClient的POST请求
	 * @param url 请求的地址
	 * @param JSON 请求的参数为JSON字符串
	 * @param headers 头部参数
	 * @return 把地址返回的JSON转为Map
	 */
	public String doPostWithJson(String url, String JSON, Map<String,String> headers) {
		logger.debug("doPost->url:{},param:{}",new Object[]{url,JSON});
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(EntityBuilder.create().setText(JSON).build());
		return post(httpPost,headers);
	}
	
	/**
	 * HttpClient的POST请求
	 * @param url 请求的地址
	 * @param binary 请求的参数为字节数据,会以流传到url
	 * @param headers 头部参数
	 * @return 把地址返回的JSON转为Map
	 */
	public String doPostWithByte(String url, byte[] binary, Map<String,String> headers) {
		logger.debug("doPost->url:{},param:{}",new Object[]{url,new String(binary)});
		return doPostWithByte(url, binary, headers, "utf-8");
	}
	
	/**
	 * HttpClient的POST请求
	 * @param url 请求的地址
	 * @param params 请求的键值参数
	 * @param headers 头部参数
	 * @param charset 要求返回时转换为某字符集
	 * @return 返回请求后的实体内容字符串
	 */
	public String doPostWithMap(String url, Map<String, String> params,Map<String,String> headers,String charset) {
		logger.debug("doPost->url:{},param:{},charset:{}",new Object[]{url,params,charset});
		HttpEntity httpEntity = buildHttpEntity(params);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(httpEntity);
		return post(httpPost,headers);
	}	
	
	/**
	 * HttpClient的POST请求
	 * @param url 请求的地址
	 * @param JSON 请求的参数为JSON字符串
	 * @param headers 头部参数
	 * @param charset 要求返回时转换为某字符集
	 * @return 返回请求后的实体内容字符串
	 */
	public String doPostWithJson(String url, String JSON,Map<String,String> headers, String charset) {
		logger.debug("doPost->url:{},param:{},charset:{}",new Object[]{url,JSON,charset});
		Map<String,String> params = fromJSON(JSON);
		return doPostWithMap(url, params, headers, charset);
	}
	
	/**
	 * HttpClient的POST请求
	 * @param url 请求的地址
	 * @param binary 请求的参数为字节数据,会以流传到url
	 * @param headers 头部参数
	 * @param charset 要求返回时转换为某字符集
	 * @return 返回请求后的实体内容字符串
	 */
	public String doPostWithByte(String url, byte[] binary, Map<String,String> headers, String charset) {
		logger.debug("doPost->url:{},param:{},charset:{}",new Object[]{url,new String(binary),charset});
		HttpEntity httpEntity = buildHttpEntity(binary);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(httpEntity);
		return post(httpPost,headers);
	}

	/**
	 * HttpClient的GET请求
	 * @param url 请求的地址
	 * @param headers 头部参数
	 */
	public String doGet(String url, Map<String,String> headers) {
		logger.debug("doGet->url:{}",url);
		HttpGet  httpGet = new HttpGet(url);
		return get(httpGet,headers);
	}
	
	/**
	 * 根据键值参数,创建请求的实体
	 * @param params 键值参数
	 * @return 请求的实体HttpEntity
	 */
	private HttpEntity buildHttpEntity(Map<String, String> params) {
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            nvp.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
        	return new UrlEncodedFormEntity(nvp, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 根据字节数据,创建请求的实体
	 * @param binary 字节数据
	 * @return 请求的实体HttpEntity
	 */
	private HttpEntity buildHttpEntity(byte[] binary) {
		return EntityBuilder.create().setBinary(binary).build();
	}
	
	/**
	 * 执行POST
	 * @param httpPost HttpClient执行的httpPost
	 * @param headers 头部参数
	 * @return Post请求的结果内容
	 */
	private String post(HttpPost httpPost,Map<String,String> headers) {
		try {
			//设置头部信息
			if(headers != null){
				for(Map.Entry<String, String> entry : headers.entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			//设置连接超时时间和处理时间
			requestConfig = RequestConfig.custom().setSocketTimeout(this.SOCKET_TIMEOUT).setConnectTimeout(this.CONNECT_TIMEOUT).build();
			httpPost.setConfig(requestConfig);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				logger.info("post->post data success! url:{}",httpPost.getURI());
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toString(httpEntity, "utf-8");
			} else {
				logger.error("post->post data error from {} , error code:{}",new Object[]{httpPost.getURI(),statusCode});
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toString(httpEntity, "utf-8");
			}
		} catch (Exception e) {
			logger.error("post->post data error from {} , error:{}",new Object[]{httpPost.getURI(),e});
			return null;
		}
	}
	
	/**
	 * 执行get
	 * @param httpGet HttpClient执行的httpGet
	 * @param headers 头部参数
	 */
	private String get(HttpGet httpGet,Map<String,String> headers){
		try {
			//设置头部信息
			if(headers != null){
				for(Map.Entry<String, String> entry : headers.entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}
			//设置连接超时时间和处理时间
			requestConfig = RequestConfig.custom().setSocketTimeout(this.SOCKET_TIMEOUT).setConnectTimeout(this.CONNECT_TIMEOUT).build();
			HttpResponse httpResponse = httpClient.execute(httpGet);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK) {
				logger.info("get->get data success! url:{}",httpGet.getURI());
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toString(httpEntity, "utf-8");
			} else {
				logger.error("get->get data error from {} , error code:{}",new Object[]{httpGet.getURI(),statusCode});
				return null;
			}
		} catch (Exception e) {
			logger.error("get->get data error from {} , error msg :{}",new Object[]{httpGet.getURI(),e});
			return null;
		} 
	}
	
	/**
	 * JSON字符串转为Map对象
	 * @param JSON JSON字符串
	 * @return 键值对Map对象
	 */
	private Map<String,String> fromJSON(String JSON) {
		try {
			Map<String,Object> result = JsonUtil.json2Map(JSON);
			Map<String,String> newResult = new HashMap<String,String>();
			for(Map.Entry<String, Object> entry : result.entrySet()) {
				newResult.put(entry.getKey(), entry.getValue().toString());
			}
			return newResult;
			//return result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,e->(String)e.getValue()));
		} catch (Exception e) {
			logger.error("fromJSON->{} from json error , error msg :{}",new Object[]{JSON,e});
			return null;
		}
	}

	/**
	 * 发送 SSL POST 请求（HTTPS），JSON形式
	 * @param apiUrl API接口URL
	 * @param json JSON对象
	 * @return
	 */
	public static String doPostSSL(String apiUrl, Object json, Map<String,String> headers) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(apiUrl);
        HttpResponse  response = null;
        String responseContent=null;
//		String httpStr = null;
		if(headers != null){
			for(Map.Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		try {
            //创建TrustManager
            X509TrustManager xtm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            //这个好像是HOST验证
            X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
                public void verify(String arg0, SSLSocket arg1) throws IOException {}
                public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
                public void verify(String arg0, X509Certificate arg1) throws SSLException {}
            };

            //TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
            SSLContext ctx = SSLContext.getInstance("TLS");
            //使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
            ctx.init(null, new TrustManager[] { xtm }, null);
            //创建SSLSocketFactory
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            socketFactory.setHostnameVerifier(hostnameVerifier);
            //通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));

			httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(json.toString(),"utf-8");//解决中文乱码问题
			stringEntity.setContentEncoding("utf-8");
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
           response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity(); // 获取响应实体
            if (entity != null) {
                responseContent = EntityUtils.toString(entity, "utf-8");
                return responseContent;
            }
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responseContent;
	}



    public static void main(String[] args) throws Exception {
	    Map<String, String> map = new TreeMap<>();
	    map.put("app", "uc工");
        map.put("appNameCn", "在大用户中心");
        map.put("Content-Type", "application/json");
		String  apiResult = HttpClientUtil.getInstance().doPostSSL("http://localhost:9999/eip/td/account/personal/register", "{\n" +
                        "\"cgtTelNo\":\"13421386303\",\n" +
                        "\"bankAccountNo\":\"6228480402564890011\",\n" +
                        "\"deviceType\":\"PC\",\n" +
                        "\"idCardNo\":\"362322198808244831\",\n" +
                        "\"idCardType\":\"PRC_ID\",\n" +
                        "\"realName\":\"三11111\",\n" +
                        "\"telNo\":\"13421386303\",\n" +
                        "\"userName\":\"tes1t12221\",\n" +
                        "\"userRole\":\"INVESTOR\",\n" +
                        "\"callbackUrl\":\"http://192.168.14.43:9999/td/callback\"\n" +
                        "}",
				map);
       System.out.println(apiResult);

	}
}