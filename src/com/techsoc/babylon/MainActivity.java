package com.techsoc.babylon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.techsoc.Language.Translator;
import com.techsoc.babylon.singletalk.ChatMessage;
import com.techsoc.babylon.singletalk.UserPagerFragment;

public class MainActivity extends FragmentActivity {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 0;

	public static final int NUM_PAGES = 2;

	protected static String PACKAGE_NAME;

	private EditText write_message_et;
	private ViewPager mPager;
	private ImageButton keyboard_btn;
	private ImageView mic_btn;
	private ImageView edgeGlow_iv;
	
	private UserPagerAdapter mPagerAdapter;
	private UserPagerFragment mUserPagerFragment;

	public HashMap<String, ArrayList<ChatMessage>> chatSource;

	public ArrayList<ChatMessage> englishMessageHistory = new ArrayList<ChatMessage>();
	public ArrayList<ChatMessage> germanMessageHistory = new ArrayList<ChatMessage>();
	public ArrayList<ChatMessage> russianMessageHistory = new ArrayList<ChatMessage>();
	
	public ArrayList<String> userNames = new ArrayList<String>();
	public ArrayList<String> userColours = new ArrayList<String>();
	
	public int currentPagePosition = 0; // Needed to change the names in actionbar
	
	int messageCounter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//initialisng colours, currently there are only two colours
		userColours.add("blue");
		userColours.add("red");
		userColours.add("yellow");
		
		//Initialising Names
		userNames.add("Vlad");
		userNames.add("Martin");
		
		PACKAGE_NAME = this.getApplicationContext().getPackageName();
		
