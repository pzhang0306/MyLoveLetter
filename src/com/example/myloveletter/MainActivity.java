package com.example.myloveletter;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	public final static String EXTRA_USERNAME = "com.example.myfirstapp.USERNAME";
	public final static String EXTRA_RECEIVER = "com.example.myfirstapp.RECEIVER";
	public final static String PREFS_NAME = "MyPrefsFile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
	    actionBar.hide();
	    
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		boolean rem = settings.getBoolean("rem", false);
		
		if(rem) {
			String username = settings.getString("username", "");
			String password = settings.getString("password", "");
			EditText editText = (EditText) findViewById(R.id.username);
			editText.setText(username);
			editText = (EditText) findViewById(R.id.password);
			editText.setText(password);
			CheckBox checkBox = (CheckBox) findViewById(R.id.keep_login);
			checkBox.setChecked(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void dialogPage(View view){
		
		Intent intent=new Intent(this, DialogActivity.class);
		EditText editText = (EditText) findViewById(R.id.username);
		String username = editText.getText().toString();
		editText = (EditText) findViewById(R.id.password);
		String password = editText.getText().toString();
		CheckBox checkBox = (CheckBox) findViewById(R.id.keep_login);
		if(checkBox.isChecked()) {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor =  settings.edit();
			editor.putBoolean("rem", true);
			editor.putString("username", username);
			editor.putString("password", password);
			editor.commit();
		} else {
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			SharedPreferences.Editor editor =  settings.edit();
			editor.putBoolean("rem", false);
			editor.commit();
		}
		if(username.equals(Account.PENG)&&password.equals(Account.PENGPWD)) {
			String receiver = Account.YAOYI;
			intent.putExtra(EXTRA_USERNAME, username);
			intent.putExtra(EXTRA_RECEIVER, receiver);
			startActivity(intent);
		} else if (username.equals(Account.YAOYI)&&password.equals(Account.YAOYIPWD)) {
			String receiver = Account.PENG;
			intent.putExtra(EXTRA_USERNAME, username);
			intent.putExtra(EXTRA_RECEIVER, receiver);
			startActivity(intent);
		} else {
			TextView tv = (TextView) findViewById(R.id.login_message);
			tv.setText("Acount doesn't exist or password doesn't match!");
		}
		
	}

}