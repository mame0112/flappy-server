package com.mame.lcom.constant;

public class LcomConst {

	public final static int NO_USER = -1;

	// public final static String DATE_PATTERN = "yyyy-mm-dd'T'HH:mm:ss";
	// public final static String DATE_PATTERN = "dd-MMM-yyy";
	// public final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_PATTERN = "dd-MM-yy:HH:mm:SS";

	// FIXME Change this before release/debug
	public final static boolean IS_ENCRYPT = true;

	// FIXME Change this before release/debug
	public final static boolean IS_DEBUG = true;

	public final static String MEMCACHE_SEPARATOR = "#@#";

	public final static String API_KEY = "AIzaSyBkrzyfBwaHQgjVphRiUNHusjEOjPQdOr4";

	public final static int API_LEVEL = 1;

	public final static String NULL = "null";

	public final static String SERVLET_USER_ID = "servlet_userid";

	public final static String SERVLET_USER_NAME = "servlet_user_name";

	public final static String SERVLET_TARGET_USER_ID = "servlet_target_userid";

	public final static String SERVLET_TARGET_USER_NAME = "servlet_target_user_name";

	public final static String SERVLET_PASSWORD = "servlet_password";

	public final static String SERVLET_MAILADDRESS = "servet_mailAddress";

	public final static String SERVLET_LANGUAGE = "servet_language";

	public final static String SERVLET_MESSAGE_BODY = "servlet_message_body";

	public final static String SERVLET_MESSAGE_DATE = "servlet_message_date";

	public final static String SERVLET_THUMBNAIL = "servlet_thumbnail";

	public final static String SERVLET_TOTAL_USER_NUM = "servlet_total_user_num";

	public final static String SERVLET_ORIGIN = "servlet_origin";

	public final static String SERVLET_DEVICE_ID = "servlet_device_id";

	public final static String SERVLET_API_LEVEL = "servlet_api_level";

	public final static String SERVLET_IDENTIFIER = "servlet_identifier";

	public final static String SERVLET_INQUERY_CATEGORY = "servlet_inquery_category";

	public final static String MESSAGE_EXPIRED = "EXPIRED";

	public final static String KIND_ALL_USER_DATA = "LcomAllUserData";

	public final static String KIND_USER_DATA = "LcomUserData";

	public final static String KIND_FRIENDSHIP_DATA = "LcomFriendshipData";

	public final static String ENTITY_TOTAL_USER_NUM = "mTotalUserNum";

	public final static String ENTITY_USER_ID = "mUserId";

	public final static String ENTITY_USER_NAME = "mUserName";

	public final static String ENTITY_PASSWORD = "mPassword";

	public final static String ENTITY_MAIL_ADDRESS = "mMailAddress";

	public final static String ENTITY_THUMBNAIL = "mThumbnail";

	public final static String ENTITY_DEVICE_ID = "mDeviceId";

	public final static String ENTITY_FRIENDSHIP_USER_ID = "mUserId";

	public final static String ENTITY_FRIENDSHIP_USER_NAME = "mUserName";

	public final static String ENTITY_FRIENDSHIP_FRIEND_ID = "mFriendUserId";

	public final static String ENTITY_FRIENDSHIP_FRIEND_NAME = "mFriendUserName";

	public final static String ENTITY_FRIENDSHIP_RECEIVE_MESSAGE = "mReceiveMessage";

	public final static String ENTITY_FRIENDSHIP_POSTED_TIME = "mPostedTime";

	public final static String ENTITY_FRIENDSHIP_EXPIRE_TIME = "mExpireTime";

	public final static String MEMCACHE_KEY_DEVICEID = "memcache_device_id";

	// public final static String SERVLET_CONTEXT_IDENTIFIER =
	// "servlet_identifier";

	/**
	 * Login constants
	 */
	public final static int LOGIN_RESULT_OK = 0;

	public final static int LOGIN_RESULT_PARAMETER_NULL = 1;

	public final static int LOGIN_RESULT_LOGIN_FAILED = 2;

	/**
	 * Create account constants
	 */
	public final static int CREATE_ACCOUNT_RESULT_OK = 0;

	public final static int CREATE_ACCOUNT_PARAMETER_NULL = 1;

	public final static int CREATE_ACCOUNT_USER_ALREADY_EXIST = 2;

	public final static int CREATE_ACCOUNT_UNKNOWN_ERROR = 3;

	public final static int CREATE_ACCOUNT_RESULT_OK_WITH_ADDRESS_REGISTERED = 4;

	/**
	 * Send new (Welcome) message constants
	 */
	public final static int INVITATION_NEW_USER_RESULT_OK = 0;

	public final static int INVITATION_EXISTING_USER_RESULT_OK = 1;

	public final static int INVITATION_UNKNOWN_ERROR = 2;

	/**
	 * confirmed new message constants
	 */
	public final static int INVITATION_CONFIRMED_RESULT_OK = 0;

	public final static int INVITATION_CONFIRMED_UNKNOWN_ERROR = 1;

	public final static int INVITATION_CONFIRMED_MAIL_CANNOT_BE_SENT = 2;

	/**
	 * confirmed send message constants
	 */
	public final static int SEND_MESSAGE_RESULT_OK = 0;

	public final static int SEND_MESSAGE_UNKNOWN_ERROR = 1;

	public final static int SEND_MESSAGE_DATE_CANNOT_BE_PARSED = 2;

	/**
	 * Constant for message expire time.
	 */
	public final static long MESSAGE_EXPIRE_PERIOD = 60 * 60 * 1000;
	// 30min*60sec*1000msec
	// public final static int MESSAGE_EXPIRE_PERIOD = 30; // 30min

	/**
	 * Separator between each item within message
	 */
	public final static String SEPARATOR = "@@";

	public final static String MESSAGE_SEPARATOR = "__";

	public final static String ITEM_SEPARATOR = "##";

	/**
	 * Constants for Memcache
	 */
	public final static String NUM_OF_USER = "num_of_user";

	public final static String USER_ID_BY_MAIL_ADDRESS = "userid_by_mail_address";

	/**
	 * Constants for locale
	 */
	public static enum LOCALE_SETTING {
		ENGLISH, JAPANESE
	};

}
