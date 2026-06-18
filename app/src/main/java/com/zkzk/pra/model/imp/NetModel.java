package com.zkzk.pra.model.imp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.AbstractHttpEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.whswzz.prfluroanalyzer.app.MyApp;
import com.whswzz.prfluroanalyzer.app.Target;
import com.whswzz.prfluroanalyzer.consts.Consts;
import com.whswzz.prfluroanalyzer.entity.IData;
import com.whswzz.prfluroanalyzer.fluoro.entity.FluData;
import com.whswzz.prfluroanalyzer.param.Params;
import com.zkzk.pra.R;
import com.zkzk.pra.model.IModel;
import com.zkzk.pra.utils.ExceptionHandler;
import com.zkzk.pra.utils.Tools;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import top.jemen.interfaces.ICallback;
import top.jemen.utils.LogUtil;

public class NetModel implements IModel {
	private Gson gson;
	private RequestQueue queue;
	private static NetModel netModel;
	private Resources res = MyApp.getApp().getResources();

	private NetModel() {
		queue = MyApp.getApp().getQueue();
		gson = new Gson();

	}
	// public NetModel(Context context) { 
	// queue = MyApplication.getApp().getQueue();
	// gson = new Gson();
	// }
	public synchronized static NetModel getModel() {
		if (netModel == null) {
			netModel = new NetModel();
		}
		return netModel;
	}

	@Override
	/**
	 */
	public void loadContent(String url, final ICallback callback) {
		StringRequest request = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				callback.onSuccess(response);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub

			}
		});
		queue.add(request);

	}

	@Override
	public void loadString(String url, final ICallback callback) {
		StringRequest request = new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				callback.onSuccess(response);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				callback.onFailed("error," + error.getMessage());
			}
		});
		queue.add(request);
	}

/*
	public void upload(final List<DataNet> datas, final ICallback callback) {
		String url = MyApplication.getApp().getPref().getString(Consts.KEY_URL, "");
		new AsyncTask<String, String, String>() {
			final String SUCCESS = "success";
			@Override
			protected String doInBackground(String... params) {
				try {
					URL url = new URL(params[0]);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(15000);
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);
		            conn.setDoInput(true);
		            conn.setUseCaches(false);
		            conn.setRequestProperty("Connection", "Keep-Alive");
		            conn.setRequestProperty("Charset", "UTF-8");
		            conn.setRequestProperty("accept","application/json");
		            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		            String content=URLEncoder.encode("data="+gson.toJson(datas),"UTF-8");//服务端再解码
//		            String content="data="+gson.toJson(datas);
//					byte[] bs=content.toString().getBytes("UTF-8");
//					conn.setRequestProperty("Content-Length", String.valueOf(bs.length));
					OutputStream os = conn.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeUTF(content);
					
//					oos.write(bs);
					
					//每条数据都应该以键值对方式发送，多条时以&分割。
//					 oos.writeUTF("\nwrite over\n");
//					 oos.writeObject(datas);
					oos.flush();
					LogUtil.d("oos.flush执行完毕");
					// 写完之后需要进行读取操作服务端才能正确获取数据。
					BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					String line;
					StringBuilder sb = new StringBuilder();
					LogUtil.d("准备读取返回值");
					while ((line = bf.readLine()) != null) {
						sb.append(line).append("\n");
					}
					LogUtil.d("返回：" + sb.toString());
					bf.close();
					oos.close();
					// 可让服务端在收到数据后进行回复，这里用inputStream进行读取。
					conn.disconnect();
					LogUtil.d("上传代码执行完毕！！！");
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return res.getString(R.string.wrong_url_path);
				} catch (IOException e) {
					ExceptionHandler.handleException(e);
					return res.getString(R.string.can_not_connect_to_server);
				} catch (Exception e) {
					ExceptionHandler.handleException(e);
					return res.getString(R.string.upload_failed);
				}
				return SUCCESS;
			}

			protected void onPostExecute(String result) {
				if (SUCCESS.equals(result)) {
					callback.onSuccess(res.getString(R.string.data_upload_succed));
				} else {
					callback.onFailed(result);
				}
			};

		}.execute(url);
	}
	
	
	/**
	 * 优先使用此
	 * @param datas
	 * @param callback
	 */
	
