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
import com.mame.lcom.invitation.LcomMail;
import com.mame.lcom.util.TimeUtil;

public class LcomInqueryServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomInqueryServlet.class
			.getName());

	private enum Return_Code {
		RESULT_OK, RESULT_ORIGIN_OR_API_LEVEL_NULL, RESULT_USERNAME_NULL, RESULT_MAIL_ADDRESS_NULL, RESULT_MESSAGE_NULL,
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());
		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String mailAddress = req.getParameter(LcomConst.SERVLET_MAILADDRESS);
		String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);
		String category = req.getParameter(LcomConst.SERVLET_INQUERY_CATEGORY);

		// success
		Return_Code result = Return_Code.RESULT_OK;

		List<String> list = new ArrayList<String>();
		if (origin != null && apiLevel != null) {
			log.log(Level.INFO, "origin and apiLevel is not null");
			if (userName != null) {
				if (mailAddress != null) {
					result = Return_Code.RESULT_OK;
					// Send message
					LcomMail mail = new LcomMail();
					boolean mailResult = mail.sendInqueryMail(mailAddress,
							category, userName, message);
					if (mailResult) {
						log.log(Level.INFO, "successfully message sent");
					} else {
						log.log(Level.INFO, "failed to send message");
					}
				} else {
					result = Return_Code.RESULT_MAIL_ADDRESS_NULL;
				}
			} else {
				result = Return_Code.RESULT_USERNAME_NULL;
			}
		} else {
			log.log(Level.INFO, "origin or apiLevel is null");
			result = Return_Code.RESULT_ORIGIN_OR_API_LEVEL_NULL;
		}

		list.add(String.valueOf(result));
		list.add(origin);

		String url = "/contact.html";
		resp.sendRedirect(url);

		// String json = new Gson().toJson(list);
		// resp.setContentType("application/json");
		// resp.setCharacterEncoding("UTF-8");
		// resp.getWriter().write(json);

	}
}
