package com.barter.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

public class LoginListener implements BarterServer.ILoginListener {
    BarterApplication app = null;
    MainActivity mainActivity = null;

    LoginListener(BarterApplication a, MainActivity activity) {
        app = a;
        mainActivity = activity;
    }

    @Override
    public void callback(BarterServer.LoginTask task) {
        app.accessToken = task.accessToken;

        //now that we are logged in:
        //do we have a user record yet??? (this is created when we first login
        //get likes & interests (skip for now)
        //insert likes/interests into database (of certain categories?)
        //create new categories if new
        //create class for this??? yes!


        Fragment f = mainActivity.showMainFragment();
        ((MainFragment)f).barterGetMainNumbers(app);
    }
}