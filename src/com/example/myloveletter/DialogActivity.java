package com.example.myloveletter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.example.myloveletter.R;
import com.example.myloveletter.loveletterendpoint.Loveletterendpoint;
import com.example.myloveletter.loveletterendpoint.model.CollectionResponseLoveLetter;
import com.example.myloveletter.loveletterendpoint.model.LoveLetter;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class DialogActivity extends Activity {

	LinearLayout mainScreen;
	String username;
	String receiver;
	@SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();

        setContentView(R.layout.activity_dialog);

        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        username = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
        receiver = intent.getStringExtra(MainActivity.EXTRA_RECEIVER);
        setTitle(username + " ====> " + receiver);
        
        mainScreen =  (LinearLayout)findViewById(R.id.main_screen);
        new Thread(new Runnable() {
            public void run() {
            	Date lastReceiveDate = new Date(new Date().getTime()-Constant.ONE_DAY);
            	Loveletterendpoint.Builder builder = new Loveletterendpoint.Builder(
                AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);
                builder = CloudEndpointUtils.updateBuilder(builder);
                Loveletterendpoint endpoint = builder.build();
            	CollectionResponseLoveLetter LoveLetterRecords = null;
                for(;;){
                	try {
                		LoveLetterRecords = endpoint.listLoveLetter().execute();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                	if (!(LoveLetterRecords == null || LoveLetterRecords.getItems() == null || LoveLetterRecords.getItems().size() < 1)) {
                		List<LoveLetter> lls = LoveLetterRecords.getItems();
                		Collections.sort(lls,new LoveLetterComparer());
                		boolean isReceiveMsg =  false;
                		for(final LoveLetter ll : lls){
                			Date sentDate = new Date(ll.getLoveLetterDate().getValue());
                			if(sentDate.after(lastReceiveDate)&&ll.getReceiver().equals(username)) {
                				isReceiveMsg = true;
                				runOnUiThread(new Runnable() {
                                    public void run() {
                                    	TextView textView = new TextView(DialogActivity.this);
                                    	textView.setTextSize(14);
                                        textView.setText(ll.getContent());
                                        textView.setBackgroundResource(R.drawable.dialog_border_receive);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                                        lp.gravity=Gravity.LEFT;
                                        lp.setMargins(25, 25, 25, 0);
                                        textView.setLayoutParams(lp);
                                        LinearLayout mainScreen =  (LinearLayout)findViewById(R.id.main_screen);
                                        mainScreen.addView(textView);
                                        mainScreen.invalidate();
                                        ScrollView sv = (ScrollView) findViewById(R.id.main_screen_container);
                                        sv.scrollTo(0, 10000);
                                    }
                                });
                			}
                		}
                		if(isReceiveMsg) {
                			lastReceiveDate = new Date();
                			try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            				runOnUiThread(new Runnable() {
                                public void run() {
                                    ScrollView sv = (ScrollView) findViewById(R.id.main_screen_container);
                                    sv.scrollTo(0, 10000);
                                }
                            });
                		}
                	}
                	try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void sendMessage(View view){
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		editText.setText("");
		TextView textView = new TextView(this);
        textView.setTextSize(14);
        textView.setText(message);
        textView.setBackgroundResource(R.drawable.dialog_border);
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity=Gravity.RIGHT;
        lp.setMargins(25, 25, 25, 0);
        textView.setLayoutParams(lp);
        
        mainScreen.addView(textView);
        
        mainScreen.invalidate();
        
        
        new LoveLetterTask().execute(message);
	}
    
    private class LoveLetterTask extends AsyncTask<String, Void, Void> {

        /**
         * Calls appropriate CloudEndpoint to indicate that user checked into a place.
         *
         * @param params the place where the user is checking in.
         * @return 
         */
        @Override
        protected Void doInBackground(String... params) {
          runOnUiThread(new Runnable() {
              public void run() {
                  ScrollView sv = (ScrollView) findViewById(R.id.main_screen_container);
                  sv.scrollTo(0, 10000);
              }
          });
        	
          LoveLetter loveletter = new LoveLetter();

          // Set the ID of the store where the user is.
          loveletter.setContent(params[0]);
          loveletter.setLoveLetterDate(new DateTime(new Date()));
          loveletter.setSender(username);
          loveletter.setReceiver(receiver);

          Loveletterendpoint.Builder builder = new Loveletterendpoint.Builder(
              AndroidHttp.newCompatibleTransport(), new JacksonFactory(), null);

          builder = CloudEndpointUtils.updateBuilder(builder);

          Loveletterendpoint endpoint = builder.build();


          try {
            endpoint.insertLoveLetter(loveletter).execute();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          return null;
        }
    }
        

}
