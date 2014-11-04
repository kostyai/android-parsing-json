package com.blueBird.TestApp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity {

    private ProgressDialog pDialog;


    // URL to get users JSON
    private static String url = "https://api.github.com/users";

    // JSON Node names
    private static final String TAG_LOGIN = "login";
    private static final String TAG_ID = "id";
    private static final String TAG_AVATAR = "avatar_url";
    private static final String TAG_GRAV = "gravatar_id";
    private static final String TAG_URL = "url";
    private static final String TAG_HTML = "html_url";
    private static final String TAG_FOLLOWERS = "followers_url";
    private static final String TAG_FOLLOWING = "following_url";
    private static final String TAG_GISTS = "gists_url";
    private static final String TAG_STARRED = "starred_url";
    private static final String TAG_SUBS = "subscriptions_url";
    private static final String TAG_ORG = "organizations_url";
    private static final String TAG_REPOS = "repos_url";
    private static final String TAG_EVENTS = "events_url";
    private static final String TAG_RECEIVED = "received_events_url";
    private static final String TAG_TYPE = "type";
    private static final String TAG_ADMIN = "site_admin";

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> usersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        usersList = new ArrayList<HashMap<String, String>>();

        // Calling async task to get json
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray jsonObj = new JSONArray(jsonStr);

                    final TableLayout table = (TableLayout) findViewById(R.id.table);
                    // looping through All Users
                    for (int i = 0; i < jsonObj.length(); i++) {

                        final View row = createRow(jsonObj.getJSONObject(i));
                        table.post(new Runnable() {
                            public void run() {
                                table.addView(row);
                            }
                        });
                        JSONObject c = jsonObj.getJSONObject(i);

                        String login = c.getString(TAG_LOGIN);
                        String id = c.getString(TAG_ID);
                        String ava = c.getString(TAG_AVATAR);
                        ava = Html.fromHtml(ava).toString();
                        String grav = c.getString(TAG_GRAV);
                        String urll = c.getString(TAG_URL);
                        String html = c.getString(TAG_HTML);
                        String follow = c.getString(TAG_FOLLOWERS);
                        String following = c.getString(TAG_FOLLOWING);
                        String gists = c.getString(TAG_GISTS);
                        String starred = c.getString(TAG_STARRED);
                        String subs = c.getString(TAG_SUBS);
                        String org = c.getString(TAG_ORG);
                        String repos = c.getString(TAG_REPOS);
                        String events = c.getString(TAG_EVENTS);
                        String received = c.getString(TAG_RECEIVED);
                        String type = c.getString(TAG_TYPE);
                        String admin = c.getString(TAG_ADMIN);


                        // tmp hashmap for single user
                        HashMap<String, String> user = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        user.put(TAG_ID, id);
                        user.put(TAG_LOGIN, login);
                        user.put(TAG_URL, urll);
                        user.put(TAG_AVATAR, ava);

                        // adding user to user list
                        usersList.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }

        private View createRow(JSONObject item) throws JSONException {
            View row = getLayoutInflater().inflate(R.layout.row, null);
            ((TextView) row.findViewById(R.id.TRAIN_CELL)).setText(item
                    .getString(TAG_LOGIN));
            ((TextView) row.findViewById(R.id.FROM_CELL)).setText(item
                    .getString(TAG_URL));

            ((ImageView) row.findViewById(R.id.TO_CELL)).setImageBitmap(getBitmapFromURL(item
                    .getString(TAG_AVATAR)));
            return row;
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null,o);
            //The new size we want to scale to
            final int REQUIRED_WIDTH=100;
            final int REQUIRED_HIGHT=100;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;
            o.inJustDecodeBounds=false;
            o.inSampleSize=scale;

            input.close();
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            input = connection.getInputStream();
            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            return BitmapFactory.decodeStream(input, null, o);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}