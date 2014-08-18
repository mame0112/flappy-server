package com.mame.lcom.servlet;

import java.io.IOException;
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
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomNewMessageServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomNewMessageServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);
		String isAllDataRequire = req
				.getParameter(LcomConst.SERVLET_ALL_DATA_REQUIRE);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		if (userId != null && apiLevel != null && isAllDataRequire != null) {
			log.log(Level.INFO, "userId:" + userId);
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			long currentTime = TimeUtil.getCurrentDate();

			boolean isAllData = Boolean.valueOf(isAllDataRequire);

			List<LcomFriendshipData> friendListData = null;

			if (isAllData) {
				friendListData = manager.getAllFriendshipData(Long
						.valueOf(userId));
			} else {
				friendListData = manager.getNewMessageData(
						Long.valueOf(userId), currentTime);
			}

			if (friendListData != null && friendListData.size() != 0) {
				String result = DatastoreUtil.parseFriendListData(
						Integer.valueOf(userId), friendListData);

				log.log(Level.INFO, "result::" + result);
				list.add(result);
			} else {
				log.log(Level.INFO, "friendListData is null or 0");
			}
		}

		log.log(Level.INFO, "end:" + TimeUtil.calcResponse());
		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		// resp.setHeader("Cache-Control", "public, max-age=86400");
		resp.getWriter().write(json);

	}
}
