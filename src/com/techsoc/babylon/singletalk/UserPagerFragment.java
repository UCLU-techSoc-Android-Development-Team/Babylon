package com.techsoc.babylon.singletalk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.techsoc.babylon.R;

public class UserPagerFragment extends Fragment {

	protected static final String TAG = UserPagerFragment.class.getName();

	public static final String ARG_USER = "user";
	public static final String USER_NAME = "user_name";

	private static String PACKAGE_NAME;
	private int mPageNumber;
		
	private int NUMBER_OF_PAGES = 2;

	/* Discussion List */
	public DiscussArrayAdapter adapter;

	private ListView mDiscussionList;

	/* Pager for Languages */
	private ViewPager mPager;
	private LanguageSliderAdapter mPagerAdapter;

	public String fragmentLang = "";
	private static HashMap<String, ArrayList<ChatMessage>> chatSource;

	private int currentPosition = 0;

	private TextToSpeech textToSpeech;

	public static UserPagerFragment create(int pageNumber,
			HashMap<String, ArrayList<ChatMessage>> inputChatSource,
			String userName) {

		UserPagerFragment fragment = new UserPagerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_USER, pageNumber);
		args.putString(USER_NAME, userName);
		fragment.setArguments(args);

		chatSource = inputChatSource;

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		mPageNumber = getArguments().getInt(ARG_USER);
		PACKAGE_NAME = this.getActivity().getApplicationContext()
				.getPackageName();

		setupTextToSpeech();

	}

	private void setupTextToSpeech() {
		textToSpeech = new TextToSpeech(this.getActivity(),
				new TextToSpeech.OnInitListener() {
					public void onInit(int status) {
						if (status == TextToSpeech.SUCCESS) {
						} else {
							Log.e(TAG, "Failed to intialize Text to Speech");
						}
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.user_pager_fragment, container, false);

		/* Initialise the ListView with its adapter */
		mDiscussionList = (ListView) rootView
				.findViewById(R.id.main_chat_listview);
		adapter = new DiscussArrayAdapter(this.getActivity()
				.getApplicationContext(), R.layout.listitem_discuss);
		mDiscussionList.setAdapter(adapter);

		mDiscussionList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				String currentLanguage = getLanguage();
				ArrayList<ChatMessage> currentChatSource = chatSource
						.get(currentLanguage);
				String text = currentChatSource.get(position).getText();
				
				convertToSpeech(text, currentLanguage);
			}

		});

		/* Initialise the View Pager with its adapter */
		mPager = (ViewPager) rootView.findViewById(R.id.lang_pager);

		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				
				
				
				currentPosition = position;
				String currentLanguage = getLanguage();
				String currentUser = getUserName();
				
				
				Log.d("New Language", currentLanguage);
				Log.d("", "");

				ArrayList<ChatMessage> currentChatSource = chatSource
						.get(currentLanguage);

				adapter.clearChat();

				for (int i = 0; i < currentChatSource.size(); i++) {

					ChatMessage curMessage = currentChatSource.get(i);

					if (curMessage.getAuthor().equals(currentUser))
						curMessage.setPosition(false);
					else
						curMessage.setPosition(true);

					adapter.add(curMessage);
				}
			}

		});

		mPagerAdapter = new LanguageSliderAdapter(
				this.getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setPageTransformer(true, new DepthPageTransformer());

		return rootView;
	}
	
	private void convertToSpeech(String text, String currentLanguage) {
		
		Log.d("Input Language", currentLanguage);
		
		Locale lanLocale;
		
		if (currentLanguage.equals("en")) {
			lanLocale = Locale.UK;
		}
		else lanLocale = new Locale(currentLanguage);
		
		
		if (textToSpeech
				.isLanguageAvailable(lanLocale) == TextToSpeech.LANG_AVAILABLE) {
			textToSpeech.setLanguage(lanLocale);
			Log.v("Translate", "Language Available: " + currentLanguage);
			textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
		} else {
			Log.e("Translate", "Language Not Available: "
					+ currentLanguage);
			Intent installIntent = new Intent();
			installIntent
					.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			this.getActivity().startActivity(installIntent);
		}
	}
	

	public void addChatMessage(ChatMessage cm) {
		adapter.add(cm);
	}

	public String getLanguage() {

		Resources res = getResources();
		String[] langImageSrc = res.getStringArray(R.array.languages);

		return langImageSrc[currentPosition];
	}

	public String getUserName() {

		return getArguments().getString(USER_NAME);
	}
	
	public void setUserName(String userName) {

		 getArguments().putString(USER_NAME,userName);
	}
	

	private class LanguageSliderAdapter extends FragmentStatePagerAdapter {

		private List<LanguageSliderFragment> languages = new ArrayList<LanguageSliderFragment>();

		public LanguageSliderAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			LanguageSliderFragment newFrag = LanguageSliderFragment
					.create(position);
			languages.add(newFrag);

			return newFrag;
		}

		@Override
		public int getCount() {
			return NUMBER_OF_PAGES;
		}
	}
}