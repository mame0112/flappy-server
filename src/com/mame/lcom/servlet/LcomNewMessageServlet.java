package com.mame.lcom.servlet;

import java.io.IOException;

import com.mame.lcom.util.DbgUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomNewMessageServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomNewMessageServlet.class.getName());

	private final static String TAG = "LcomNewMessageServlet";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DbgUtil.showLog(TAG, "doPost:" + TimeUtil.calcResponse());

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String userId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_ID), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		if (userId != null && apiLevel != null) {
			DbgUtil.showLog(TAG, "userId:" + userId);
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			long currentTime = TimeUtil.getCurrentDate();

			List<LcomFriendshipData> friendListData = null;

			friendListData = manager.getNewMessageData(Long.valueOf(userId),
					currentTime);

			if (friendListData != null && friendListData.size() != 0) {
				String result = DatastoreUtil.parseFriendListData(
						Integer.valueOf(userId), friendListData);

				DbgUtil.showLog(TAG, "result::" + result);
				list.add(result);
			} else {
				DbgUtil.showLog(TAG, "friendListData is null or 0");
			}
		}

		DbgUtil.showLog(TAG, "end:" + TimeUtil.calcResponse());
		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		// resp.setHeader("Cache-Control", "public, max-age=86400");
		resp.getWriter().write(json);

	}
}
