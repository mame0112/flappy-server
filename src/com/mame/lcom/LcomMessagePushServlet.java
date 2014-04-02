package com.mame.lcom;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class LcomMessagePushServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomMessagePushServlet.class.getName());

	/**
	 * https://code.google.com/apis/console/ で生成したAPIキー。
	 */
	private static final String API_KEY = "AIzaSyBkrzyfBwaHQgjVphRiUNHusjEOjPQdOr4";
	private static final int RETRY_COUNT = 5;

	/**
	 * ユーザIDからRegistrationIdを引くテーブル。 -本来はストレージに保存すべき情報。 -key=ユーザID: サービスの管理するＩＤ。
	 * -value=RegistrationId: AndroidがGCMから取得した端末ＩＤ。
	 */
	static Map<String, String> deviceMap = new HashMap<String, String>();

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		log.log(Level.WARNING, "doGet");
		log.log(Level.WARNING, "req.getQueryString(): " + req.getQueryString());

		String action = req.getParameter("action");
		String registrationId = req.getParameter("regId");
		String userId = req.getParameter("userId");
		String msg = req.getParameter("msg");

		log.log(Level.WARNING, "action: " + action);
		log.log(Level.WARNING, "registrationId: " + registrationId);

		if ("register".equals(action)) {
			// 端末登録、Androidから呼ばれる。
			deviceMap.put(userId, registrationId);
			log.log(Level.WARNING, "register");

		} else if ("unregister".equals(action)) {
			// 端末登録解除、Androidから呼ばれる。
			deviceMap.remove(userId);
			log.log(Level.WARNING, "unregister");

		} else if ("send".equals(action)) {
			// メッセージ送信。任意の送信アプリから呼ばれる。

			log.log(Level.WARNING, "send");

			// registrationId = deviceMap.get(userId);
			Sender sender = new Sender(API_KEY);
			Message message = new Message.Builder().addData("msg", msg).build();

			log.log(Level.WARNING, "message: " + message);
			log.log(Level.WARNING, "registrationId: " + registrationId);

			Result result = sender.send(message, registrationId, RETRY_COUNT);
			// Result result = sender
			// .send(message,
			// "APA91bEXOz2NPGB7zzZkQMgBiR8AyGvAa1gUE-F-cX5IMu9_s_KDMUI6ikvCxokbEzcHTQbQAC1EfoEKysh0NJDggulwQz18jRSkqmJV9ASIX4cdnI2RBpGSR6qNe9W0baZL9lBC1XPev1Gg_t06SKnb7aQ4doGgYA",
			// RETRY_COUNT);

			res.setContentType("text/plain");
			res.getWriter().println("Result=" + result);
		} else if ("sendAll".equals(action)) {
			// TODO: 省略。googleのサンプル参照。

		} else {
			res.setStatus(500);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		log.log(Level.WARNING, "doPost");
		doGet(req, res);
	}

}
