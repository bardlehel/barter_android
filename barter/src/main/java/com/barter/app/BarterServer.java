package com.barter.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.net.http.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.os.AsyncTask;

public class BarterServer {
	//private static String serverUrl = "http://54.69.96.153:8082";
	private static String serverUrl = "https://evening-inlet-3222.herokuapp.com/";
	private String accessToken = "";
	private BasicCookieStore cookieStore;
	private HttpContext localContext;
	private List<Cookie> cookies;
	private int cookieSize;
	
	public interface ILoginListener{
		public void callback(LoginTask task);
	}
	
	public interface IGetUserListener{
		public void callback(GetUserTask task);
	}
	
	public interface IGetNumbersListener {
		public void callback(GetNumbersTask task);
	}
	
	public interface IGetHavesListener {
		public void callback(GetHavesTask task);
	}
	
	
	class LoginTask extends AsyncTask<String, String, String> {
		
		public String accessToken;
		private ILoginListener listener;

		public void setListener(ILoginListener _listener){
			listener = _listener;
		}

/*
        public boolean isNetworkAvailable() {
            ConnectivityManager cm = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            // if no network is available networkInfo will be null
            // otherwise check if we are connected
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
            return false;
        }
*/
        @Override
    	protected String doInBackground(String... params) {
    	
    		String facebookId = params[0];
            String facebookToken = params[1];
    		
    		cookieStore    =  new BasicCookieStore();
            localContext   = new BasicHttpContext();    
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    		
    		HttpClient client = new DefaultHttpClient();
    		HttpGet get = new HttpGet(serverUrl + "api/access_token?facebook-id=" + facebookId + "&facebook-token=" + facebookToken);

			
      	  	try {
				HttpResponse response = client.execute(get, localContext);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				int cp;
			    while ((cp = reader.read()) != -1) {
			      sb.append((char) cp);
			    }
				reader.close();
								
				JSONObject json = new JSONObject(sb.toString());
				accessToken = json.getString("accessToken");
				
				if(accessToken == "") {
					throw new BarterException("accessToken not set after login");
				}
				return accessToken;
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BarterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return "";
    	 
    	}
    	 
    	protected void onPostExecute(String result) {
    		listener.callback(this);
    	}
    } // end LoginTask
	
	
	class GetUserTask extends AsyncTask<String, String, String> {
		public JSONObject userInfo = null;
		private IGetUserListener listener;

