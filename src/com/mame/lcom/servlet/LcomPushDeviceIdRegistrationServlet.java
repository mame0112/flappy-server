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
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.CipherUtil;

public class LcomPushDeviceIdRegistrationServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomPushDeviceIdRegistrationServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost");

		String origin = CipherUtil.decrypt(req
				.getParameter(LcomConst.SERVLET_ORIGIN));
		String userId = CipherUtil.decrypt(req
				.getParameter(LcomConst.SERVLET_USER_ID));
		String deviceId = CipherUtil.decrypt(req
				.getParameter(LcomConst.SERVLET_DEVICE_ID));
		String apiLevel = CipherUtil.decrypt(req
				.getParameter(LcomConst.SERVLET_API_LEVEL));

		List<String> list = new ArrayList<String>();

		if (origin != null && userId != null && deviceId != null
				&& apiLevel != null) {

			list.add(origin);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			manager.setDeviceIdForMessagePush(Integer.valueOf(userId), deviceId);
		}

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
