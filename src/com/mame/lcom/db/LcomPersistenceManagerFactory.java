package com.mame.lcom.db;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class LcomPersistenceManagerFactory {

	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private LcomPersistenceManagerFactory() {

	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

}
