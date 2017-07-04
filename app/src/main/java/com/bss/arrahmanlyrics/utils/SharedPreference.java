package com.bss.arrahmanlyrics.utils;

/**
 * Created by mohan on 7/5/17.
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.bss.arrahmanlyrics.models.Song;
import com.google.gson.Gson;

public class SharedPreference {

	public static final String PREFS_NAME = "PRODUCT_APP";
	public static final String FAVORITES = "Product_Favorite";

	public SharedPreference() {
		super();
	}

	// This four methods are used for maintaining favorites.
	public void saveFavorites(Context context, List<Song> favorites) {
		SharedPreferences settings;
		Editor editor;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = settings.edit();

		Gson gson = new Gson();
		String jsonFavorites = gson.toJson(favorites);

		editor.putString(FAVORITES, jsonFavorites);

		editor.commit();
	}

	public void addFavorite(Context context, Song product) {
		List<Song> favorites = getFavorites(context);
		if (favorites == null)
			favorites = new ArrayList<Song>();
		favorites.add(product);
		saveFavorites(context, favorites);
	}

	public void removeFavorite(Context context, Song product) {
		ArrayList<Song> favorites = getFavorites(context);
		if (favorites != null) {
			favorites.remove(product);
			saveFavorites(context, favorites);
		}
	}

	public ArrayList<Song> getFavorites(Context context) {
		SharedPreferences settings;
		List<Song> favorites;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);

		if (settings.contains(FAVORITES)) {
			String jsonFavorites = settings.getString(FAVORITES, null);
			Gson gson = new Gson();
			Song[] favoriteItems = gson.fromJson(jsonFavorites,
					Song[].class);

			favorites = Arrays.asList(favoriteItems);
			favorites = new ArrayList<Song>(favorites);
		} else
			return null;

		return (ArrayList<Song>) favorites;
	}
}