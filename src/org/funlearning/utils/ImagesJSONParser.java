package org.funlearning.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import org.funlearning.R;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;

public class ImagesJSONParser {
	private ArrayList<String> images;

	private static final Random RANDOM = new Random();

	public ImagesJSONParser(Context context) {
		images = new ArrayList<String>();

		try {

			JSONParser parser = new JSONParser();
			JSONArray arr = null;

			InputStream is = context.getResources().openRawResource(
					R.raw.images_json);
			JSONObject jsonObject = (JSONObject) parser
					.parse(new BufferedReader(new InputStreamReader(is)));

			try {
				Object j = jsonObject.get("images");
				arr = (JSONArray) j;
			} catch (Exception ex) {
			}

			if (arr != null) {
				for (Object t : arr) {

					images.add(((JSONObject) t).get("name").toString());
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getWords() {
		return images;
	}

	public String getRandomWord() {
		return images.get(RANDOM.nextInt(images.size()));
	}
	
	public String getAt(int index) {
		return images.get(index);
	}
	
	public int getRandomWordPos() {
		return RANDOM.nextInt(images.size());
	}
}
