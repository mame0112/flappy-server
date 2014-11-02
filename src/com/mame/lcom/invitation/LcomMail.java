package com.mame.lcom.invitation;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

public class LcomMail {

	private final static Logger log = Logger
			.getLogger(LcomMail.class.getName());

	private final static String TAG = "LcomMail";

	public LcomMail() {

	}

	public boolean sendServiceWelcomeMail(String address, String userName,
			String language) throws UnsupportedEncodingException {
		DbgUtil.showLog(TAG, "sendServiceWelcomeMail");
		if (userName != null) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			String msgBody = null;

			String msgEng = "Welcome to flappy!<br><br>"
					+ "This service is a stress-less communication tool by to be disappeard message."
					+ "For more details, please see below link:<br><br>"
					+ "http://loosecommunication.appspot.com/<br><br>"
					+ "And if you have any comment or question on this service, please send an e-mail to us from below link.<br>"
					+ "flappy.communication@gmail.com<br><br>" + "---<br>"
					+ "2014 flappy<br><br><br>";

			String msgJpn = "flappy(フラッピー)にようこそ!<br><br>"
					+ "このサービスは、「既読スルー」可能な、自然に消えるメッセージによる、ゆるふわコミュニケーションツールです。<br>"
					+ "より詳細は、下記のリンクよりご確認ください。<br><br>"
					+ "http://loosecommunication.appspot.com/<br><br>"
					+ "また、もし何か疑問や不明点などございましたら、下記の連絡先へご連絡ください<br>"
					+ "flappy.communication@gmail.com<br><br>" + "---<br>"
					+ "2014 flappy<br><br>";
			// If language is not null
			if (language != null) {
				// And if language is japanese
				if (language.equals(LcomConst.LOCALE_SETTING.JAPANESE)) {
					msgBody = msgJpn + msgEng;

				} else {
					// Otherwise (In this case, local is English and others)
					msgBody = msgEng + msgJpn;
				}
			} else {
				// If language is null
				msgBody = msgEng + msgJpn;
			}

			try {
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(
						"flappy.communication@gmail.com", "flappy"));
				msg.setRecipient(Message.RecipientType.TO, new InternetAddress(
						address, "New user"));
				msg.setSubject("flappy");
				msg.setText(msgBody);
				msg.setContent(msgBody, "text/html");
				Transport.send(msg);
				DbgUtil.showLog(TAG, "Successfully sent message.");
				return true;
			} catch (MessagingException e) {
				DbgUtil.showLog(TAG, "MessagingException:: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	public boolean sendInvitationMail(String address, String fromUserName,
			String message, String language)
			throws UnsupportedEncodingException {
		DbgUtil.showLog(TAG, "sendInvitationMail");
		if (address != null) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			String msgBody = null;

			// String msgEng = "Welcome to Loose communication!\n\n"
			// + "You are hereby invited by "
			// + fromUserName
			// + " to this service."
			// +
			// "This service is a stress-less communication tool by to be disappeard message."
			// + "For more details, please see below link:\n\n"
			// + "http://loosecommunication.appspot.com/\n\n"
			// +
			// "And if you have any comment or question on this service, please send an e-mail to us from below link.\n"
			// + "flappy.communication@gmail.com\n\n" + "---\n"
			// + "2014 flappy¥n¥n¥n";
			//
			// String msgJpn = "flappyにようこそ!¥n¥n" + "あなたは" + fromUserName
			// + " さんにこのサービスに招待されました。¥n"
			// + "このサービスは、自然に消えるメッセージによるストレスフリーのコミュニケーションツールです。¥n"
			// + "より詳細は、下記のリンクよりご確認ください。\n\n"
			// + "http://loosecommunication.appspot.com/¥n¥n"
			// + "また、もし何か疑問や不明点などございましたら、下記の連絡先へご連絡ください¥n"
			// + "flappy.communication@gmail.com\n\n" + "---\n"
			// + "2014 flappy";

			String msgEng = "Welcome to flappy!<br><br>"
					+ "You are hereby invited by "
					+ fromUserName
					+ " to this service."
					+ "This service is a stress-less communication tool by to be disappeard message."
					+ "For more details, please see below link:<br><br>"
					+ "http://loosecommunication.appspot.com/<br><br>"
					+ "And if you have any comment or question on this service, please send an e-mail to us from below link.<br>"
					+ "flappy.communication@gmail.com<br><br>" + "---<br>"
					+ "2014 flappy<br><br><br>";

			String msgJpn = "flappy(フラッピー)にようこそ!<br><br>" + "あなたは" + fromUserName
					+ " さんから、このサービスに招待されました。<br>"
					+ "flappyは、自然に消えるメッセージによるストレスフリーのコミュニケーションサービスです。<br>"
					+ "より詳細は、下記のリンクよりご確認ください。<br><br>"
					+ "http://loosecommunication.appspot.com/<br><br>"
					+ "また、もし何か疑問や不明点などございましたら、下記の連絡先へご連絡ください<br>"
					+ "flappy.communication@gmail.com<br><br>" + "---<br>"
					+ "2014 flappy<br><br>";
			// If language is not null
			if (language != null) {
				// And if language is japanese
				if (language.equals(LcomConst.LOCALE_SETTING.JAPANESE)) {
					if (message != null && !message.equals(LcomConst.NULL)) {
						msgBody = message + "<br><br>" + "---" + "<br><br>"
								+ msgJpn + msgEng;
					} else {
						msgBody = msgJpn + msgEng;
					}
				} else {
					// Otherwise (In this case, local is English and others)
					if (message != null && !message.equals(LcomConst.NULL)) {
						msgBody = message + "<br><br>" + "---" + "<br><br>"
								+ msgEng + msgJpn;
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

			try {
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(
						"flappy.communication@gmail.com", "flappy"));
				msg.setRecipient(Message.RecipientType.TO, new InternetAddress(
						address, "New user"));
				msg.setSubject("flappy");
				msg.setText(msgBody);
				msg.setContent(msgBody, "text/html");
				Transport.send(msg);
				DbgUtil.showLog(TAG, "Successfully sent message.");
				return true;
			} catch (MessagingException e) {
				DbgUtil.showLog(TAG, "MessagingException:: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	public boolean sendInqueryMail(String address, String category,
			String userName, String message)
			throws UnsupportedEncodingException {
		DbgUtil.showLog(TAG, "sendInqueryMail");
		if (address != null) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);

			String msgBody = "userName: " + userName + "<br>" + "category: "
					+ category + "<br>" + "message: " + message;

			try {
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(
						"flappy.communication@gmail.com", "Inquery"));
				msg.setRecipient(Message.RecipientType.TO, new InternetAddress(
						"flappy.communication@gmail.com", "flappy"));
				msg.setSubject("Inquery");
				msg.setText(msgBody);
				msg.setContent(msgBody, "text/html");
				Transport.send(msg);
				DbgUtil.showLog(TAG, "Successfully sent message.");
				return true;
			} catch (MessagingException e) {
				DbgUtil.showLog(TAG, "MessagingException:: " + e.getMessage());
				return false;
			}
		}
		return false;
	}
}
