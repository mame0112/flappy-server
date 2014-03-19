package com.mame.lcom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.gson.Gson;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomCreateAccountServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomCreateAccountServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String password = req.getParameter(LcomConst.SERVLET_PASSWORD);
		String mailAddress = req.getParameter(LcomConst.SERVLET_MAILADDRESS);
		String thumb = req.getParameter(LcomConst.SERVLET_THUMBNAIL);

		List<String> list = new ArrayList<String>();

		int result = LcomConst.CREATE_ACCOUNT_RESULT_OK;
		int userId = LcomConst.NO_USER;

		if (origin != null && userName != null && password != null
				&& mailAddress != null) {
			list.add(origin);

			Blob thumbnail = null;

			if (thumb != null) {
				thumbnail = DatastoreUtil.transcodeString2Blob(thumb);
				if (thumbnail != null) {
					log.log(Level.WARNING,
							"thumbnail size: " + thumbnail.getBytes().length);
				}
			}

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			userId = manager.getUserIdByName(userName);
			if (userId == LcomConst.NO_USER) {
				// Check if the current user is registered by other use because
				// someone asked him to join this service
				int userIdByMail = manager.getUserIdByMailAddress(mailAddress);
				log.log(Level.INFO, "userIdByMail: " + userIdByMail);
				if (userIdByMail == LcomConst.NO_USER) {
					// If the target mail address doesn't exist in DB (This is
					// normal new registration case)
					// Adding +1 should be done by LcomDatabaseManager.
					result = LcomConst.CREATE_ACCOUNT_RESULT_OK;
					LcomUserData data = new LcomUserData(userIdByMail,
							userName, password, mailAddress, thumbnail);
					userId = manager.addNewUserData(data);

				} else {
					// If mail address exist in DB although user name is not
					// registered (This is a case that the user was invited by
					// his friend)
					log.log(Level.INFO, "A2");
					manager.updateUserData(userIdByMail, userName, password,
							mailAddress, thumbnail);
					userId = manager.getNumOfUserId();
					result = LcomConst.CREATE_ACCOUNT_RESULT_OK_WITH_ADDRESS_REGISTERED;
					// LcomUserData data = new LcomUserData(userId, userName,
					// password, mailAddress);
				}
			} else {
				log.log(Level.INFO, "B" + TimeUtil.calcResponse());
				result = LcomConst.CREATE_ACCOUNT_USER_ALREADY_EXIST;
			}
		} else {
			result = LcomConst.CREATE_ACCOUNT_PARAMETER_NULL;
		}

		list.add(String.valueOf(result));
		list.add(String.valueOf(userId));
		list.add(userName);

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}
}
