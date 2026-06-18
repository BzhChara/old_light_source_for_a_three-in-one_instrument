package com.whswzz.prfluroanalyzer.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import top.jemen.utils.LogUtil;

public class HttpUtil {

	public static String postJson(String urlPath, String Json) {
		String result = "";
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
				httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
				conn = httpsConn;
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}

			conn.setRequestMethod("POST");
			conn.setConnectTimeout(5000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			// conn.setRequestProperty("accept","*/*");
			conn.setRequestProperty("accept", "application/json");
			if (Json != null) {
				byte[] writebytes = Json.getBytes();
				conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
				OutputStream outwritestream = conn.getOutputStream();
				outwritestream.write(writebytes);
				outwritestream.flush();
				outwritestream.close();
			}
			// if (conn.getResponseCode() == 200) {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			result = reader.readLine();
			// }
		} catch (Exception e) {
			e.printStackTrace();
			result+=e.getMessage();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		LogUtil.d2("postJson result: " + result);
		return result;
	}
	public static String postForm(String urlPath, String ...kvs) {
		String result = "";
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
				httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
				conn = httpsConn;
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			// conn.setRequestProperty("accept","*/*");
			conn.setRequestProperty("accept", "application/json");
			if (kvs != null) {
				StringBuilder sb=new StringBuilder();
				for(int i=0;i+1<kvs.length;i+=2) {
					sb.append(URLEncoder.encode(kvs[i],"utf-8")).append("=").append(URLEncoder.encode(kvs[i+1],"utf-8"));
					if(i+1<kvs.length-1) {
						sb.append("&");
					}
				}
				byte[] writebytes = sb.toString().getBytes();
				conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
				OutputStream outwritestream = conn.getOutputStream();
				outwritestream.write(writebytes);
				outwritestream.flush();
				outwritestream.close();
			}
			// if (conn.getResponseCode() == 200) {
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			result = reader.readLine();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 当前线程请求并返回响应值
	 * @param urlPath
	 * @param kvs
	 * @return
	 */
	public static String get(String urlPath,  String...kvs){
		if(null==urlPath||"".equals(urlPath)){
			return "no url";
		}
		StringBuilder sb=new StringBuilder(urlPath);
		if(kvs!=null&&kvs.length>=2){
			sb.append("?");
			for(int i=0;i+1<kvs.length;i+=2){
				sb.append(kvs[i]).append("=").append(kvs[i+1]);
				if(i+1!=kvs.length-1){
					sb.append("&");
				}
			}
		}
		StringBuilder result = new StringBuilder();
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		BufferedReader in = null;
		try {
			URL url = new URL(urlPath);
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
				httpsConn.setHostnameVerifier(DO_NOT_VERIFY);
				conn = httpsConn;
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod("GET");
			//Get请求不需要DoOutPut
			conn.setDoOutput(false);
			conn.setDoInput(true);
			//设置连接超时时间和读取超时时间
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//连接服务器
			conn.connect();
			// 取得输入流，并使用Reader读取
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			return result.toString();

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try{
				if(in!=null){
					in.close();
				}
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		return "null";
	}





	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String arg0, SSLSession arg1) {
			return true;
		}
	};

	public static void trustAllHosts() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

			}

			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

			}
		}

		};

		try {
			// SSLContext sc = SSLContext.getInstance("TLS");
			// sc.init(null, trustAllCerts, new SecureRandom());

			// HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultSSLSocketFactory(new SSL(new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
