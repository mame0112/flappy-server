package com.mame.lcom;

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
import com.mame.lcom.data.LcomNewMessageData;
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

		List<String> list = new ArrayList<String>();
		list.add(origin);

		if (userId != null) {
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			List<LcomNewMessageData> newMessages = manager
					.getNewMessages(Integer.valueOf(userId));

			if (newMessages != null && newMessages.size() != 0) {
				String result = DatastoreUtil.parseNewMessageList(newMessages);
				list.add(result);
			}
		}

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
