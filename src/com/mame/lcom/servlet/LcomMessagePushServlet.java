package com.mame.lcom.servlet;

import java.io.IOException;

import com.mame.lcom.util.DbgUtil;

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
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.CipherUtil;

public class LcomMessagePushServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomMessagePushServlet.class.getName());

	private final static String TAG = "LcomMessagePushServlet";

	private static final String API_KEY = "AIzaSyBkrzyfBwaHQgjVphRiUNHusjEOjPQdOr4";
	private static final int RETRY_COUNT = 5;

	static Map<String, String> deviceMap = new HashMap<String, String>();

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {

		DbgUtil.showLog(TAG, "doGet");
		DbgUtil.showLog(TAG, "req.getQueryString(): " + req.getQueryString());

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String action = CipherUtil.decrypt(req.getParameter("action"),
				secretKey);
		String registrationId = CipherUtil.decrypt(req.getParameter("regId"),
				secretKey);
		String userId = CipherUtil.decrypt(req.getParameter("userId"),
				secretKey);
		String msg = CipherUtil.decrypt(req.getParameter("msg"), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		DbgUtil.showLog(TAG, "action: " + action);
		DbgUtil.showLog(TAG, "registrationId: " + registrationId);

		if ("register".equals(action)) {
			// 端末登録、Androidから呼ばれる。
			deviceMap.put(userId, registrationId);
			DbgUtil.showLog(TAG, "register");

		} else if ("unregister".equals(action)) {
			// 端末登録解除、Androidから呼ばれる。
			deviceMap.remove(userId);
			DbgUtil.showLog(TAG, "unregister");

		} else if ("send".equals(action)) {
			// メッセージ送信。任意の送信アプリから呼ばれる。

			DbgUtil.showLog(TAG, "send");

			// registrationId = deviceMap.get(userId);
			Sender sender = new Sender(API_KEY);
			Message message = new Message.Builder().addData("msg", msg).build();

			DbgUtil.showLog(TAG, "message: " + message);
			DbgUtil.showLog(TAG, "registrationId: " + registrationId);

			Result result = sender.send(message, registrationId, RETRY_COUNT);
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
		DbgUtil.showLog(TAG, "doPost");
		doGet(req, res);
	}

}
