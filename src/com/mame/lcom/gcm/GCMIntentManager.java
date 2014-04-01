package com.mame.lcom.gcm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.mame.lcom.constant.LcomConst;

public class GCMIntentManager {
	private final static Logger log = Logger.getLogger(GCMIntentManager.class
			.getName());

	private GCMIntentService mService = null;

	public GCMIntentManager() {
		mService = new GCMIntentService();
	}

	public void pushGCMNotification(int userId, String message, String regId) {
		log.log(Level.WARNING, "pushGCMNotification");
		String USER_ID = "TEST_USER";
		String uri = LcomConst.BASE_URL + "message_push?action=register"
				+ "&userId=" + USER_ID + "&regId=" + regId;
		log.log(Level.WARNING, "uri: " + uri);
		// Util.doGet(uri);

		HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
		String response = easyHttpClient.execDoGet();
		log.log(Level.WARNING, "response: " + response);
	}

	class HttpClientUtil {

		protected String url;
		protected HttpClient httpClient;
		protected List<NameValuePair> params = new ArrayList<NameValuePair>(1);

		public HttpClientUtil(String url) {
			this.httpClient = new DefaultHttpClient();
			this.url = url;
		}

		public void addParam(String key, String value) {
			this.params.add(new BasicNameValuePair(key, value));
		}

		public String execDoGet() {
			String responseString;
			try {
				// // post の場合
				// responseString = easyHttpClient.doPost();
				// get の場合
				responseString = doGet();
				log.log(Level.WARNING, "responseString: " + responseString);
				return responseString;
			} catch (UnsupportedEncodingException e) {
				log.log(Level.WARNING,
						"UnsupportedEncodingException: " + e.getMessage());
			} catch (ClientProtocolException e) {
				log.log(Level.WARNING,
						"ClientProtocolException: " + e.getMessage());
			} catch (IOException e) {
				log.log(Level.WARNING, "IOException: " + e.getMessage());
			}
			return null;
		}

		private String doGet() throws ClientProtocolException, IOException {
			// String queries = URLEncodedUtils.format(this.params, "UTF-8");
			// HttpGet httpGet = new HttpGet(this.url + "?" + queries);
			HttpGet httpGet = new HttpGet(this.url);

			return this.doHttpRequest(httpGet);
		}

		private String doPost() throws UnsupportedEncodingException,
				ClientProtocolException, IOException {
			UrlEncodedFormEntity entry = new UrlEncodedFormEntity(this.params);
			HttpPost httpPost = new HttpPost(this.url);
			httpPost.setEntity(entry);
			return this.doHttpRequest(httpPost);
		}

		protected String doHttpRequest(HttpUriRequest request)
				throws ClientProtocolException, IOException {
			String responseText = null;
			HttpResponse response = this.httpClient.execute(request);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			response.getEntity().writeTo(byteArrayOutputStream);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				responseText = byteArrayOutputStream.toString();
			}
			byteArrayOutputStream.close();

			return responseText;
		}
	}

}
