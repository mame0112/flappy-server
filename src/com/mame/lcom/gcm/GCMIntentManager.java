package com.mame.lcom.gcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.mame.lcom.constant.LcomConst;

public class GCMIntentManager {
	private final static Logger log = Logger.getLogger(GCMIntentManager.class
			.getName());

	private GCMIntentService mService = null;

	private String mUserId = null;

	private String mMessage = null;

	private String mRegId = null;

	private String action = "send";

	public GCMIntentManager() {
		mService = new GCMIntentService();
	}

	public void pushGCMNotification2(HttpServletResponse res, int userId,
			String msg, String regId) {

		log.log(Level.WARNING, "pushGCMNotification2");

		String API_KEY = "AIzaSyBkrzyfBwaHQgjVphRiUNHusjEOjPQdOr4";
		int RETRY_COUNT = 5;

		Sender sender = new Sender(API_KEY);
		Message message = new Message.Builder().addData("msg", msg).build();

		log.log(Level.WARNING, "message: " + message);
		log.log(Level.WARNING, "registrationId: " + regId);

		Result result = null;
		try {
			result = sender.send(message, regId, RETRY_COUNT);
		} catch (IOException e) {
			log.log(Level.WARNING, "IOException: " + e.getMessage());
		}
		// Result result = sender
		// .send(message,
		// "APA91bEXOz2NPGB7zzZkQMgBiR8AyGvAa1gUE-F-cX5IMu9_s_KDMUI6ikvCxokbEzcHTQbQAC1EfoEKysh0NJDggulwQz18jRSkqmJV9ASIX4cdnI2RBpGSR6qNe9W0baZL9lBC1XPev1Gg_t06SKnb7aQ4doGgYA",
		// RETRY_COUNT);

		res.setContentType("text/plain");
		try {
			res.getWriter().println("Result=" + result);
		} catch (IOException e) {
			log.log(Level.WARNING, "IOException: " + e.getMessage());
		}
	}

	public void pushGCMNotification(int userId, String message, String regId) {
		log.log(Level.WARNING, "pushGCMNotification");
		mUserId = "TEST_USER";
		mMessage = message;
		mRegId = regId;

		// String uri = LcomConst.BASE_URL + "message_push?action=send"
		// + "&userId=" + USER_ID + "&msg=" + message + "&regId=" + regId;

		String uri = LcomConst.BASE_URL + "message_push";
		log.log(Level.WARNING, "uri: " + uri);
		// Util.doGet(uri);

		HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
		String response = easyHttpClient.execDoGet();
		log.log(Level.WARNING, "response: " + response);
	}

	class HttpClientUtil {

		protected String url;
		// protected HttpClient httpClient;
		protected List<NameValuePair> params = new ArrayList<NameValuePair>(1);

		public HttpClientUtil(String url) {
			// this.httpClient = new DefaultHttpClient();
			this.url = url;
		}

		public void addParam(String key, String value) {
			this.params.add(new BasicNameValuePair(key, value));
		}

		public String execDoGet() {
			String responseString = null;

			try {
				URL url = new URL(this.url);
				log.log(Level.WARNING, "url: " + this.url);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");

				OutputStreamWriter writer = new OutputStreamWriter(
						connection.getOutputStream());

				String parameterString = new String("action=send＆userId="
						+ mUserId + "&msg=" + mMessage + "&regId=" + mRegId);
				writer.write(parameterString);
				// writer.print(parameterString);

				// writer.write("action=send");
				// writer.write("userId=" + mUserId);
				// writer.write("msg=" + mMessage);
				// writer.write("regId=" + mRegId);
				writer.close();

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					log.log(Level.WARNING, "OK");
					InputStream input = connection.getInputStream();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(input, "UTF-8"));
					String line;
					StringBuilder response = new StringBuilder();
					while ((line = reader.readLine()) != null) {
						// ...
						response.append(line);
					}
					reader.close();
					log.log(Level.WARNING, "response: " + new String(response));
					responseString = new String(response);
				} else {
					log.log(Level.WARNING,
							"NOK: " + connection.getResponseCode());
					// Server returned HTTP error code.
				}
			} catch (MalformedURLException e) {
				log.log(Level.WARNING,
						"MalformedURLException: " + e.getMessage());
			} catch (IOException e) {
				log.log(Level.WARNING, "IOExcepton: " + e.getMessage());
			}

			// URL url = new URL(this.url);
			// HttpURLConnection connection = (HttpURLConnection) url
			// .openConnection();
			// connection.setDoOutput(true);
			// connection.setUseCashes(false);
			// connection.setRequestMethod("GET");
			// BufferReader bufferReader = new BufferReader(new
			// InputStreamReader(
			// connection.getInputStream(), "JISAutoDetect"));
			// String httpSource = new String();
			// String str;
			// while (null != (str = bufferReader.readLine())) {
			// httpSource = httpSource + str;
			// }
			// bufferReader.close();
			// connection.disconnect();

			// try {
			// // post の場合
			// responseString = easyHttpClient.doPost();
			// get の場合
			// responseString = doGet();
			// log.log(Level.WARNING, "responseString: " + responseString);
			// return responseString;
			// } catch (UnsupportedEncodingException e) {
			// log.log(Level.WARNING,
			// "UnsupportedEncodingException: " + e.getMessage());
			// } catch (ClientProtocolException e) {
			// log.log(Level.WARNING,
			// "ClientProtocolException: " + e.getMessage());
			// } catch (IOException e) {
			// log.log(Level.WARNING, "IOException: " + e.getMessage());
			// }
			return responseString;
		}
		// private String doGet() throws ClientProtocolException, IOException {
		// // String queries = URLEncodedUtils.format(this.params, "UTF-8");
		// // HttpGet httpGet = new HttpGet(this.url + "?" + queries);
		// HttpGet httpGet = new HttpGet(this.url);
		//
		// return this.doHttpRequest(httpGet);
		// }

		// private String doPost() throws UnsupportedEncodingException,
		// ClientProtocolException, IOException {
		// UrlEncodedFormEntity entry = new UrlEncodedFormEntity(this.params);
		// HttpPost httpPost = new HttpPost(this.url);
		// httpPost.setEntity(entry);
		// return this.doHttpRequest(httpPost);
		// }

		// protected String doHttpRequest(HttpUriRequest request)
		// throws ClientProtocolException, IOException {
		// String responseText = null;
		// HttpResponse response = this.httpClient.execute(request);
		//
		// ByteArrayOutputStream byteArrayOutputStream = new
		// ByteArrayOutputStream();
		// response.getEntity().writeTo(byteArrayOutputStream);
		//
		// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		// responseText = byteArrayOutputStream.toString();
		// }
		// byteArrayOutputStream.close();
		//
		// return responseText;
		// }
	}

}
