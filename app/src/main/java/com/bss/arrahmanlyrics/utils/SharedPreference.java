package com.bss.arrahmanlyrics.utils;

/**
 * Created by mohan on 7/5/17.
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;
import com.bss.arrahmanlyrics.mainApp;

import com.bss.arrahmanlyrics.models.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class SharedPreference {
	FirebaseUser user;
	DatabaseReference userRef;
	HashMap<String, Object> movies;
	HashMap<String, ArrayList<String>> Favorites;
	ArrayList<String> songlist;


	public static final String PREFS_NAME = "PRODUCT_APP";
	public static final String FAVORITES = "Product_Favorite";

	public SharedPreference() {
		super();
		if(mainApp.getUser() == null){
			user = FirebaseAuth.getInstance().getCurrentUser();
		}else {
			user = mainApp.getUser();
		}

		Log.e("user", String.valueOf(user.getUid()));
		songlist = new ArrayList<>();
		Favorites = new HashMap<>();
		userRef = FirebaseDatabase.getInstance().getReference();

		userRef.child(user.getUid()).child("Fav Songs").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				movies = (HashMap<String, Object>) dataSnapshot.getValue();
				Favorites.clear();
				if (movies != null) {
					for (String movie : movies.keySet()) {

						HashMap<String, Object> songs = (HashMap<String, Object>) movies.get(movie);

						songlist.clear();
						for (String song : songs.keySet()) {

							songlist.add(song);
						}

						Favorites.put(movie, (ArrayList<String>) songlist.clone());
					}


				}

			}


			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

	}

	/* This four methods are used for maintaining favorites.
	public void saveFavorites(List<Song> favorites) {
		/*SharedPreferences settings;
		Editor editor;

		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		editor = settings.edit();

		Gson gson = new Gson();
		String jsonFavorites = gson.toJson(favorites);

		editor.putString(FAVORITES, jsonFavorites);

		editor.commit();
		HashMap<String, Object> list = new HashMap<>();
		list.put("Fav Songs", favorites);
		userRef.child(user.getUid()).updateChildren(list);


	}*/

	public void addFavorite(String movieTitle, String songTitle) {

		if (Favorites.containsKey(movieTitle)) {
			if (Favorites.get(movieTitle).contains(songTitle)) {
				return;
			}
		} else {
			HashMap<String, Object> map = new HashMap<>();
			map.put(songTitle, songTitle);

			userRef.child(user.getUid()).child("Fav Songs").child(movieTitle).updateChildren(map);
		}

	}


	public void removeFavorite(String movieTitle, String songTitle) {
		Log.e("removeFavorite", songTitle + " " + movieTitle);
		if (Favorites.containsKey(movieTitle)) {
			if (Favorites.get(movieTitle).contains(songTitle)) {
				HashMap<String, Object> map = new HashMap<>();
				map.put(songTitle, songTitle);

				userRef.child(user.getUid()).child("Fav Songs").child(movieTitle).child(songTitle).removeValue(new DatabaseReference.CompletionListener() {
					@Override
					public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

					}
				});
			}
		}
	}

	public HashMap<String, ArrayList<String>> getFavorites() {
	/*	SharedPreferences settings;
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

		return (ArrayList<Song>) favorites;*/
		return Favorites;
	}
}