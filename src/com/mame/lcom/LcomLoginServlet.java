package com.mame.lcom;

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
import com.mame.lcom.util.TimeUtil;

public class LcomLoginServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomLoginServlet.class
			.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());
		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String password = req.getParameter(LcomConst.SERVLET_PASSWORD);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		int result = LcomConst.LOGIN_RESULT_OK;
		int userId = LcomConst.NO_USER;

		if (origin != null && userName != null && password != null
				&& apiLevel != null) {
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			log.log(Level.INFO, "userName:" + userName);
			log.log(Level.INFO, "password:" + password);
			userId = manager.getUserIdByNameAndPassword(userName, password);
			if (userId == LcomConst.NO_USER) {
				result = LcomConst.LOGIN_RESULT_LOGIN_FAILED;
			}
		} else {
			result = LcomConst.LOGIN_RESULT_PARAMETER_NULL;
		}

		list.add(String.valueOf(result));
		list.add(String.valueOf(userId));
		list.add(userName);

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.WARNING, "doGet");
	}

}
