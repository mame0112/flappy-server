package com.mame.lcom.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.CipherUtil;

public class LcomRequestFriendsThumbnailServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomRequestFriendsThumbnailServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost");

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String friendsId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_TARGET_USER_ID), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		// Friend array as List
		List<String> list = new ArrayList<String>();

		if (origin != null && friendsId != null && apiLevel != null) {
			List<String> ids = parseFriendIds(friendsId);
			list.add(origin);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			HashMap<Integer, String> datas = manager.getFriendThubmnails(ids);
			if (datas != null) {
				String result = parseThumbnailData(datas);

				// If no thumbnail available, "null" string shall be returned.
				list.add(result);
			}

		}

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}

	private List<String> parseFriendIds(String friendsId) {
		if (friendsId != null) {
			log.log(Level.WARNING, "parseFriendIds");
			String[] idArray = friendsId.split(LcomConst.SEPARATOR);
			if (idArray != null && idArray.length != 0) {
				log.log(Level.WARNING, "A");
				List<String> result = Arrays.asList(idArray);

				for (String str : result) {
					log.log(Level.WARNING, "str: " + str);
				}

				return result;
			}
		}
		return null;
	}

	private String parseThumbnailData(HashMap<Integer, String> datas) {
		if (datas != null && datas.size() != 0) {
			String result = "a";
			for (Iterator<?> it = datas.entrySet().iterator(); it.hasNext();) {
				// for (HashMap<Integer, String> data : datas) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				String friendThumb = (String) entry.getValue();
				if (friendThumb != null && friendThumb.length() != 0) {
					result = result + LcomConst.ITEM_SEPARATOR + friendId
							+ LcomConst.SEPARATOR + friendThumb;
				}
			}

			// If more than one thumbnail is registered
			if (result != null && result.length() > 1) {
				result = result.substring(
						(1 + LcomConst.ITEM_SEPARATOR.length()),
						result.length());
			} else {
				// If no thumbnail is registered, we return null
				result = null;
			}

			return result;
		}

		return null;
	}
}
