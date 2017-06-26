package com.bss.arrahmanlyrics.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bss.arrahmanlyrics.R;
import com.bss.arrahmanlyrics.activites.albumSongList;
import com.bss.arrahmanlyrics.adapter.albumAdapter;
import com.bss.arrahmanlyrics.models.Album;
import com.bss.arrahmanlyrics.utils.RecyclerItemClickListener;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link albums.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link albums#newInstance} factory method to
 * create an instance of this fragment.
 */
public class albums extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private albumAdapter albumListAdapter;
    private FastScrollRecyclerView rv;
    private OnFragmentInteractionListener mListener;
    ProgressDialog dialog;
    DatabaseReference albumData;
    HashMap<String, Object> values;
    private List<Album> albumList;
    Toolbar toolbar;
    View rootView;

    public albums() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment albums.
     */
    // TODO: Rename and change types and number of parameters
    public static albums newInstance(String param1, String param2) {
        albums fragment = new albums();
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
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading Album");
        dialog.show();
        albumList = new ArrayList<>();
        albumListAdapter = new albumAdapter(getContext(), albumList);
        rv = (FastScrollRecyclerView) rootView.findViewById(R.id.albumrv);
        rv.setAdapter(albumListAdapter);
        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                
            }
        });
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Album selectedAlbum = albumList.get(position);
                Bundle songs = new Bundle();

                //songs.putSerializable("map",(HashMap<String,Object>)values.get(selectedAlbum.getName()));
                songs.putString("Title",selectedAlbum.getName());
                //songs.putByteArray("image",albumList.get(position).getThumbnail());
                try{
                    Intent intent = new Intent(getContext(),albumSongList.class);
                    intent.putExtras(songs);
                    startActivity(intent);
                }catch (Exception e){
                    Log.i("Exception",e.getMessage());
                }

            }
        }));
        loadGridView(2);
        albumData = FirebaseDatabase.getInstance().getReference();
        albumData.child("AR Rahman").child("Tamil").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                values = (HashMap<String, Object>) dataSnapshot.getValue();

                prepareAlbums();
                dialog.hide();



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //prepareAlbums();

        return rootView;
    }

   /* private void setBackgrounds() {
        View mainwindow = getActivity().findViewById(android.R.id.content).getRootView();
        mainwindow.setDrawingCacheEnabled(true);

        Bitmap image = mainwindow.getDrawingCache();
        Bitmap croppedBitmap = Bitmap.createBitmap(image, 0, (int) (image.getHeight() * 0.8), image.getWidth(), 40);
        try {
            Palette.from(croppedBitmap).maximumColorCount(1600000).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    // Get the "vibrant" color swatch based on the bitmap

                    if (palette.getDarkVibrantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getDarkVibrantSwatch().getRgb());
                    } else if (palette.getDominantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getDominantSwatch().getRgb());
                    } else if (palette.getDarkMutedSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getDarkMutedSwatch().getRgb());
                    } else if (palette.getVibrantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getVibrantSwatch().getRgb());

                    } else if (palette.getLightVibrantSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getLightVibrantSwatch().getRgb());   //holder.count.setTextColor(palette.getMutedSwatch().getBodyTextColor());

                    } else if (palette.getLightMutedSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getLightMutedSwatch().getRgb());//holder.count.setTextColor(palette.getMutedSwatch().getBodyTextColor());
                    } else if (palette.getMutedSwatch() != null) {
                        toolbar.setBackgroundColor(palette.getMutedSwatch().getRgb());
                    }


                }
            });


        } catch (Exception e) {

        }
    }*/

    private void prepareAlbums() {
        albumList.clear();
        for (String album : values.keySet()) {
            albumList.add(new Album(album, ((HashMap<String, Object>) values.get(album)).size() - 1,
                    getImage(String.valueOf(((HashMap<String, Object>) values.get(album)).get("IMAGE"))), getBitmap(String.valueOf(((HashMap<String, Object>) values.get(album)).get("IMAGE")))));
        }
        albumListAdapter.notifyDataSetChanged();


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
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog.dismiss();
    }

    public Bitmap getBitmap(String imageString) {
        if (imageString.equals(null)) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            return bitmap;
        }
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }
    private void loadGridView(int columns) {

            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columns);
            rv.setLayoutManager(layoutManager);

    }
}
