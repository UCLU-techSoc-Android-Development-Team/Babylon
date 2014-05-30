package com.techsoc.babylon.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.techsoc.babylon.MainActivity;

public class AddUser {
	
	public static void startDialog(final MainActivity context) {
		
		AlertDialog.Builder newUserDialog = new AlertDialog.Builder(context);

		newUserDialog.setTitle("Add People");
		newUserDialog.setMessage("What's your Name?");

		final LinearLayout listView = new LinearLayout(context);
		listView.setOrientation(LinearLayout.VERTICAL);

		final EditText input = new EditText(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		input.setLayoutParams(lp);
		listView.addView(input);

		final Spinner sprCoun = new Spinner(context);
		sprCoun.setLayoutParams(lp);
		List<String> colourList = new ArrayList<String>();
		colourList.add("blue");
		colourList.add("red");
		colourList.add("yellow");
		colourList.add("green");
		colourList.add("gray");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, colourList);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sprCoun.setAdapter(dataAdapter);
		listView.addView(sprCoun);

		newUserDialog.setView(listView);

		newUserDialog.setPositiveButton("Create",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {

						String userName = input.getText().toString();
						String userColour = sprCoun.getSelectedItem()
								.toString();

						context.addNewUser(userName, userColour);
					}
				});

		newUserDialog.show();
	}

}
