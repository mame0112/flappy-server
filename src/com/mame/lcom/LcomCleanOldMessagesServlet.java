package com.mame.lcom;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LcomCleanOldMessagesServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomCleanOldMessagesServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.WARNING, "Clean message doGet");

	}

}
