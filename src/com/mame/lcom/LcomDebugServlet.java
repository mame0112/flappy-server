package com.mame.lcom;

import java.io.IOException;
import java.text.ParseException;
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
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.TimeUtil;

public class LcomDebugServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomDebugServlet.class
			.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.WARNING, "doPost:" + TimeUtil.calcResponse());
		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String requestCode = req.getParameter("requestCode");
		String numOfUser = req.getParameter(LcomConst.SERVLET_TOTAL_USER_NUM);

		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String targetUserId = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_ID);
		String targetUserName = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_NAME);
		String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);
		String date = req.getParameter(LcomConst.SERVLET_MESSAGE_DATE);

		log.log(Level.WARNING, "origin:" + origin);
		log.log(Level.WARNING, "userId:" + userId);
		log.log(Level.WARNING, "userName:" + userName);
		log.log(Level.WARNING, "targetUserId:" + targetUserId);
		log.log(Level.WARNING, "targetUserName:" + targetUserName);
		log.log(Level.WARNING, "message:" + message);
		log.log(Level.WARNING, "date:" + date);

		log.log(Level.WARNING, "requestCode:" + requestCode);
		log.log(Level.WARNING, "numOfUser:" + numOfUser);

		List<String> list = new ArrayList<String>();

		list.add(origin);

		LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

		// If we want to add conversation data
		if (origin.equals("DEBUG_SEND_AND_ADD_DATA")) {
//			long parsedDate = 0L;
//			try {
//				parsedDate = TimeUtil.getDateInDateFormat(date);
//			} catch (ParseException e) {
//				log.log(Level.WARNING, "ParseException: " + e.getMessage());
//				// result = LcomConst.SEND_MESSAGE_DATE_CANNOT_BE_PARSED;
//			}

			manager.addNewMessageInfo(Integer.valueOf(userId),
					Integer.valueOf(targetUserId), userName, targetUserName,
					message, Long.valueOf(date));

			list.add(userId);
			list.add(userName);
			list.add(targetUserId);
			list.add(targetUserName);
			list.add(message);
			list.add(date);
		} else if (origin.equals("ALL_USER_DATA")) {
			// Other case
			manager.debugModifyNumOfUser(Integer.valueOf(numOfUser));
			// list.add(requestCode);

		}

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
