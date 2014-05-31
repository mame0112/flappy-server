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
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomRequestConversationServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomRequestConversationServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		// String identifier = req
		// .getParameter(LcomConst.SERVLET_CONTEXT_IDENTIFIER);
		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String friendUserId = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_ID);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);

		List<String> list = new ArrayList<String>();

		if (origin != null && userId != null && friendUserId != null
				&& apiLevel != null) {
			list.add(origin);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			List<LcomNewMessageData> datas = manager
					.getNewMessagesWithTargetUser(Integer.valueOf(userId),
							Integer.valueOf(friendUserId));
			if (datas != null && datas.size() != 0) {
				String result = DatastoreUtil.parseNewMessageList(datas);
				list.add(result);
			}
		}

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}
}
