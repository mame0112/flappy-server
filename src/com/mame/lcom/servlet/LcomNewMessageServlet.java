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

		List<String> list = new ArrayList<String>();
		list.add(origin);

		if (userId != null && apiLevel != null) {
			log.log(Level.INFO, "userId:" + userId);
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			long currentTime = TimeUtil.getCurrentDate();

			List<LcomFriendshipData> friendListData = manager
					.getFriendListData(Integer.valueOf(userId), currentTime);

			if (friendListData != null && friendListData.size() != 0) {

				// If user name is not available, we use mail address instead.
				for (LcomFriendshipData data : friendListData) {
					log.log(Level.INFO, "currentTime:" + currentTime);
					log.log(Level.INFO, "data.getLastMessageExpireTime():"
							+ data.getLastMessageExpireTime());
					// Not to send already expire data
					if (currentTime < data.getLastMessageExpireTime()) {
						int firstUserId = data.getFirstUserId();

						// If first user is user himself (meaning friend is
						// second
						// user)
						if (firstUserId == Integer.valueOf(userId)) {
							String friendName = data.getSecondUserName();
							log.log(Level.INFO, "getSecondUserName: "
									+ friendName);
							if (friendName == null || friendName.equals("")
									|| friendName.equals(LcomConst.NULL)) {
								log.log(Level.INFO, "data.getSecondUserId():"
										+ data.getSecondUserId());
								LcomUserData friendData = manager
										.getUserData(data.getSecondUserId());
								data.setSecondUserName(friendData
										.getMailAddress());
							}
						} else {
							// If second user is user (meaning friend is first
							// user)
							String friendName = data.getFirstUserName();
							log.log(Level.INFO, "getFirstUserName: "
									+ friendName);
							if (friendName == null || friendName.equals("")
									|| friendName.equals(LcomConst.NULL)) {
								log.log(Level.INFO, "data.getFirstUserId():"
										+ data.getFirstUserId());
								LcomUserData friendData = manager
										.getUserData(data.getFirstUserId());
								data.setSecondUserName(friendData
										.getMailAddress());
							}
						}
					}
				}

				String result = DatastoreUtil.parseFriendListData(
						Integer.valueOf(userId), friendListData);

				log.log(Level.INFO, "result::" + result);
				list.add(result);
			}
		}

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
