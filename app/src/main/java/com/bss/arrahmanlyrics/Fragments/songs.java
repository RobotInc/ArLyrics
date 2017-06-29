package com.bss.arrahmanlyrics.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bss.arrahmanlyrics.R;
import com.bss.arrahmanlyrics.adapter.fragmentSongAdapter;
import com.bss.arrahmanlyrics.adapter.mainFragmentSongAdapter;
import com.bss.arrahmanlyrics.models.slideSong;
import com.bss.arrahmanlyrics.utils.CustomLayoutManager;
import com.bss.arrahmanlyrics.utils.DividerItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link songs.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link songs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class songs extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	FastScrollRecyclerView songlistView;
	List<slideSong> songlist;
	HashMap<String, Object> values;
	DatabaseReference songref;
	mainFragmentSongAdapter adapter;
	ProgressDialog dialog;

	private OnFragmentInteractionListener mListener;

	public songs() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment songs.
	 */
	// TODO: Rename and change types and number of parameters
	public static songs newInstance(String param1, String param2) {
		songs fragment = new songs();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		//return inflater.inflate(R.layout.fragment_songs, container, false);
		View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
		dialog = new ProgressDialog(getContext());
		dialog.setMessage("Loading Album");
		dialog.show();
		songlist = new ArrayList<>();
		adapter = new mainFragmentSongAdapter(getContext(), songlist);
		songlistView = (FastScrollRecyclerView) rootView.findViewById(R.id.songPlayList);
		songlistView.setAdapter(adapter);
		final CustomLayoutManager customLayoutManager = new CustomLayoutManager(getContext());
		customLayoutManager.setSmoothScrollbarEnabled(true);
		songlistView.setLayoutManager(customLayoutManager);

		songlistView.addItemDecoration(new DividerItemDecoration(getContext(), 75, false));
		songref = FirebaseDatabase.getInstance().getReference();

		songref.child("AR Rahman").child("Tamil").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				values = (HashMap<String, Object>) dataSnapshot.getValue();

				prepareSongList();
				adapter.notifyDataSetChanged();
				dialog.hide();
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		return rootView;
	}

	private void prepareSongList() {
		songlist.clear();
		List<slideSong> list = new ArrayList<>();
		SortedSet<String> trackNos = new TreeSet<>();
		for (String albums : values.keySet()) {
			HashMap<String, Object> songs = (HashMap<String, Object>) values.get(albums);
			byte[] image = getImage(String.valueOf(songs.get("IMAGE")));
			for (String song : songs.keySet()) {
				if (!song.equals("IMAGE")) {
					HashMap<String, Object> oneSong = (HashMap<String, Object>) songs.get(song);
					slideSong newSong = new slideSong(song, oneSong.get("Track NO").toString(), oneSong.get("Lyricist").toString(),image);
					list.add(newSong);
					trackNos.add(song);
				}

			}
		}


		for (String Track : trackNos) {
			for (slideSong songNo : list) {
				if (songNo.getSongName().equals(Track)) {
					songlist.add(songNo);
				}
			}

		}
		adapter.notifyDataSetChanged();

	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}




	private byte[] getImage(String imageString) {
		if (imageString.equals(null)) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			byte[] bitMapData = stream.toByteArray();
			return bitMapData;

		}
		byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
		//Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		return decodedString;
	} @Override
	public void onPause() {
		super.onPause();
		dialog.dismiss();
	}


}
