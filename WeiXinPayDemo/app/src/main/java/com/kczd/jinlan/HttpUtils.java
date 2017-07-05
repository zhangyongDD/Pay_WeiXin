package com.kczd.jinlan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Http请求的工具类
 * 
 * @author zhy
 * 
 */
public class HttpUtils{
	private static final int TIMEOUT_IN_MILLIONS = 5000;
	public interface CallBack{
		void onRequestComplete(byte[] arr);
	}


	/**
	 * 异步的Get请求
	 * 
	 * @param urlStr
	 * @param callBack
	 */
	public static void doGetAsyn(final String urlStr, final CallBack callBack){
		new Thread(){
			public void run(){
				try{
					byte []arr = doGet(urlStr);
					if (callBack != null){
						callBack.onRequestComplete(arr);
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * 异步的Post请求
	 * @param urlStr
	 * @param params
	 * @param callBack
	 * @throws Exception
	 */
	public static void doPostAsyn(final String urlStr, final String params,
			final CallBack callBack) throws Exception{
		new Thread(){
			public void run(){
				try{
					byte []arr = doPost(urlStr, params);
					if (callBack != null){
						callBack.onRequestComplete(arr);
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * Get请求，获得返回数据
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static byte[] doGet(String urlStr){
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		int len =0;
		byte[] buf = new byte[1024];
		try{
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200){
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				while ((len = is.read(buf)) != -1){
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toByteArray();
			} else{
				throw new RuntimeException(" responseCode is not 200 ... ");
			}
		} catch (Exception e){
			e.printStackTrace();
		}finally{
			try{
				if (is != null)
					is.close();
				if (baos != null)
					baos.close();
			} catch (IOException e){
				e.printStackTrace();
			}
			conn.disconnect();
		}
		return null ;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static byte[] doPost(String url, String param){
		PrintWriter out = null;
		InputStream in = null;
		//本地的输出流
		ByteArrayOutputStream os;
		HttpURLConnection conn=null;
		//每次读的字节数
		byte[] data =new byte[1024];
		//每次读到的字节数，一般是1024，如果到了最后一行就会少于1024，到了末尾就是 -1
		int len=0;
		try{
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			if (param != null && !param.trim().equals("")){
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			in = conn.getInputStream();
			in=conn.getInputStream();
			os=new ByteArrayOutputStream();
			while ((len=in.read(data))!=-1){
				os.write(data,0,len);
			}
			return os.toByteArray();
			//return new String(os.toByteArray());
		} catch (Exception e){
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally{
			try{
				if (out != null){
					out.close();
				}
				if (in != null){
					in.close();
				}
			} catch (IOException ex){
				ex.printStackTrace();
			}
			conn.disconnect();
		}
		return null;
	}
}
