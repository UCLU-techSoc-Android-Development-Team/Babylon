package com.techsoc.babylon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.techsoc.Language.Translator;
import com.techsoc.babylon.singletalk.ChatMessage;
import com.techsoc.babylon.singletalk.UserPagerFragment;

public class MainActivity extends FragmentActivity {

	public static final int NUM_PAGES = 2;

	private EditText editText1;
	private ViewPager mPager;

	private UserPagerAdapter mPagerAdapter;
	private UserPagerFragment mUserPagerFragment;

	public HashMap<String, ArrayList<ChatMessage>> chatSource;

	public ArrayList<ChatMessage> englishMessageHistory = new ArrayList<ChatMessage>();
	public ArrayList<ChatMessage> germanMessageHistory = new ArrayList<ChatMessage>();
	public ArrayList<ChatMessage> russianMessageHistory = new ArrayList<ChatMessage>();

	int messageCounter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub

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

		editText1 = (EditText) findViewById(R.id.write_message_field);
		editText1.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					submitMessage();
				}
				return false;
			}
		});

		ImageView buttonSend = (ImageView) findViewById(R.id.microphone_btn);
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submitMessage();
			}
		});

		chatSource = new HashMap<String, ArrayList<ChatMessage>>();

	}

	private void submitMessage() {

		mUserPagerFragment = mPagerAdapter.getFragment(mPager.getCurrentItem());

		String language = mUserPagerFragment.getLanguage();
		String currentUserName = mUserPagerFragment.getUserName();

		ChatMessage newChatMessage = new ChatMessage(false, editText1.getText()
				.toString(), language, currentUserName, Calendar.getInstance());

		new TranslateText(newChatMessage).execute(newChatMessage.getText(),
				language, "ru");

		new TranslateText(newChatMessage).execute(newChatMessage.getText(),
				language, "de");

		new TranslateText(newChatMessage).execute(newChatMessage.getText(),
				language, "en");

		mUserPagerFragment.addChatMessage(newChatMessage);

		editText1.setText("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class UserPagerAdapter extends FragmentStatePagerAdapter {
		private List<UserPagerFragment> frags = new ArrayList<UserPagerFragment>();

		public UserPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			String user_name = "";
			if (position==0) user_name="Vlad";
			else user_name = "Martin";

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