		public void setListener(IGetUserListener _listener){
			listener = _listener;
		}
		
		
		@Override
    	protected String doInBackground(String... params) {
			String accessToken = params[0];
    		//String password = params[1];
    		
    		HttpClient client = new DefaultHttpClient();
    		HttpPost post = new HttpPost(serverUrl + "/api/get_user");
      	  	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
      	  	pairs.add(new BasicNameValuePair("accessToken", accessToken));
      	  	//pairs.add(new BasicNameValuePair("password", password));
      	  	UrlEncodedFormEntity entity;
			
      	  	try {
				entity = new UrlEncodedFormEntity(pairs);
				post.setEntity(entity);
				
				HttpResponse response = client.execute(post, localContext);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				int cp;
			    while ((cp = reader.read()) != -1) {
			      sb.append((char) cp);
			    }
				reader.close();
								
				userInfo = new JSONObject(sb.toString());
				 		
				
			} /*catch (BarterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/ catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return "";
			
    	
    	}
    	
    	protected void onPostExecute(String result) {
    		listener.callback(this);
    	}
	}
	
	class GetNumbersTask extends AsyncTask<String, String, String> {
		public String numHaves, numWants;
		public JSONObject numbers = null;
		private IGetNumbersListener listener;

		public void setListener(IGetNumbersListener _listener){
			listener = _listener;
		}
		
		
		@Override
    	protected String doInBackground(String... params) {
			String accessToken = params[0];
    		
    		HttpClient client = new DefaultHttpClient();
    		HttpPost post = new HttpPost(serverUrl + "/api/get_numbers");
      	  	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
      	  	pairs.add(new BasicNameValuePair("accessToken", accessToken));
      	  	UrlEncodedFormEntity entity;
			
      	  	try {
				entity = new UrlEncodedFormEntity(pairs);
				post.setEntity(entity);
				
				HttpResponse response = client.execute(post, localContext);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				int cp;
			    while ((cp = reader.read()) != -1) {
			      sb.append((char) cp);
			    }
				reader.close();
								
				numbers = new JSONObject(sb.toString());
				numHaves = numbers.getString("haves");
				numWants = numbers.getString("wants");
				 		
				if(numbers == null) {
					throw new BarterException("accessToken not set after login");
				}
				return accessToken;
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BarterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return "";
			
    	
    	}
    	
    	protected void onPostExecute(String result) {
    		listener.callback(this);
    	}
	}
	
	class AddWantTask extends AsyncTask<String, String, String> {
	   	 
    	@Override
    	protected String doInBackground(String... params) {
			return "";
    	
    	}
    	
    	protected void onPostExecute(String result) {
       	 
    	}
    	
	}
	
	
	class AddHaveTask extends AsyncTask<String, String, String> {
	   	 
    	@Override
    	protected String doInBackground(String... params) {
			return "";
    	
    	}
    	
    	protected void onPostExecute(String result) {
       	 
    	}
    	
	}
	
	class GetCategoryTask extends AsyncTask<String, String, String> {
	   	 
    	@Override
    	protected String doInBackground(String... params) {
			return "";
    	
    	}
    	
    	protected void onPostExecute(String result) {
       	 
    	}
    	
	}
	
	class GetHavesTask extends AsyncTask<String, String, String> {
		public class Have {
			public String title;
			public String imageUrl;
		}
		
		public List<Have> haves;
	   	 
		private IGetHavesListener listener;

		public void setListener(IGetHavesListener _listener){
			listener = _listener;
		}
		
    	@Override
    	protected String doInBackground(String... params) {
    		String accessToken = params[0];
    		
    		HttpClient client = new DefaultHttpClient();
    		HttpPost post = new HttpPost(serverUrl + "/api/get_haves");
      	  	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
      	  	pairs.add(new BasicNameValuePair("accessToken", accessToken));
      	  	UrlEncodedFormEntity entity;
			
      	  	try {
				entity = new UrlEncodedFormEntity(pairs);
				post.setEntity(entity);
				
				HttpResponse response = client.execute(post, localContext);
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				int cp;
			    while ((cp = reader.read()) != -1) {
			      sb.append((char) cp);
			    }
				reader.close();
								
				
				JSONArray jsonArray = new JSONArray(sb.toString());
				
				haves = new ArrayList<Have>();
				

				for(int i = 0; i <  jsonArray.length(); i++)
				{
					JSONObject element = jsonArray.getJSONObject(i);
					Have have = new Have();
					have.title = element.getString("title");
					have.imageUrl = element.getString("image_url");
					haves.add(have);
				}
				
				return "";
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	return "";
		
    	
    	}
    	
    	protected void onPostExecute(String result) {
    		listener.callback(this);
    	}
    	
	}
	
	public void login(String[] facebookParams,  ILoginListener listener) {
		LoginTask task = new LoginTask();
		task.setListener(listener);
		task.execute(facebookParams[0], facebookParams[1], null);
	}
	
	public void getUser(String token, IGetUserListener listener) {
		GetUserTask task = new GetUserTask();
		task.setListener(listener);
		task.execute(token);
	}
	
	public void getNumbers(String token, IGetNumbersListener listener) {
		GetNumbersTask task = new GetNumbersTask();
		task.setListener(listener);
		task.execute(token);
	}
	
	public void getHaves(String token, IGetHavesListener listener) {
		GetHavesTask task = new GetHavesTask();
		task.setListener(listener);
		task.execute(token);
	}
}






