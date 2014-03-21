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
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.techsoc.Language.Translator;
import com.techsoc.babylon.singletalk.ChatMessage;
import com.techsoc.babylon.singletalk.UserPagerFragment;

public class MainActivity extends FragmentActivity {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 0;

	public static int NUM_PAGES = 2;

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
		userNames.add("Vlad");
		userColours.add("blue");
		
		
		userNames.add("Martin");
		userColours.add("red");
		
		
		
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
				} else {	
					// For devices older than API 16
					mic_btn.setBackgroundDrawable(getResources().getDrawable(
							getResources().getIdentifier("mic_btn_selector_" + colour, "drawable", PACKAGE_NAME)));
					
					edgeGlow_iv.setBackgroundDrawable(getResources().getDrawable(
							getResources().getIdentifier("edge_glow_" + colour, "drawable", PACKAGE_NAME)));
				
					mic_btn.invalidate();
				}
				
					
				mUserPagerFragment = mPagerAdapter.getFragment(position);
				mPagerAdapter.notifyDataSetChanged();
				
	        	
				mUserPagerFragment.adapter.clearChat();

				String currentLanguage = mUserPagerFragment.getLanguage();
				String currentUserName = mUserPagerFragment.getUserName();
				int currentPageNumber = mUserPagerFragment.getPageNumber();

				
				ArrayList<ChatMessage> currentChatSource = chatSource.get(currentLanguage);
				
				if (currentChatSource!=null) {
					
					for (int i = 0; i < currentChatSource.size(); i++) {
						
						ChatMessage curMessage = currentChatSource.get(i);
						
						if (curMessage.getPageNumber() == currentPageNumber) {
							
							if (!curMessage.getAuthor().equals(currentUserName)) curMessage.setAuthor(currentUserName);
							curMessage.setPosition(false);
						}
						else
							curMessage.setPosition(true);
						
						mUserPagerFragment.adapter.add(curMessage);
					}
				}
				
			}

		});

		chatSource = new HashMap<String, ArrayList<ChatMessage>>();

	}


	private void submitMessage(String stringMessage) {

		mUserPagerFragment = mPagerAdapter.getFragment(mPager.getCurrentItem());

		String language = mUserPagerFragment.getLanguage();
		String currentUserName = mUserPagerFragment.getUserName();
		String currentColour =  mUserPagerFragment.getUserColour();
		int currentPageNumber = mUserPagerFragment.getPageNumber();

		ChatMessage newChatMessage = new ChatMessage(false, stringMessage, language, currentUserName, 
				Calendar.getInstance(), currentColour, currentPageNumber);

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
	        	
	        	final LinearLayout listView = new LinearLayout(this);
	        	listView.setOrientation(LinearLayout.VERTICAL);
	        	
	        	final EditText input = new EditText(this);
	        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	        	        LinearLayout.LayoutParams.MATCH_PARENT,
	        	        LinearLayout.LayoutParams.WRAP_CONTENT);
	        	input.setLayoutParams(lp);
	        	listView.addView(input);
	        	
	        	
	        	 final Spinner sprCoun = new Spinner(this);
	        	 sprCoun.setLayoutParams(lp);
	             List<String> colourList = new ArrayList<String>();
	             colourList.add("blue");
	             colourList.add("red");
	             colourList.add("yellow");
	             colourList.add("green");
	             colourList.add("gray");
	             ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, colourList);
	             dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	             sprCoun.setAdapter(dataAdapter);
	             listView.addView(sprCoun);
	             
	             newUserDialog.setView(listView);
		        	
	             
	             
	        	newUserDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
	        	    public void onClick(DialogInterface dialog, int whichButton) {
	        	    	
	        	    	String userName = input.getText().toString();
	        	    	String userColour = sprCoun.getSelectedItem().toString();
	        	    	 	 
	        	    	userNames.add(userName);
	        	    	userColours.add(userColour);
	        	    	NUM_PAGES++;
	        	    	mPagerAdapter.notifyDataSetChanged();  	    
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
			
			UserPagerFragment frag; 
			
			String user_name = userNames.get(position);
			String user_colour = userColours.get(position);
			
			frag = UserPagerFragment.create(position, chatSource, user_name, user_colour);
			
			if (frags.size()>position) {
				frags.remove(position);
			}
			frags.add(position, frag);
			
			
			return frag;
		}

	
		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
		

		public UserPagerFragment getFragment(int position) {
			
			if (frags.get(position).getActivity()==null) {
				Toast.makeText(getApplicationContext(), "oh no", Toast.LENGTH_SHORT).show();
				
				return initFragment(position);
				
			}
			
			return frags.get(position);
		}
		
		
		 private UserPagerFragment initFragment(int position) {
			 
			 FragmentManager fragmentManager = getSupportFragmentManager();
		     FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			 
			 fragmentTransaction.remove(frags.get(position)).commit();
			 frags.remove(position);
			 
			 fragmentTransaction = fragmentManager.beginTransaction();
			       
		     UserPagerFragment frag;
			 String user_name = userNames.get(position);
			 String user_colour = userColours.get(position);
				
			 frag = UserPagerFragment.create(position, chatSource, user_name, user_colour);
		     fragmentTransaction.replace(R.id.user_pager_frag, frag);
		     fragmentTransaction.commit();
		        
		     frags.add(position, frag);
		     return frag;
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
					curParentMessage.getCalendarTime(), curParentMessage.getBoxColour(), curParentMessage.getPageNumber());

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