		edgeGlow_iv = (ImageView)findViewById(R.id.edge_glow);
		keyboard_btn = (ImageButton)findViewById(R.id.keyboard_btn);
		mic_btn = (ImageView)findViewById(R.id.microphone_btn);
		write_message_et = (EditText) findViewById(R.id.write_message_field);
		write_message_et.setOnEditorActionListener(new OnEditorActionListener() {
	        
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			     if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
			                
			       if(write_message_et.requestFocus()) {
			        			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			        			imm.hideSoftInputFromWindow(v.getWindowToken(),0); 
			        }
			            	
			            	playKeyboardHideAnim();
			            	
			            	if (v.getText().toString().isEmpty() != true){	// Check if the text is not empty
			            		
				            	submitMessage(write_message_et.getText().toString());
			            		
			            	}
			            	
			         }    
			     return false;
			 }
		});
		
		
		mic_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				String tag = (String) button.getTag();
				Log.v(tag, "mic CLick");
				//button.setBackgroundResource(R.drawable.mic_btn_pressed_uk);
				startVoiceRecognitionActivity();
			}
		});
		
		keyboard_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View button) {
				String tag = (String) button.getTag();
				Log.v(tag, "mic CLick");
				playKeyboardShowAnim();
			}
		});
		 
		
		/* Initialise the language pager */
		mPager = (ViewPager) findViewById(R.id.user_pager);
		mPagerAdapter = new UserPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		
		
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}


			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") // New API error caused because of setBackground which is available from API 16. 
			@Override
			public void onPageSelected(int position) {
								
				Log.v("OnPageChangeListener", "User Changed");
				
				currentPagePosition = position;
				
				getActionBar().setTitle(userNames.get(position));
				
				//Change colour of mic and glow based on the user
				
				String colour = userColours.get(position%(userColours.size()));
				
				if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN){
					
					mic_btn.setBackground(getResources().getDrawable(
							getResources().getIdentifier("mic_btn_selector_" + colour, "drawable", PACKAGE_NAME)));
										
					edgeGlow_iv.setBackground(getResources().getDrawable(
							getResources().getIdentifier("edge_glow_" + colour, "drawable", PACKAGE_NAME)));
					
					mic_btn.invalidate();
				}else{	
					// For devices older than API 16
					mic_btn.setBackgroundDrawable(getResources().getDrawable(
							getResources().getIdentifier("mic_btn_selector_" + colour, "drawable", PACKAGE_NAME)));
					
					edgeGlow_iv.setBackgroundDrawable(getResources().getDrawable(
							getResources().getIdentifier("edge_glow_" + colour, "drawable", PACKAGE_NAME)));
				
					mic_btn.invalidate();
				}
				
								
				mUserPagerFragment = mPagerAdapter.getFragment(position);
				mUserPagerFragment.adapter.clearChat();

				String currentLanguage = mUserPagerFragment.getLanguage();
				
				String currentUser = mUserPagerFragment.getUserName();

				
				
				ArrayList<ChatMessage> currentChatSource = chatSource.get(currentLanguage);

				for (int i = 0; i < currentChatSource.size(); i++) {

					ChatMessage curMessage = currentChatSource.get(i);

					if (curMessage.getAuthor().equals(currentUser))
						curMessage.setPosition(false);
					else curMessage.setPosition(true);
					
					mUserPagerFragment.adapter.add(curMessage);
				}
			}

		});

		chatSource = new HashMap<String, ArrayList<ChatMessage>>();

	}


	private void submitMessage(String stringMessage) {

		mUserPagerFragment = mPagerAdapter.getFragment(mPager.getCurrentItem());

		String language = mUserPagerFragment.getLanguage();
		String currentUserName = mUserPagerFragment.getUserName();

		ChatMessage newChatMessage = new ChatMessage(false, stringMessage, language, currentUserName, Calendar.getInstance());

		new TranslateText(newChatMessage).execute(newChatMessage.getText(),
				language, "ru");

		new TranslateText(newChatMessage).execute(newChatMessage.getText(),
				language, "de");

		new TranslateText(newChatMessage).execute(newChatMessage.getText(),
				language, "en");

		mUserPagerFragment.addChatMessage(newChatMessage);

		write_message_et.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actionbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            return true;
	        case R.id.action_add_people:
	        	
	        	AlertDialog.Builder newUserDialog = new AlertDialog.Builder(this);

	        	newUserDialog.setTitle("Add People");
	        	newUserDialog.setMessage("What's your Name?");


	        	final EditText input = new EditText(this);
	        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	        	        LinearLayout.LayoutParams.MATCH_PARENT,
	        	        LinearLayout.LayoutParams.WRAP_CONTENT);
	        	input.setLayoutParams(lp);
	        	newUserDialog.setView(input);

	        	newUserDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int whichButton) {
	        	    	
	        	    	String userName = input.getText().toString();
	        	    	
	        	    	/*
	        	    	 * 
	        	    	 
	        	    	UserPagerFragment frag = UserPagerFragment.create(position, chatSource, userName);
	        			frags.add(position, frag);
	        	    	*/
	        	    }	
	        	});


	        	newUserDialog.show();
	            return true;
	            
	        case R.id.action_edit_name:
	        	
	        	
	        	AlertDialog.Builder usernameDialog = new AlertDialog.Builder(this);

	        	usernameDialog.setTitle("Edit Name");
	        	usernameDialog.setMessage("New name:");


	        	final EditText editName_et = new EditText(this);
	        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	        	        LinearLayout.LayoutParams.MATCH_PARENT,
	        	        LinearLayout.LayoutParams.WRAP_CONTENT);
	        	editName_et.setLayoutParams(params);
	        	usernameDialog.setView(editName_et);

	        	usernameDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int whichButton) {
	        	    	
	        	    	String newUserName = editName_et.getText().toString();
	        	    	
	    	        	userNames.set(currentPagePosition, newUserName);
	    	        	getActionBar().setTitle(newUserName);
	    	        	mPagerAdapter.getFragment(currentPagePosition).setUserName(newUserName);
	    	        	mPagerAdapter.notifyDataSetChanged();
	        	    }
	        	});


	        	usernameDialog.show();
	        	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	private class UserPagerAdapter extends FragmentStatePagerAdapter {
		private List<UserPagerFragment> frags = new ArrayList<UserPagerFragment>();

		public UserPagerAdapter(FragmentManager fm) {
			super(fm);
		}

	
		@Override
		public Fragment getItem(int position) {
			
			String user_name = userNames.get(position);

			UserPagerFragment frag = UserPagerFragment.create(position, chatSource, user_name);
			frags.add(position, frag);
			return frag;
		}

	
		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		public UserPagerFragment getFragment(int position) {
			return frags.get(position);
		}
	}

	
	private void playKeyboardShowAnim(){
		
		keyboard_btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.keyboard_anim_out));
		keyboard_btn.setVisibility(View.INVISIBLE);
		
		mic_btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_anim));
		mic_btn.setVisibility(View.INVISIBLE);
		
		write_message_et.setVisibility(View.VISIBLE);
		write_message_et.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_anim));
		
		if(write_message_et.requestFocus()) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		}
		
	}
	
	
	private void playKeyboardHideAnim(){
		
		write_message_et.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_anim));
		write_message_et.setVisibility(View.INVISIBLE);
		
		mic_btn.setVisibility(View.VISIBLE);
		mic_btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_anim));
		
		
		keyboard_btn.setVisibility(View.VISIBLE);
		keyboard_btn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.keyboard_anim_in));

	}
	
	
	private void startVoiceRecognitionActivity() {
		
		mUserPagerFragment = mPagerAdapter.getFragment(mPager.getCurrentItem());
		String language = mUserPagerFragment.getLanguage();
		
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == Activity.RESULT_OK) {
			// Returns the arraylist of strings sorted by confidence
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String message = matches.get(0);
			//mic_btn.setBackgroundResource(R.drawable.mic_btn_selector_uk);
			submitMessage(message);
		}
	}
	
	
	class TranslateText extends AsyncTask<String, Void, String> {

		ChatMessage curParentMessage;
		String outputLanguage = "";

		public TranslateText(ChatMessage newChatMessage) {

			this.curParentMessage = newChatMessage;
		}

		// Takes three parameters the text that needs to be translated, the
		// input and output languages

		@Override
		protected String doInBackground(String... languageParams) {

			Translator translator = new Translator(languageParams[1],
					languageParams[2]);

			Log.d("Language 0", languageParams[1]);
			Log.d("Language 1", languageParams[2]);

			outputLanguage = languageParams[2];

			String translatedText = "";

			try {
				translatedText = translator.Translate(languageParams[0]);
			} catch (IOException e) {
				Log.e("Translate", "IOException");
				e.printStackTrace();
			}

			return translatedText;
		}

		
		@Override
		protected void onPostExecute(String translatedText) {
			// TODO Things to do with translated Text
			// Below an example for text to speech using the translated Text

			ChatMessage newChatMessage = new ChatMessage(false, translatedText,
					outputLanguage, curParentMessage.getAuthor(),
					curParentMessage.getCalendarTime());

			if (outputLanguage.equals("en")) {

				if (!chatSource.containsKey("en"))
					chatSource.put("en", englishMessageHistory);
				englishMessageHistory.add(newChatMessage);
			}

			if (outputLanguage.equals("ru")) {
				if (!chatSource.containsKey("ru"))
					chatSource.put("ru", russianMessageHistory);
				russianMessageHistory.add(newChatMessage);
			}

			if (outputLanguage.equals("de")) {
				if (!chatSource.containsKey("de"))
					chatSource.put("de", germanMessageHistory);
				germanMessageHistory.add(newChatMessage);
			}

		}
	}

}
