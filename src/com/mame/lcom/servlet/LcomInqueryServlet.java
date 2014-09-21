package com.mame.lcom.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.invitation.LcomMail;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomInqueryServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomInqueryServlet.class
			.getName());

	private enum Return_Code {
		RESULT_OK, RESULT_BEFORE_SEND, RESULT_USERNAME_NULL, RESULT_MAIL_ADDRESS_NULL, RESULT_MESSAGE_NULL,
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		// String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);
		// String origin = CipherUtil.decrypt(
		// req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		// String userName = CipherUtil.decrypt(
		// req.getParameter(LcomConst.SERVLET_USER_NAME), secretKey);
		// String mailAddress = CipherUtil.decrypt(
		// req.getParameter(LcomConst.SERVLET_MAILADDRESS), secretKey);
		// String message = CipherUtil.decrypt(
		// req.getParameter(LcomConst.SERVLET_MESSAGE_BODY), secretKey);
		// String apiLevel = CipherUtil.decrypt(
		// req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);
		// String category = CipherUtil.decrypt(
		// req.getParameter(LcomConst.SERVLET_INQUERY_CATEGORY),
		// secretKey);

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String mailAddress = req.getParameter(LcomConst.SERVLET_MAILADDRESS);
		String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);
		String category = req.getParameter(LcomConst.SERVLET_INQUERY_CATEGORY);

		// success
		Return_Code result = Return_Code.RESULT_OK;

		// List<String> list = new ArrayList<String>();
		if (origin != null && apiLevel != null) {
			log.log(Level.INFO, "origin and apiLevel is not null: " + origin
					+ " / " + apiLevel);
			if (userName != null) {
				if (mailAddress != null) {
					log.log(Level.INFO, "userName: " + userName);
					log.log(Level.INFO, "mailAddress: " + mailAddress);
					log.log(Level.INFO, "category: " + category);
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
			result = Return_Code.RESULT_BEFORE_SEND;
			log.log(Level.INFO, "origin and apiLevel is null");
		}

		// list.add(String.valueOf(result));
		// list.add(origin);

		String url = "/contact.jsp";

		HttpSession session = req.getSession();
		session.setAttribute("result", String.valueOf(result.ordinal()));
		// session.setAttribute("result",
		// CipherUtil.encrypt(String.valueOf(result.ordinal()), secretKey));

		// String json = new Gson().toJson(list);
		// resp.setContentType("application/json");
		// resp.setCharacterEncoding("UTF-8");
		// resp.getWriter().write(json);

		// req.setAttribute("result", result);
		// RequestDispatcher dispatcher = req.getRequestDispatcher(url);
		// try {
		// dispatcher.forward(req, resp);
		// } catch (ServletException e) {
		// log.log(Level.INFO, "ServletException: " + e.getMessage());
		// }

		resp.sendRedirect(url);

	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}
}
