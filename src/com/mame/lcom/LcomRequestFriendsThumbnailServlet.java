package com.mame.lcom;

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

public class LcomRequestFriendsThumbnailServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomRequestFriendsThumbnailServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost");

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String friendsId = req.getParameter(LcomConst.SERVLET_TARGET_USER_ID);

		// Friend array as List
		List<String> ids = parseFriendIds(friendsId);
		List<String> list = new ArrayList<String>();

		if (origin != null && friendsId != null) {
			log.log(Level.INFO, "friendsId: " + friendsId);
			list.add(origin);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
			HashMap<Integer, String> datas = manager.getFriendThubmnails(ids);
			if (datas != null) {
				String result = parseThumbnailData(datas);
				log.log(Level.INFO, "result; " + result);
				list.add(result);
			}

		}

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}

	private List<String> parseFriendIds(String friendsId) {
		if (friendsId != null) {
			log.log(Level.INFO, "friendsId: " + friendsId);
			String[] idArray = friendsId.split(LcomConst.SEPARATOR);
			if (idArray != null && idArray.length != 0) {
				List<String> result = Arrays.asList(idArray);

				for (String str : result) {
					log.log(Level.INFO, "str: " + str);
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
				if (friendThumb != null) {
					result = result + friendId + LcomConst.SEPARATOR
							+ friendThumb + LcomConst.SEPARATOR
							+ LcomConst.ITEM_SEPARATOR;
				}
			}

			if (result != null) {
				// Remove first "a"
				int end = result.length() - LcomConst.SEPARATOR.length()
						- LcomConst.ITEM_SEPARATOR.length();
				result = result.substring(1, end);
			}

			return result;
		}

		return null;
	}
}
