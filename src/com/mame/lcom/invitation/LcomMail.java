package com.mame.lcom.invitation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Session;

import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.TimeUtil;

public class LcomMail {

	private final static Logger log = Logger
			.getLogger(LcomMail.class.getName());

	public LcomMail() {

	}

	public void sendInvitationMail(String address, String fromUserName,
			String message, String language)
			throws UnsupportedEncodingException {
		log.log(Level.INFO, "sendInvitationMail");
		if (address != null) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			String msgBody = null;

			String msgEng = "Welcome to Loose communication!\n\n"
					+ "You are hereby invited by "
					+ fromUserName
					+ " to this service."
					+ "This service is a stress-less communication tool by to be disappeard message."
					+ "For more details, please see below link:\n\n"
					+ "http://loosecommunication.appspot.com/\n\n"
					+ "And if you have any comment or question on this service, please send an e-mail to us from below link.\n"
					+ "flappy.communication@gmail.com\n\n" + "---\n"
					+ "2014 flappy¥n¥n¥n";

			String msgJpn = "flappyにようこそ!¥n¥n" + "あなたは" + fromUserName
					+ " さんにこのサービスに招待されました。¥n"
					+ "このサービスは、自然に消えるメッセージによるストレスフリーのコミュニケーションツールです。¥n"
					+ "より詳細は、下記のリンクよりご確認ください。\n\n"
					+ "http://loosecommunication.appspot.com/¥n¥n"
					+ "また、もし何か疑問や不明点などございましたら、下記の連絡先へご連絡ください¥n"
					+ "flappy.communication@gmail.com\n\n" + "---\n"
					+ "2014 flappy";
			// If language is not null
			if (language != null) {
				// And if language is japanese
				if (language.equals(LcomConst.LOCALE_SETTING.JAPANESE)) {
					if (message != null && !message.equals(LcomConst.NULL)) {
						msgBody = message + "¥n¥n" + "---" + "¥n¥n" + msgJpn
								+ msgEng;
					} else {
						msgBody = msgJpn + msgEng;
					}
				} else {
					// Otherwise (In this case, local is English and others)
					if (message != null && !message.equals(LcomConst.NULL)) {
						msgBody = message + "¥n¥n" + "---" + "¥n¥n" + msgEng
								+ msgJpn;
					} else {
						msgBody = msgEng + msgJpn;
					}

				}
			} else {
				// If language is null
				if (message != null && !message.equals(LcomConst.NULL)) {
					msgBody = message + "¥n¥n" + "---" + "¥n¥n" + msgEng
							+ msgJpn;
				} else {
					msgBody = msgEng + msgJpn;
				}
			}

			Message msg = new Message();
			msg.setSender("flappy.communication@gmail.com");
			msg.setTo(address);
			msg.setSubject("flappy");
			msg.setTextBody(msgBody);
			try {
				MailServiceFactory.getMailService().send(msg);
			} catch (IOException e) {
				log.log(Level.WARNING, "Mail exception: " + e.getMessage());
			}
			// message.setsend

			// Message msg = new MimeMessage(session);
			// msg.setContent("My Fan club - Inquery1",
			// "text/html;charset=UTF-8");
			// msg.setHeader("Content-Transfer-Encoding", "7bit");
			// // msg.setFrom(new InternetAddress(address));
			// msg.setFrom(new
			// InternetAddress("myfanclub.committee@gmail.com"));
			//
			// // msg.addRecipient(Message.RecipientType.TO, new
			// InternetAddress(
			// // address, "My Fan club"));
			// msg.setRecipient(Message.RecipientType.TO, new InternetAddress(
			// address));
			// // msg.addRecipient(Message.RecipientType.TO, new
			// InternetAddress(
			// // address, "My Fan club"));
			// ((MimeMessage) msg).setSubject("Loose communication", "UTF-8");
			// msg.setText(msgBody);
			// Transport.send(msg);
		}
	}
}
