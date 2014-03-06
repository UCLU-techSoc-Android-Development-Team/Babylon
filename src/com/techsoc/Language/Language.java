package com.techsoc.Language;

import java.util.ArrayList;

public class Language {

	public String RUSSIAN_LANGUAGE_CODE = "ru";
	public String ENGLISH_LANGUAGE_CODE = "en";
	public String GERMAN_LANGUAGE_CODE = "de";
	public String ITALIAN_LANGUAGE_CODE = "it";
	public String ROMANIAN_LANGUAGE_CODE = "ro";
	public String NORWEGIAN_LANGUAGE_CODE = "no";
	public String SOUTHKOREAN_LANGUAGE_CODE = "ko";
	public String CHINESE_LANGUAGE_CODE = "zh-CN";
	public String HINDI_LANGUAGE_CODE ="hi";
	
	public ArrayList<String> countries;
	
	public Language() {
		countries = new ArrayList<String>(); 
     	countries.add("United Kingdom");
   		countries.add("Germany");
   		countries.add("Russia");
    	countries.add("Italy");
    	 // to get all countries
        //Locale[] locales = Locale.getAvailableLocales(); // get all locales available     
        /*
        for (Locale l : locales) {
        	String country = l.getDisplayCountry(); // get country name
        	if (country.trim().length() > 0 && !countries.contains(country)) { // country is not already in the array
        		countries.add(country); 
        	}
        }*/
	}
	
	
	
	
	public String getLanguageCode(String language) {
		
		if (language.equals("English")) return ENGLISH_LANGUAGE_CODE;
		else if (language.equals("Deutsch")) return GERMAN_LANGUAGE_CODE;
		else if (language.equals("Italiano")) return ITALIAN_LANGUAGE_CODE;
		else if (language.equals("Русский")) return RUSSIAN_LANGUAGE_CODE;
		else if (language.equals("Român")) return ROMANIAN_LANGUAGE_CODE;
		else if (language.equals("Norsk")) return NORWEGIAN_LANGUAGE_CODE;
		else if (language.equals("中国的")) return CHINESE_LANGUAGE_CODE;
		else if (language.equals("हिंदी")) return HINDI_LANGUAGE_CODE;
		else if (language.equals("한국의")) return SOUTHKOREAN_LANGUAGE_CODE;
		else return "";
	}
	
    public String getLanguageFromCode(String code) {
		
		if (code.equals(ENGLISH_LANGUAGE_CODE)) return "English";
		else if (code.equals(GERMAN_LANGUAGE_CODE)) return "Deutsch";
		else if (code.equals(ITALIAN_LANGUAGE_CODE)) return "Italiano";
		else if (code.equals(RUSSIAN_LANGUAGE_CODE)) return "Русский";
		else if (code.equals(ROMANIAN_LANGUAGE_CODE)) return "Român";
		else if (code.equals(NORWEGIAN_LANGUAGE_CODE)) return "Norsk";
		else if (code.equals(CHINESE_LANGUAGE_CODE)) return "中国的";
		else if (code.equals(HINDI_LANGUAGE_CODE)) return "हिंदी";
		else if (code.equals(SOUTHKOREAN_LANGUAGE_CODE)) return "한국의";
		else return "";
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
	
	
	
}
