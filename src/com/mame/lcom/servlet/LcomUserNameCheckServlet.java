package com.mame.lcom.servlet;

import java.io.IOException;

import com.mame.lcom.util.DbgUtil;

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
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomUserNameCheckServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomUserNameCheckServlet.class.getName());

	private final static String TAG = "LcomUserNameCheckServlet";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DbgUtil.showLog(TAG, "doPost" + TimeUtil.calcResponse());

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String userName = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_NAME), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		List<String> list = new ArrayList<String>();

		int result = LcomConst.CREATE_ACCOUNT_RESULT_OK;
		long userId = LcomConst.NO_USER;

		list.add(origin);

		if (origin != null && userName != null && apiLevel != null) {
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			userId = manager.getUserIdByName(userName);
			if (userId == LcomConst.NO_USER) {
				result = LcomConst.CREATE_ACCOUNT_RESULT_OK;
			} else {
				DbgUtil.showLog(TAG, "B");
				result = LcomConst.CREATE_ACCOUNT_USER_ALREADY_EXIST;
			}
		} else {
			result = LcomConst.CREATE_ACCOUNT_PARAMETER_NULL;
		}

		list.add(String.valueOf(result));
		list.add(String.valueOf(userId));
		list.add(userName);

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);
	}

}
