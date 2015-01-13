package com.barter.app;

import android.app.Application;

public class BarterApplication extends Application {
	public Facebook facebook;
	public BarterServer barterServer;
	public String accessToken = "";

    public class MainScreenData {
        public int numHaves;
        public int numWants;
    }

    public MainScreenData mainScreenData;
}