//	public <T> void uploadClient(final List<T> datas, final ICallback callback) {
////		String url = MyApp.getApp().getPref().getString(Consts.KEY_URL, "http://58.49.112.42:8181/Server/data");
////		String url = MyApp.getApp().getPref().getString(Consts.KEY_URL, "http://58.49.112.42:8282/hm/data");
//		String url=Params.UPLOAD_URL;
//		LogUtil.d("url="+url);
//		new AsyncTask<String, String, String>() {
//			final String SUCCESS = "success";
//			@Override
//			protected String doInBackground(String...url) {
//					try {
//						HttpClient client=new DefaultHttpClient();
//						HttpPost post=new HttpPost(url[0]);
//						post.setHeader("Accept","aplication/json");
//						post.addHeader("Content-Type","application/x-www-form-urlencoded");
////						post.addHeader("Content-Type","application/json;charset=UTF-8");
//						List<NameValuePair> params=new ArrayList<NameValuePair>();
//						params.add(new BasicNameValuePair("device", "PRAIO"));
//						params.add(new BasicNameValuePair("username", "USERX"));
//						params.add(new BasicNameValuePair("password", "654321"));
////						params.add(new BasicNameValuePair("deviceId",android.os.Build.SERIAL));
//						params.add(new BasicNameValuePair("deviceId",Tools.getJemenId()));
//
//						String version = Tools.getCurrentVersion(null);
//						params.add(new BasicNameValuePair("version",version));
//						params.add(new BasicNameValuePair("data",gson.toJson(datas)));
//						UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
//						post.setEntity(entity);
//						HttpResponse response=client.execute(post);
//						if(response.getStatusLine().getStatusCode()==200) {//请求和响应成功
//							HttpEntity rEntity=response.getEntity();
//							String strResponse=EntityUtils.toString(rEntity,"utf-8");
//							LogUtil.d("sreponse="+strResponse);
//						}
//					} catch (UnsupportedEncodingException e) {
//						ExceptionHandler.handleException(e);
//						return res.getString(R.string.data_upload_failed);
//					} catch (ClientProtocolException e) {
//						ExceptionHandler.handleException(e);
//						return res.getString(R.string.data_upload_failed);
//					} catch (IOException e) {
//						ExceptionHandler.handleException(e);
//						return res.getString(R.string.data_upload_failed);
//					}catch (Exception e) {
//						ExceptionHandler.handleException(e);
//						return res.getString(R.string.data_upload_failed);
//					}
//				return SUCCESS;
//			}
//
//			protected void onPostExecute(String result) {
//				if (SUCCESS.equals(result)) {
//					callback.onSuccess(res.getString(R.string.data_upload_succed));
//				} else {
//					callback.onFailed(result);
//				}
//			};
//
//		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
//	}
	
