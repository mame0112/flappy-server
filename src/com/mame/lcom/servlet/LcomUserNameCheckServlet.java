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
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.TimeUtil;

public class LcomUserNameCheckServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomUserNameCheckServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);

		List<String> list = new ArrayList<String>();

		int result = LcomConst.CREATE_ACCOUNT_RESULT_OK;
		int userId = LcomConst.NO_USER;

		list.add(origin);

		if (origin != null && userName != null && apiLevel != null) {
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			userId = manager.getUserIdByName(userName);
			if (userId == LcomConst.NO_USER) {
				result = LcomConst.CREATE_ACCOUNT_RESULT_OK;
			} else {
				log.log(Level.INFO, "B");
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
