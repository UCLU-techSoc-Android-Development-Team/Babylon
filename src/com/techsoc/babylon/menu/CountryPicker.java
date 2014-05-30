package com.techsoc.babylon.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.techsoc.Language.Language;
import com.techsoc.babylon.MainActivity;
import com.techsoc.babylon.R;

public class CountryPicker {
	
	// maximum swipable languages allowed
	int MAX_LANG = 5;
	String ERROR_MESSAGE = "You can't have more than 5 countries in dialog :(";

	MainActivity context;

	ArrayAdapter<String> countryListAdapter;
	ArrayList<String> curCountries;

	ArrayList<String> curSwipeCountries;
	ArrayAdapter<String> swipeListAdapter;
	
	public CountryPicker(MainActivity context) {
		this.context = context;
	}
	
	
	
	public void startDialog(final MainActivity context, final Language lang, final int currentPageNumber) {
		
		
		final AlertDialog.Builder countryListDialog = new AlertDialog.Builder(context);

		countryListDialog.setTitle("List of Countries");
		
		LayoutInflater inflater = LayoutInflater.from(context);
		final LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.menu_country_picker, null, false);

		setUpSwipable(context, layout, lang.getSwipeCountries());
		setUpCountryList(context, layout);
		setUpAutoComplete(context, layout);
		

		LinearLayout.LayoutParams contrListParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(contrListParams);

		countryListDialog.setView(layout);
		final AlertDialog alertDialog = countryListDialog.create();
		
		alertDialog.show();
		
		Button doneButton =(Button) layout.findViewById(R.id.doneButton);
		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String[] swipeCountryList = getSwipeCountries();
				
				lang.setSwipeLang(swipeCountryList);
				
				context.getUserFragment(currentPageNumber).updateLanguageFragment();
				context.updateLanguageSrc();
				
				
				AutoCompleteTextView countryInputText = (AutoCompleteTextView) layout
						.findViewById(R.id.inputText);
				countryInputText.setText("");
				alertDialog.dismiss();
			}	
		}); 
		
	}
	
	
	
	public String[] getSwipeCountries() {
		
		String[] outputSwipe = new String[MAX_LANG];
		
		for (int i = 0; (i < curSwipeCountries.size())&&(i<MAX_LANG); i++) {
			outputSwipe[i] = curSwipeCountries.get(i);
		}
				
		return outputSwipe;
	}

	private void autoUpdateList(final ArrayAdapter<String> autoCompleteAdapter,
			final ArrayList<String> countriesList,
			final ArrayAdapter<String> listViewAdapter) {

		autoCompleteAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				super.onChanged();

				if (!autoCompleteAdapter.isEmpty()) {
					countriesList.clear();

					for (int counter = 0; counter < autoCompleteAdapter
							.getCount(); counter++) {

						Log.v("autoCompleteAdapter", "getCount="
								+ autoCompleteAdapter.getCount());
						String country = autoCompleteAdapter.getItem(counter);
						countriesList.add(country);

					}

					listViewAdapter.notifyDataSetChanged();
				}
			}
		});

	}

	private void setUpAutoComplete(MainActivity context, LinearLayout layout) {

		final ArrayAdapter<String> autoCmpltAdapter = new ArrayAdapter<String>(
				context, android.R.layout.simple_list_item_1, getCountries());
		AutoCompleteTextView countryInputText = (AutoCompleteTextView) layout
				.findViewById(R.id.inputText);
		countryInputText.setAdapter(autoCmpltAdapter);
		countryInputText.setThreshold(1);
		countryInputText.setDropDownHeight(0);

		countryInputText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				autoUpdateList(autoCmpltAdapter, curCountries,
						countryListAdapter);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				autoUpdateList(autoCmpltAdapter, curCountries,
						countryListAdapter);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				autoUpdateList(autoCmpltAdapter, curCountries,
						countryListAdapter);
			}
		});

	}

	private void setUpSwipable(MainActivity context, LinearLayout layout, String[] curSwipable) {

		curSwipeCountries = new ArrayList<String>();
		if (curSwipable != null) {
			for (int i = 0; (i < curSwipable.length) && (curSwipable[i]!=null); i++) {
				curSwipeCountries.add(curSwipable[i]);
			}
		}

		swipeListAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, curSwipeCountries);

		final ListView swipeCountryList = (ListView) layout
				.findViewById(R.id.swipableCountryList);
		swipeCountryList.setAdapter(swipeListAdapter);

		swipeCountryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				String pickedCountry = (String) swipeCountryList
						.getItemAtPosition(position);
				curSwipeCountries.remove(position);

				curCountries.add(pickedCountry);
				Collections.sort(curCountries);

				countryListAdapter.notifyDataSetChanged();
				swipeListAdapter.notifyDataSetChanged();
			}

		});
	}

	private void setUpCountryList(MainActivity context, LinearLayout layout) {

		curCountries = getCountries();

		countryListAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, curCountries);
		final ListView countryList = (ListView) layout
				.findViewById(R.id.countryList);
		countryList.setAdapter(countryListAdapter);

		countryList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				// check if you can add more countries to swipable
				if (curSwipeCountries.size() < MAX_LANG) {
					
					String pickedCountry = (String) countryList
							.getItemAtPosition(position);
					
					curCountries.remove(position);

					curSwipeCountries.add(pickedCountry);

					countryListAdapter.notifyDataSetChanged();
					swipeListAdapter.notifyDataSetChanged();
				}
				else {
					popUpAlert(ERROR_MESSAGE);
				}
			}
		}

		);

	}

	
	private ArrayList<String> getCountries() {
		Locale[] locales = Locale.getAvailableLocales();
		ArrayList<String> countries = new ArrayList<String>();
		for (Locale locale : locales) {
			String country = locale.getDisplayCountry();
			if (country.trim().length() > 0 && !countries.contains(country)) {
				countries.add(country);
			}
		}
		Collections.sort(countries);
		return countries;
	}
	
	private void popUpAlert(String message) {
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
	
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.dismiss();
					}
				});
		
		final AlertDialog alertDialog = alertDialogBuilder.create();
			
				
			alertDialog.show();
	}

}