//
//	public static void setIp(String ip, String mask, final ICallback callback) {
//		new AsyncTask<String, String, String>() {
//			@Override
//			protected String doInBackground(String... params) {
//				return null;
//			}
//			protected void onPostExecute(String result) {
//			};
//		}.execute(ip, mask);
//	}
//	/**
//	 * 默认采用
//	 * @param datas
//	 * @param callback
//	 */
//	public void upload2(final List<DataNet> datas, final ICallback callback) {
//		String url = MyApplication.getApp().getPref().getString(Consts.KEY_URL, 
//				"http://120.77.243.254:7979/index.php/Api/TestApi/testzrd");
////		url="http://192.168.1.165/testapi/index.php/Api/TestApi/testzjs?XDEBUG_SESSION_START=ECLIPSE_DBGP";
//		//内部测试用URL
//		new AsyncTask<String, String, String>() {
//			final String SUCCESS = "success";
//			@Override
//			protected String doInBackground(String... params) {
//				try {
//					// 1. 获得一个相当于浏览器对象HttpClient，使用这个接口的实现类来创建对象，DefaultHttpClient
//					HttpClient hc = new DefaultHttpClient();
//					// DoPost方式请求的时候设置请求，关键是路径
//					HttpPost request = new HttpPost(params[0]);
//					request.setHeader("Accept","aplication/json");
//					request.addHeader("Content-Type","application/x-www-form-urlencoded");
//					// 2. 为请求设置请求参数，也即是将要上传到web服务器上的参数
//					List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//					parameters .add(new BasicNameValuePair("username", "test2"));
//					parameters .add(new BasicNameValuePair("password", "123456"));
//					NameValuePair nameValuePairs = new BasicNameValuePair("data", gson.toJson(datas));
//					parameters.add(nameValuePairs);
//					// 请求实体HttpEntity也是一个接口，我们用它的实现类UrlEncodedFormEntity来创建对象，注意后面一个String类型的参数是用来指定编码的
//					HttpEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
//					request.setEntity(entity);
//					// 3. 执行请求
//					HttpResponse response = hc.execute(request);
//					// 4. 通过返回码来判断请求成功与否
//					if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
//						HttpEntity enttR=response.getEntity();
//						LogUtil.d("应答："+EntityUtils.toString(enttR,"UTF-8"));
//						return SUCCESS;
//					} else {
//						return "";
//					}
//				} catch (UnsupportedEncodingException e) {
//					ExceptionHandler.handleException(e);
//					return res.getString(R.string.data_upload_failed);
//				} catch (ClientProtocolException e) {
//					ExceptionHandler.handleException(e);
//					return res.getString(R.string.data_upload_failed);
//				} catch (NotFoundException e) {
//					ExceptionHandler.handleException(e);
//					return res.getString(R.string.wrong_url_path);
//				} catch (IOException e) {
//					ExceptionHandler.handleException(e);
//					return res.getString(R.string.data_upload_failed);
//				}catch (Exception e) {
//					ExceptionHandler.handleException(e);
//					return res.getString(R.string.data_upload_failed);
//				}
//			}
//
//			protected void onPostExecute(String result) {
//				if (SUCCESS.equals(result)) {
//					callback.onSuccess(res.getString(R.string.data_upload_succed));
//				} else {
//					callback.onFailed(result);
//				}
//			};
//
//		}.execute(url);
//	}

	
	public void okHttpTest() {

	
	}
	/**
	 */
//	public void uploadMsg(final String msg) {
//		String url = "http://58.49.112.42:8181/Server/error";
//		LogUtil.d("url=" + url);
//		new AsyncTask<String, String, String>() {
//			final String SUCCESS = "success";
//
//			@Override
//			protected String doInBackground(String... url) {
//				try {
//					HttpClient client = new DefaultHttpClient();
//					HttpPost post = new HttpPost(url[0]);
//					post.setHeader("Accept", "aplication/json");
//					post.addHeader("Content-Type", "application/x-www-form-urlencoded");
//					List<NameValuePair> params = new ArrayList<NameValuePair>();
//					params.add(new BasicNameValuePair("device", Target.DEVICE));
//					params.add(new BasicNameValuePair("deviceId",Tools.getJemenId()));
//					String version = Tools.getCurrentVersion(null);
//					params.add(new BasicNameValuePair("version", version));
//					params.add(new BasicNameValuePair("msg", msg));
//					params.add(new BasicNameValuePair("hardware", android.os.Build.HARDWARE));
//					// String time=(String)
//					// DateFormat.format("yyyy-MM-dd,hh:mm",System.currentTimeMillis());
//					// params.add(new BasicNameValuePair("time",time));//时间直接采用服务器的当前时间。
//					UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
//					post.setEntity(entity);
//					HttpResponse response = client.execute(post);
//					if (response.getStatusLine().getStatusCode() == 200) {// 请求和响应成功
//						HttpEntity rEntity = response.getEntity();
//						String strResponse = EntityUtils.toString(rEntity, "utf-8");
//						LogUtil.d("sreponse=" + strResponse);
//					}
//				} catch (Exception e) {
//					ExceptionHandler.handleException(e);
//					return "上传失败";
//				}
//				return SUCCESS;
//			}
//		}.executeOnExecutor(jemenExecutor,url);
//	}
	
	  private  final ThreadFactory sThreadFactory = new ThreadFactory() {
	        private final AtomicInteger mCount = new AtomicInteger(1);

	        public Thread newThread(Runnable r) {
	            return new Thread(r, "jemen-AsyncTask #" + mCount.getAndIncrement());
	        }
	    };
	
	 /**
	  * 给不需要与界面交互的线程，提供一个专用的线程池。
	  */
	ThreadPoolExecutor jemenExecutor = new ThreadPoolExecutor(
           2, 4, 30, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(10),sThreadFactory);

//	public void uploadClient(FluData  data, ICallback callback) {
//		List<FluData> datas=new LinkedList<FluData>();
//		datas.add(data);
//		uploadClient(datas, callback);
//	}



}
