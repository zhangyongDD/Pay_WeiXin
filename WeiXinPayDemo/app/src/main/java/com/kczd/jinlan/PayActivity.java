package com.kczd.jinlan;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PayActivity extends Activity {

	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID,false);
		//api = WXAPIFactory.createWXAPI(this, "wxb4ba3c02aa476ea1");
		// 将该app注册到微信
		api.registerApp(Constants.APP_ID);
		Button appayBtn = (Button) findViewById(R.id.appay_btn);
		appayBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "http://wxpay.weixin.qq.com/pub_v2/app/app_pay.php?plat=android";
				final Button payBtn = (Button) findViewById(R.id.appay_btn);
				Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();

				new Thread(new Runnable() {
					@Override
					public void run() {
						payHandle();
					}
				}).start();
//					 HttpUtils.doGetAsyn(url, new HttpUtils.CallBack() {
//						 @Override
//						 public void onRequestComplete(byte[] buf) {
//							 if (buf != null && buf.length > 0) {
//								 String content = new String(buf);
//								 Log.e("get server pay params:", content);
//								 JSONObject json;
//								 try {
//									 json = new JSONObject(content);
//									 if (null != json && !json.has("retcode")) {
//										 PayReq req = new PayReq();
//										 // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
//										 req.appId = json.getString("appid");
//										 req.partnerId = json.getString("partnerid");
//										 req.prepayId = json.getString("prepayid");
//										 req.nonceStr = json.getString("noncestr");
//										 req.timeStamp = json.getString("timestamp");
//										 req.packageValue = json.getString("package");
//										 req.sign = json.getString("sign");
//										 req.extData = "app data"; // optional
//										 Toast.makeText(PayActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
//										 // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//										 api.sendReq(req);
//									 } else {
//										 Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
//										 Toast.makeText(PayActivity.this, "返回错误" + json.getString("retmsg"), Toast.LENGTH_SHORT).show();
//									 }
//								 } catch (JSONException e) {
//									 e.printStackTrace();
//								 }
//							 } else {
//								 Log.d("PAY_GET", "服务器请求错误");
//								 Toast.makeText(PayActivity.this, "服务器请求错误", Toast.LENGTH_SHORT).show();
//							 }
//
//						 }
//
//					 });
			}
		});
	}

	/**
	 * 登录Net
	 */
	public void payHandle() {
		//创建okHttpClient对象
		OkHttpClient client = new OkHttpClient();
		RequestBody requestBody=new FormBody.Builder()
				.add("order_sn","632")
				.add("order_amount","45.00")
				.build();
		Request request=new Request.Builder()
				.url("http://www.weiantang.com/index.php/api/api/weixinPay")
				.post(requestBody)
				.build();
		try {
			Response response=client.newCall(request).execute();
			String strResult=response.body().string();
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("ret_json", strResult);
			msg.setData(data);
			handler.sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("ret_json");
			val = val.substring(val.indexOf("{"),val.lastIndexOf("}")+1);
			Log.e("zhangy",val);
			JSONObject jsonResult;
			try {
				jsonResult = new JSONObject(val);
				String code=jsonResult.getString("code");
				if(code.equals("200")){
					PayReq req = new PayReq();
					JSONObject jb=jsonResult.getJSONObject("data");
					req.appId=jb.getString("appid");//应用ID
					req.partnerId=jb.getString("partnerid");//商户号
					req.nonceStr=jb.getString("noncestr");//随机字符串
					req.prepayId=jb.getString("prepayid");//预支付交易回话ID
					req.sign=jb.getString("sign");//签名
                    req.packageValue=jb.getString("package");
					req.timeStamp =jb.getString("timestamp");
					api.sendReq(req);
				}else {
					Log.d("PAY_GET", "返回错误" + jsonResult.getString("msg"));
					Toast.makeText(PayActivity.this, "返回错误" + jsonResult.getString("msg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
}
