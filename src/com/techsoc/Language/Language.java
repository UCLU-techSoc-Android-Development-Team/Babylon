package com.techsoc.Language;

import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;

public class Language {

	
	private String[] swipeCountries = new String[5];
	
	private ArrayList<String> countries;
	private ArrayList<String> codes;
	
	public Language() {
		
		
		swipeCountries[0] = "United Kingdom";
		swipeCountries[1] = "Russia";
		
		getLangCodes();
	}
	
	
	public void setSwipeLang(String[] swipeLang) {
		
		this.swipeCountries = swipeLang;
	}
	
	public String[] getSwipeCountries(){
		
		return swipeCountries;
	}
	
	
	public String getLanguageCode(String country) {
		
		Log.v("getLanguageCode ", "for "+country);
		
		if (countries.contains(country)){
			
			String newCode = codes.get(countries.indexOf(country));
			
			Log.v("getLanguageCode ", "code for "+country+" is "+ newCode);
			return newCode;
		}
		else Log.e("getLanguageCode", "no country "+country);
		
		return "";
	}
	
	public String getLangCodeAfterWrong(String country, String wrongCode) {
		
		int wrongIndex = codes.indexOf(wrongCode);
		Log.e("getLangCodeAfterWrong ", "wrong code for "+country+" is "+ wrongCode);
		codes.remove(wrongIndex);
		countries.remove(wrongIndex);
		return getLanguageCode(country);
	}

    
    public boolean doWeHaveThisCountry(String inputCountry) {
    	ArrayList<String> arrayCountries = this.countries;
    	for (int counter = 0; counter < arrayCountries.size(); counter++) {
    		if (inputCountry.equals(arrayCountries.get(counter))) return true;
    	}
    	return false;
    }
    
    public ArrayList<String> getCountries(){
    	
    	return this.countries;
    }
	
	private void getLangCodes() {
		
		Locale[] locales = Locale.getAvailableLocales();
		countries = new ArrayList<String>();
		codes = new ArrayList<String>();
		
		
		for (Locale locale : locales) {
			
			String country = locale.getDisplayCountry();
			String code = locale.getLanguage();
			String code2 = locale.getDisplayName();
			String code3 =""; //= locale.getISO3Country();
			String code4 =""; //= locale.getISO3Language();
			
			
			if ( (country.trim().length() > 0)) {// && !countries.contains(country)) ) {
				
				Log.v(country+" ", code+" "+code2+" "+code3+" "+code4);
				countries.add(country);
				codes.add(code);
		    }
		}
		
	}
	
}
