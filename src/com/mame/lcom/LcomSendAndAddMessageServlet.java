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
import com.mame.lcom.gcm.GCMIntentManager;
import com.mame.lcom.util.TimeUtil;

public class LcomSendAndAddMessageServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomSendAndAddMessageServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String targetUserId = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_ID);
		String targetUserName = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_NAME);
		String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);
		String date = req.getParameter(LcomConst.SERVLET_MESSAGE_DATE);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		int result = LcomConst.SEND_MESSAGE_RESULT_OK;

		if (userId != null && userName != null && targetUserId != null
				&& targetUserName != null && message != null && date != null) {

			log.log(Level.WARNING, "message:" + message);

			// long parsedDate = 0L;
			// try {
			// parsedDate = TimeUtil.getDateInDateFormat(date);
			// } catch (ParseException e) {
			// log.log(Level.WARNING, "ParseException: " + e.getMessage());
			// result = LcomConst.SEND_MESSAGE_DATE_CANNOT_BE_PARSED;
			// }

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			manager.addNewMessageInfo(Integer.valueOf(userId),
					Integer.valueOf(targetUserId), userName, targetUserName,
					message, Long.valueOf(date));
			GCMIntentManager pushManager = new GCMIntentManager();
			String regId = "APA91bG2j4wg-64_3CgK7xu8zjgQXQHIoP5w_SK0lNrUXeaX8kJhwueplHkeFeZjWcT9XxSzTVYI1ekiJ4AnpkexHmEzeJHM1cr3q4mH78S9FhxT79UaHXm9EDXDien66M14xbP71b-WlV6hwimkLC0yuTKsNzzp5w";
			// pushManager.pushGCMNotification(Integer.valueOf(userId),
			// "test here!", regId);

			pushManager.pushGCMNotification2(resp, Integer.valueOf(userId),
					message, regId);
		} else {
			log.log(Level.WARNING, "Some of parameters are null");
			result = LcomConst.SEND_MESSAGE_UNKNOWN_ERROR;
		}

		list.add(String.valueOf(result));

		list.add(userId);
		list.add(userName);
		list.add(targetUserId);
		list.add(targetUserName);
		list.add(message);
		list.add(date);

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
