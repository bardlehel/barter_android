package com.barter.app;

import java.io.Serializable;

import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;


public class Facebook implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Session session = null;
	public GraphUser user = null;
	public String id;
	public String token;
	
	public Facebook(Session s) {
		session = s;
	}

	

}
