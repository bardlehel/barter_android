package com.barter.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.barter.app.BarterServer.GetHavesTask;
import com.barter.app.BarterServer.GetNumbersTask;
import com.barter.app.BarterServer.IGetHavesListener;
import com.barter.app.BarterServer.IGetNumbersListener;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;


public class MainActivity extends ActionBarActivity {
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private boolean isResumed = false;
    private boolean userSkippedLogin = false;

    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";


    private static final int MAIN = 0;
    private static final int HAVES = 1;
    //private static final int WANTS = 3;
    private static final int CHOOSE_TOPIC = 2;
    //private static final int SETTINGS = 5;
    private static final int LOADING = 3;
    private static final int SPLASH = 4;
    private static final int FRAGMENT_COUNT = SPLASH + 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	
	private Facebook facebook = null;
	
	public void manageHaves(View view) {
		  FragmentManager fm = getSupportFragmentManager();
          FragmentTransaction transaction = fm.beginTransaction();
          transaction.hide(fragments[MAIN]);
          transaction.show(fragments[HAVES]);
		  transaction.addToBackStack(null);
         setTitle("Barter - Haves");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		  transaction.commit();
        ((HaveFragment)fragments[HAVES]).barterGetHaves();
    }
	  
	  public void manageWants(View view) {
		    // Do something in response to button click
          int x;
          x = 0;
	  }
	  


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
		
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager(); 
		  
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);

        fragments[SPLASH]= fm.findFragmentById(R.id.login_fragment);
        fragments[MAIN]= fm.findFragmentById(R.id.main_fragment);
        fragments[HAVES] = fm.findFragmentById(R.id.have_fragment);
        fragments[LOADING] = fm.findFragmentById(R.id.loading_fragment);
        //fragments[WANTS] = fm.findFragmentById(R.id.have_fragment);
        fragments[CHOOSE_TOPIC] = fm.findFragmentById(R.id.choose_topic_fragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length - 1; i++) {
            transaction.hide(fragments[i]);
        }

        transaction.commit();

        showFragment(SPLASH, false);
		    
	}



	public void shouldDisplayHomeUp(){
	   //Enable Up button only  if there are entries in the back stack
	boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
	getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
	}

	@Override
	public boolean onSupportNavigateUp() {
	    //This method is called when the up button is pressed. Just the pop back stack.
	    getSupportFragmentManager().popBackStack();
	    return true;
	}
	



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
            PackageInfo info;
            try {
                info = getPackageManager().getPackageInfo("com.barter.app",  PackageManager.GET_SIGNATURES);


                for (Signature signature : info.signatures)
                {
                    MessageDigest md;

                    md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String s = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                    Log.d("KeyHash:", s);
                }
            }

            catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            } // TODO Auto-generated catch block

        if (state.isOpened()) {
            final ActionBarActivity a = this;

            Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);
            //userInfoTextView.setVisibility(View.VISIBLE);
            final String facebookToken  = session.getAccessToken();
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                // callback after Graph API response with user object
                @Override
                public void onCompleted(GraphUser user, Response response) {

                    if (user != null) {
                        Facebook facebook = ((BarterApplication) getApplication()).facebook;
                        if (facebook != null) {
                            facebook.user = user;
                            facebook.id = user.getId();
                            facebook.token = facebookToken;
                        }

                        String[] params = new String[2];
                        params[0] = facebook.id;
                        params[1] = facebook.token;

                        //login to get accesstoken
                        ((BarterApplication) getApplication()).barterServer = new BarterServer();
                        BarterServer.ILoginListener listener = new LoginListener((BarterApplication) getApplication(), (MainActivity)a);
                        ((BarterApplication) getApplication()).barterServer.login(params, listener);
                    }
                }
            }).executeAsync();

            ((BarterApplication)getApplication()).facebook = new Facebook(session);
        } else if (state.isClosed()) {
            //userInfoTextView.setVisibility(View.INVISIBLE);
            ((BarterApplication)getApplication()).facebook = null;
            //showFragment(SPLASH, false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            uiHelper.onSaveInstanceState(outState);

            outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
        }

        @Override
        protected void onResumeFragments() {
            super.onResumeFragments();
            Session session = Session.getActiveSession();

            if (session != null && session.isOpened()) {
                // if the session is already open, try to show the selection fragment
               showFragment(LOADING, false);
               getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                userSkippedLogin = true;
            } else if (userSkippedLogin) {
                showFragment(MAIN, false);
            } else {
                // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
                showFragment(SPLASH, false);
            }
        }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[MAIN].isVisible()) {
            if (menu.size() == 0) {
                //settings = menu.add(R.string.settings);
            }
            return true;
        } else {
            menu.clear();
            //settings = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (item.equals(settings)) {
        //    showSettingsFragment();
        //    return true;
        //}
        return false;
    }

    //public void showSettingsFragment() {
    //    showFragment(SETTINGS, true);
    //}

    public Fragment showMainFragment() {
       getSupportActionBar().show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       return showFragment(MAIN, false);

    }
    private Fragment showFragment(int fragmentIndex, boolean addToBackStack) {
        Fragment ret = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
                ret = fragments[i];

                //if(i == MAIN)
                //    fragments[SPLASH].finish();
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        return ret;
    }
}

	

