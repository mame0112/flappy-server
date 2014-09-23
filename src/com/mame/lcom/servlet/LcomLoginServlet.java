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
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomLoginServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomLoginServlet.class
			.getName());

	private final static String TAG = "LcomLoginServlet";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DbgUtil.showLog(TAG, "doPost:" + TimeUtil.calcResponse());
		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);
		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String userName = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_NAME), secretKey);
		String password = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_PASSWORD), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		int result = LcomConst.LOGIN_RESULT_OK;
		long userId = LcomConst.NO_USER;

		if (origin != null && userName != null && password != null
				&& apiLevel != null) {
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			DbgUtil.showLog(TAG, "userName:" + userName);
			DbgUtil.showLog(TAG, "password:" + password);
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

		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			DbgUtil.showLog(TAG, "str:" + str);
			String output = CipherUtil.encrypt(str, secretKey);
			DbgUtil.showLog(TAG, "output:" + output);
		}

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DbgUtil.showLog(TAG, "doGet");
	}

}
