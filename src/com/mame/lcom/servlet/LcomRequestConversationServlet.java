package com.mame.lcom.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomRequestConversationServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomRequestConversationServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String userId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_ID), secretKey);
		String friendUserId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_TARGET_USER_ID), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		List<String> list = new ArrayList<String>();

		if (origin != null && userId != null && friendUserId != null
				&& apiLevel != null) {
			list.add(origin);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			long currentTime = TimeUtil.getCurrentDate();
			List<LcomNewMessageData> datas = manager
					.getNewMessagesWithTargetUser(Integer.valueOf(userId),
							Integer.valueOf(friendUserId), currentTime);
			if (datas != null && datas.size() != 0) {
				String result = DatastoreUtil.parseNewMessageList(datas);
				list.add(result);
			}
		}

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}
}
