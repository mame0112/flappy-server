package com.mame.lcom.invitation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.mail.MailService.Message;
import com.mame.lcom.util.TimeUtil;

public class LcomMail {

	private final static Logger log = Logger
			.getLogger(LcomMail.class.getName());

	public LcomMail() {

	}

	public void sendInvitationMail(String address, String fromUserName,
			String message) throws UnsupportedEncodingException {
		log.log(Level.INFO, "sendInvitationMail" + TimeUtil.calcResponse());
		if (address != null) {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			String msgBody = "Welcome to Loose communication!\n\n"
					+ "You are hereby invited by " + fromUserName
					+ " to this service.";

			Message msg = new Message();
			msg.setSender("mame0112@gmail.com");
			msg.setTo(address);
			msg.setSubject("Test");
			msg.setTextBody(msgBody);
			try {
				MailServiceFactory.getMailService().send(msg);
			} catch (IOException e){
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

//	public void sendWelcomeMail(String userName, String mailAddress)
//			throws UnsupportedEncodingException, MessagingException {
//		log.log(Level.INFO, "sendWelcomeMail" + TimeUtil.calcResponse());
//		if (mailAddress != null) {
//			Properties props = new Properties();
//			Session session = Session.getDefaultInstance(props, null);
//			// String msgBody = "Welcome to My Fan Club!";
//
//			String msgBody = userName
//					+ "����A�u�}�C�t�@���N���u�v�������p�����������肪�Ƃ��������܂��I\n\n"
//					+ "�u�}�C�t�@���N���u�v�́A���Ȃ������i�C�ɂȂ��Ă���l�ɂ��̋C������`������A���͂��Ȃ��̃t�@���������l����t�@�����b�Z�[�W���󂯎�����肷�邱�Ƃ��ł���A�\�[�V�����t�@���N���u�T�[�r�X�ł��B\n\n"
//					+ "http://mame0112.appspot.com/\n\n"
//					+ "�����s���_�₲�ӌ��Ȃǂ���܂�����A���L�̃��[���A�h���X���炲�A����������"
//					+ "\nmyfanclub.committee@gmail.com\n\n"
//					+ "�܂��A�{�T�[�r�X�������p�ɂȂ�ۂ̒ʐM��́A���q�l�����g�̂����S�ƂȂ�܂��̂ŁA���炩���߂�������������\n\n"
//					+ "---\n\n"
//					+ userName
//					+ ", thank you for using \"My Fan Club\".\n\n"
//					+ "In \"My Fan CLub\" is a social fan clbu service that enable you to find person that you are interested in and someone who are interested in you will come to your fan.\nLet's find out such person each other with your friends and enjoy!\n\n"
//					+ "if you have friend interested in, how about to contact them via E-mail or Twitter from this service "
//					+ "http://mame0112.appspot.com/\n\n"
//					+ "If you have any question or comment on the service, you can contact us by using below e-mail.\nmyfanclub.committee@gmail.com\n\n"
//					+ "Please not that data traffic charge is your responsibility.\n\n"
//					+ "----------\n\n2013 �}�C�t�@���N���u(My Fan Club)";
//
//			// + objRb.getString("str.welcome_mail_body1")
//			// + objRb.getString("str.welcome_mail_body2")
//			// + objRb.getString("str.welcome_mail_body3")
//			// + objRb.getString("str.welcome_mail_body4")
//			// + objRb.getString("str.welcome_mail_body5")
//			// + objRb.getString("str.welcome_mail_body6");
//
//			Message msg = new MimeMessage(session);
//			msg.setContent("My Fan club - Inquery1", "text/html;charset=UTF-8");
//			msg.setHeader("Content-Transfer-Encoding", "7bit");
//			msg.setFrom(new InternetAddress("myfanclub.committee@gmail.com"));
//
//			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
//					mailAddress, "My Fan Club"));
//			// msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
//			// "aka_copaco_ao_copaco_ki_copaco@docomo.ne.jp", "Mr. User"));
//			String subject = "�}�C�t�@���N���u�ɂ悤���� - Welcome to My Fan Club!";
//			((MimeMessage) msg).setSubject(subject, "UTF-8");
//			msg.setText(msgBody);
//			Transport.send(msg);
//		}
//	}
}
