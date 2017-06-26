package com.bss.arrahmanlyrics.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bss.arrahmanlyrics.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OtherLyrics.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OtherLyrics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherLyrics extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DatabaseReference songRef;
    HashMap<String, Object> selectedSong;
    TextView lyricsText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OtherLyrics() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherLyrics.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherLyrics newInstance(String param1, String param2) {
        OtherLyrics fragment = new OtherLyrics();
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
        View view = inflater.inflate(R.layout.fragment_other_lyrics,container,false);
        lyricsText = (TextView) view.findViewById(R.id.lyricsOthers) ;
        songRef = FirebaseDatabase.getInstance().getReference();
        songRef.child("AR Rahman").child("Tamil").child(getActivity().getIntent().getExtras().getString("Title")).child(getActivity().getIntent().getExtras().getString("SongTitle")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                selectedSong = (HashMap<String, Object>) dataSnapshot.getValue();
                Log.i("Selected Song", String.valueOf(selectedSong));
                setLyrics();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
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

    private void setLyrics() {

        final StringBuilder builderOther = new StringBuilder();
        builderOther.append(selectedSong.get("Others"));
        builderOther.append(selectedSong.get("OthersOne"));

        Typeface tamil = Typeface.createFromAsset(getActivity().getAssets(),"english.ttf");

        lyricsText.setText(String.valueOf(builderOther));
        lyricsText.setTypeface(tamil);
    }
}
