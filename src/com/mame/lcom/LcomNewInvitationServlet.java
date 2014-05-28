package com.mame.lcom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.invitation.LcomMail;
import com.mame.lcom.util.TimeUtil;

/**
 * Servlet for return whetner the inputted mail address user is registerd or
 * not.
 * 
 */
public class LcomNewInvitationServlet extends HttpServlet {

	private final Logger log = Logger.getLogger(LcomNewInvitationServlet.class
			.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String mailAddress = req.getParameter(LcomConst.SERVLET_MAILADDRESS);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);
		// String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		int result = LcomConst.INVITATION_NEW_USER_RESULT_OK;

		// int result = LcomConst.LOGIN_RESULT_OK;
		// int userId = LcomConst.NO_USER;

		String existingUsername = null;
		int existingUserId = LcomConst.NO_USER;

		if (origin != null && userId != null && userName != null
				&& mailAddress != null && apiLevel != null) {
			log.log(Level.INFO, "userId:" + userId);
			log.log(Level.INFO, "userName:" + userName);
			log.log(Level.INFO, "mailAddress:" + mailAddress);
			// if (message != null) {
			// log.log(Level.INFO, "message:" + message);
			// }

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			int targetUserId = manager.getUserIdByMailAddress(mailAddress);

			log.log(Level.INFO, "id from mail address:" + targetUserId);

			// If target mail address is not registered yet
			if (targetUserId == LcomConst.NO_USER) {
				result = LcomConst.INVITATION_NEW_USER_RESULT_OK;
				// int newUserId = manager.getNumOfUserId() + 1;
				// LcomUserData data = new LcomUserData(newUserId, null, null,
				// mailAddress);
				// This method shall cover for both AllUserData and UserData.
				// manager.addNewUserData(data);
			} else {
				// If target mail address is already registered to DB
				LcomUserData data = manager.getUserData(targetUserId);
				// TODO this should be done in second time.
				// manager.updateUserDate(data);
				result = LcomConst.INVITATION_EXISTING_USER_RESULT_OK;
				existingUserId = data.getUserId();
				existingUsername = data.getUserName();
			}
		} else {
			// If some of parameter(s) is null
			result = LcomConst.INVITATION_UNKNOWN_ERROR;
		}
		list.add(String.valueOf(result));
		list.add(String.valueOf(existingUserId));
		list.add(existingUsername);
		list.add(mailAddress);
		// list.add(message);

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}
}
