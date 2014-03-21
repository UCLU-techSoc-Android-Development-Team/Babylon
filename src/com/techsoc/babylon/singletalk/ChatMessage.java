package com.techsoc.babylon.singletalk;

import java.util.Calendar;

public class ChatMessage {

	private String author;
	private String message;
	private String messageLanguage;
	private Calendar messageTime;
	private String boxColour;
	private boolean leftPosition;

	public ChatMessage(boolean left, String message, String messageLanguage,
			String author, Calendar messageTime, String boxColour) {

		this.leftPosition = left;
		this.message = message;
		this.messageLanguage = messageLanguage;
		this.author = author;
		this.messageTime = messageTime;
		this.boxColour = boxColour;
	}

	public void setNewAttr(boolean left, String message,
			String messageLanguage, String author, Calendar messageTime, String boxColour) {

		this.leftPosition = left;
		this.message = message;
		this.messageLanguage = messageLanguage;
		this.author = author;
		this.messageTime = messageTime;
		this.boxColour = boxColour;
	}
	public void setPosition(boolean left){
		this.leftPosition = left;
	}

	public String getText() {
		return this.message;
	}

	public String getLanguage() {
		return this.messageLanguage;
	}

	public boolean getPosition() {
		return this.leftPosition;
	}

	public String getAuthor() {
		return this.author;
	}

	public Calendar getCalendarTime() {
		return this.messageTime;
	}
	
	public String getBoxColour() {
		return this.boxColour;
	}
	
	public String getStringTime() {

		Integer seconds = messageTime.get(Calendar.SECOND);
		Integer minutes = messageTime.get(Calendar.MINUTE);
		Integer hours = messageTime.get(Calendar.HOUR_OF_DAY);
		
		String stringSeconds = seconds.toString();
		if (seconds.equals(0)) {	
		   stringSeconds = stringSeconds+"0";
		}
		
		String stringMinutes = minutes.toString();
		if (minutes.equals(0)) {	
			stringMinutes = stringMinutes+"0";
		}
		
		String stringHours = hours.toString();
		if (hours.equals(0)) {	
			stringHours = stringHours+"0";
		}
		return stringHours + ":" + stringMinutes + ":" + stringSeconds;
	}

	public int getIntTime() {

		String formatStringTime = getStringTime();
		String stringTime = "";
		
		for (int i = 0; i < formatStringTime.length(); i++) {
			if (formatStringTime.charAt(i)!=':') {
				stringTime += formatStringTime.charAt(i);
			}
		}
		
		return Integer.parseInt(stringTime);
	}

}