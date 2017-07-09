package com.bss.arrahmanlyrics.activites;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bss.arrahmanlyrics.Fragments.favorites;
import com.bss.arrahmanlyrics.Fragments.albums;
import com.bss.arrahmanlyrics.Fragments.songs;
import com.bss.arrahmanlyrics.R;
import com.bss.arrahmanlyrics.mainApp;
import com.bss.arrahmanlyrics.utils.RoundedTransformation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
	GoogleApiClient mGoogleApiClient;
	private FirebaseAuth mFirebaseAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;
	ImageView profileImage;
	TextView userName, userEmailId;
	public HashMap<String, Object> values = new HashMap<>();
	DatabaseReference data;
	Toolbar toolbar;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	ProgressDialog dialog;
	public FirebaseUser user;
	HashMap<String, Object> movies;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);


		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();


		mFirebaseAuth = FirebaseAuth.getInstance();
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

					}
				} /* OnConnectionFailedListener */)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

		user = mFirebaseAuth.getCurrentUser();


		if (user != null) {
			initUI();
		} else {
			signIn();
		}


	}


	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}


	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_log) {
			mFirebaseAuth.signOut();
			Auth.GoogleSignInApi.signOut(mGoogleApiClient);
			signIn();
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == 1) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		} else {
			Toast.makeText(getApplicationContext(), "App needs you to login to work smoothly open the app again and login with google account", Toast.LENGTH_LONG).show();
		}
	}

	private void handleSignInResult(GoogleSignInResult result) {
		Log.d("Sign In", "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			Log.i("user Name", acct.getDisplayName());


			firebaseAuthWithGoogle(acct);


		} else {
			Toast.makeText(getApplicationContext(), "App needs you to login to work smoothly open the app again and login with google account", Toast.LENGTH_LONG).show();

			finish();
		}
	}

	private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
		Log.d("Sign in", "firebaseAuthWithGoogle:" + acct.getId());

		final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mFirebaseAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						Log.d("sign in", "signInWithCredential:onComplete:" + task.isSuccessful());
						//String pic = acct.getPhotoUrl().toString();
						//Toast.makeText(getApplicationContext(),pic,Toast.LENGTH_SHORT).show();
						// Picasso.with(getApplicationContext()).load(pic).into(profileImage);
						// If sign in fails, display a message to the user. If sign in succeeds
						// the auth state listener will be notified and logic to handle the
						// signed in user can be handled in the listener.
						user = mFirebaseAuth.getCurrentUser();
						initUI();
						userEmailId.setText(user.getEmail());
						if (!task.isSuccessful()) {
							Log.w("Sign in", "signInWithCredential", task.getException());
							Toast.makeText(MainActivity.this, "Authentication failed. Please Check your Internet Connection and Open the app again...",
									Toast.LENGTH_LONG).show();
							finish();
						}
						// ...
					}
				});
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			// Log exception
			return null;
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentTitleList = new ArrayList<>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentTitleList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentTitleList.get(position);
		}
	}

	public void signIn() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, 1);
	}


	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public HashMap<String, Object> getValues() {
		return values;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dialog.dismiss();
	}

	void initUI() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("Loading Database");
		dialog.show();
		data = FirebaseDatabase.getInstance().getReference();
		data.child("AR Rahman").child("Tamil").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				values = (HashMap<String, Object>) dataSnapshot.getValue();
				mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
				mViewPager = (ViewPager) findViewById(R.id.container);
				mViewPager.setOffscreenPageLimit(2);

				mSectionsPagerAdapter.addFragment(new albums(), "Albums");
				mSectionsPagerAdapter.addFragment(new songs(), "Songs");
				mSectionsPagerAdapter.addFragment(new favorites(), "Favorite Songs");

				mViewPager.setAdapter(mSectionsPagerAdapter);
				// Set up the ViewPager with the sections adapter.

				TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
				tabLayout.setupWithViewPager(mViewPager);
				dialog.hide();

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		View view = navigationView.getHeaderView(0);
		userEmailId = (TextView) view.findViewById(R.id.email);
		userEmailId.setText(user.getEmail());

		Typeface english = Typeface.createFromAsset(getResources().getAssets(), "english.ttf");
		userEmailId.setTypeface(english);
	}
}
