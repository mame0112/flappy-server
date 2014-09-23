package com.mame.lcom.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomCipherDebugServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomCipherDebugServlet.class.getName());

	private final static String TAG = "LcomCipherDebugServlet";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// String identifier = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String encryptValue = req.getParameter("servlet_cipher_encrypt");
		String decryptValue = req.getParameter("servlet_cipher_decrypt");

		DbgUtil.showLog(TAG, "encryptValue: " + encryptValue);
		DbgUtil.showLog(TAG, "decryptValue: " + decryptValue);

		CipherUtil util = new CipherUtil();

		String result = null;

		if (encryptValue != null && !encryptValue.equals("null")
				&& encryptValue.length() >= 2) {
			DbgUtil.showLog(TAG, "Try encript");
			result = util.encryptForInputString(encryptValue);
		} else if (decryptValue != null && !decryptValue.equals("null")
				&& decryptValue.length() >= 2) {
			DbgUtil.showLog(TAG, "Try decrypt");
			result = util.decryptForInputString(decryptValue);
		} else {
			DbgUtil.showLog(TAG, "No value inputted");
		}

		DbgUtil.showLog(TAG, "result: " + result);

		HttpSession session = req.getSession();
		session.setAttribute("result", result);
		String url = "/debug.jsp";
		resp.sendRedirect(url);

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		DbgUtil.showLog(TAG, "doGet");
		try {
			doPost(req, resp);
		} catch (IOException e) {
			DbgUtil.showLog(TAG, "IOException: " + e.getMessage());
		}
	}
}
