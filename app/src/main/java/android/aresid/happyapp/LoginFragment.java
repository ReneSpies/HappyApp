package android.aresid.happyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment
		extends Fragment
		implements View.OnClickListener
{
	private static final String PREFERENCES_ID = "userID";
	private static final int RC_LOGIN = 13;
	private final String TAG = getClass().getSimpleName();
	private OnFragmentInteractionListener mFragmentInteractionListener;
	private EditText mEmailField;
	private EditText mPasswordField;
	private Button mLoginInButton;
	private Button mSignUpButton;
	private Button mGoogleLoginButton;
	private FirebaseAuth mFirebaseAuth;
	private GoogleSignInClient mGoogleSignInClient;




	public LoginFragment()
	{
		Log.d(TAG, "LoginFragment:true, empty constructor");
		// Required empty public constructor
	}




	public static LoginFragment newInstance()
	{
		Log.d("static", "newInstance:true");

		return new LoginFragment();
	}




	@Override
	public void onClick(View v)
	{
		Log.d(TAG, "onClick:true");

		switch (v.getId())
		{
			case R.id.login_login_button:
			{
				Log.d(TAG, "onClick:login_login_button");

				String email = mEmailField.getText()
				                          .toString();
				String password = mPasswordField.getText()
				                                .toString();

				// Check password and email validity.
				if (!isEmailCorrect(email))
				{
					Snackbar.make(v, getString(R.string.check_email), Snackbar.LENGTH_SHORT)
					        .show();
					return;
				}
				else if (!isPasswordCorrect(password))
				{
					Snackbar.make(v, getString(R.string.password_min_chars), Snackbar.LENGTH_SHORT)
					        .show();
					return;
				}

				// This method handles the signInWithEmailAndPassword.
				handleLogin(email, password);

				break;
			}
			case R.id.login_google_button:
			{
				Log.d(TAG, "onClick:login_google_button");

				// This method handles the login with Google account.
				handleGoogleLogin();

				break;
			}
			case R.id.login_sign_up_button:
			{
				Log.d(TAG, "onClick:login_sign_up_button");

				// All the SignUp things will be handled in the SignUpFragment
				String email = mEmailField.getText()
				                          .toString();

				mFragmentInteractionListener.displaySignUpFragment(email);

				break;
			}
		}
	}




	/**
	 * Method validates the email correctness.
	 *
	 * @param email The email to obtain.
	 * @return Boolean if email is valid or not.
	 */
	private boolean isEmailCorrect(String email)
	{
		Log.d(TAG, "isEmailCorrect:true");
		return (email.length() != 0 && email.indexOf('@') != -1);
	}




	/**
	 * Method validates the password correctness.
	 *
	 * @param password The password to obtain.
	 * @return Boolean if password is fulfills the password requirements.
	 */
	private boolean isPasswordCorrect(String password)
	{
		Log.d(TAG, "isPasswordCorrect:" + (password.length() > 6));
		return password.length() > 6;
	}




	/**
	 * Method handles the signInWithEmailAndPassword.
	 *
	 * @param email    The email to sign in with.
	 * @param password The password to sign in with.
	 */
	private void handleLogin(String email, String password)
	{
		Log.d(TAG, "handleLogin:true");

		mFirebaseAuth.signInWithEmailAndPassword(email, password)
		             .addOnSuccessListener(authResult ->
		                                   {
			                                   Log.d(TAG, "onSuccess:true");

			                                   // updateUI handles login procedures.
			                                   updateUI(authResult.getUser(), null);
		                                   })
		             .addOnFailureListener(e ->
		                                   {
			                                   // TODO: Update exception handling!
			                                   Log.e(TAG, "onFailure: ", e);

			                                   if (e instanceof com.google.firebase.auth.FirebaseAuthInvalidUserException)
			                                   {
				                                   Toast.makeText(getActivity(), "No user found under this email or password", Toast.LENGTH_SHORT)
				                                        .show();
			                                   }
		                                   });
	}




	/**
	 * Method handles the sign in with an existing Google account.
	 */
	private void handleGoogleLogin()
	{
		Log.d(TAG, "handleGoogleLogin:true");

		Intent loginIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(loginIntent, RC_LOGIN);
	}




	/**
	 * Method handles all the login and sign up procedures that needs UI updates.
	 * Only one param cannot be null.
	 *
	 * @param user    FirebaseUser if the user logs in with this.
	 * @param account Google account if the user logs in with this.
	 */
	private void updateUI(FirebaseUser user, GoogleSignInAccount account)
	{
		Log.d(TAG, "updateUI:true");

		if (user != null)
		{
			Log.d(TAG, "updateUI:user != null");

			// Final because it's accessed within inner class.
			final FirebaseUser USER = user;

			// Method displays the loading screen.
			displayProgressBar();

			// Reload the user before because the account info is cached offline.
			user.reload()
			    .addOnCompleteListener(task ->
			                           {
				                           // If email is verified, login.
				                           if (USER.isEmailVerified())
				                           {
					                           Log.d(TAG, "isEmailVerified:" + USER.isEmailVerified());

				                           }
				                           else
				                           {
					                           Log.d(TAG, "isEmailVerified:" + USER.isEmailVerified());

					                           // Email is not yet verified, email verification screen.
					                           mFragmentInteractionListener.displayEmailVerificationFragment(USER);
				                           }
			                           });
		}
		else if (account != null)
		{
			// TODO: Handle Google login
			Log.d(TAG, "updateUI:account != null");
		}
		else
		{
			// User && Account == null
		}
	}




	/**
	 * Method shows the progress bar and makes the rest content invisible.
	 */
	private void displayProgressBar()
	{
		Log.d(TAG, "displayProgressBar:true");
		// TODO: Move this to activity level.

		// Getting instances.
		Activity activity = getActivity();
		LinearLayout loginLayout, googleLayout, signUpLayout;
		loginLayout = activity.findViewById(R.id.login_linear_layout);
		googleLayout = activity.findViewById(R.id.google_linear_layout);
		signUpLayout = activity.findViewById(R.id.sign_up_linear_layout);
		ProgressBar progressBar = activity.findViewById(R.id.progress_bar);
		TextView statusInfoTextView = activity.findViewById(R.id.login_text_view);
		Button loginButton = activity.findViewById(R.id.login_login_button);

		// Setting invisible first so in case of long loading times there are no views overlapping.
		// Setting invisible.
		loginLayout.setVisibility(View.INVISIBLE);
		googleLayout.setVisibility(View.INVISIBLE);
		signUpLayout.setVisibility(View.INVISIBLE);
		loginButton.setVisibility(View.INVISIBLE);

		// Setting visible.
		progressBar.setVisibility(View.VISIBLE);
		statusInfoTextView.setVisibility(View.VISIBLE);
	}




	/**
	 * Method fetches user's Firestore ID from SharedPrefs.
	 *
	 * @return String containing user's Firestore ID.
	 */
	private String getUserIDFromSharedPreferences()
	{
		Log.d(TAG, "getUserIDFromSharedPreferences:true");
		// TODO: Move to activity level.

		SharedPreferences userIDPrefs = getActivity().getSharedPreferences("android.aresid.happyapp", Context.MODE_PRIVATE);
		return userIDPrefs.getString(PREFERENCES_ID, null);
	}




	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d(TAG, "onActivityResult:true");

		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching then Intent from GoogleSignInIntent.getSignInIntent().
		if (requestCode == RC_LOGIN)
		{
			// The task returned by this is always completed, no need to register a listener.
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

			// Method handles the sign in with Google account.
			handleLoginResult(task);
		}
	}




	/**
	 * Method handles the sign in with Google account.
	 *
	 * @param task The task that emerged from the Google sign in procedure.
	 */
	private void handleLoginResult(Task<GoogleSignInAccount> task)
	{
		Log.d(TAG, "handleLoginResult:true");

		try
		{
			GoogleSignInAccount account = task.getResult(ApiException.class);

			// Signed in successfully, show authenticated UI.
			updateUI(null, account);
		}
		catch (ApiException e)
		{
			// The ApiException status code indicates the detailed failure reason.
			Log.e(TAG, "handleLoginResult: ", e);
			Log.w(TAG, "handleLoginResult:error code:" + e.getStatusCode());

			// Call this with null params to handle exceptions there.
			updateUI(null, null);
		}
	}




	@Override
	public void onAttach(Context context)
	{
		Log.d(TAG, "onAttach:true");

		super.onAttach(context);
		if (context instanceof OnFragmentInteractionListener)
		{
			mFragmentInteractionListener = (OnFragmentInteractionListener) context;
		}
		else
		{
			throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
		}
	}




	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreate:true");
		super.onCreate(savedInstanceState);

		// Grab a new FirebaseAuth instance
		mFirebaseAuth = FirebaseAuth.getInstance();

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
		                                                                                              .build();

		// TODO: Move to activity level.
		// Build a GoogleSignInClient with the options specified by gso.
		mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
	}




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView:true");

		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_login, container, false);

		// Init all the views for this fragment
		mEmailField = rootView.findViewById(R.id.login_email_field);
		mLoginInButton = rootView.findViewById(R.id.login_login_button);
		mSignUpButton = rootView.findViewById(R.id.login_sign_up_button);
		mGoogleLoginButton = rootView.findViewById(R.id.login_google_button);
		mPasswordField = rootView.findViewById(R.id.login_password_field);

		// Calling the onClick handler
		mLoginInButton.setOnClickListener(this);
		mSignUpButton.setOnClickListener(this);
		mGoogleLoginButton.setOnClickListener(this);

		return rootView;
	}




	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart:true");
		super.onStart();

		// Get current user instance
		FirebaseUser user = mFirebaseAuth.getCurrentUser();

		// TODO: Move to activity level.
		// Check for existing Google Sign In account, if the user is already signed in
		// the GoogleSignInAccount will be non-null.
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

		updateUI(user, account);
	}




	@Override
	public void onResume()
	{
		Log.d(TAG, "onResume:true");
		super.onResume();
	}




	@Override
	public void onPause()
	{
		Log.d(TAG, "onPause:true");
		super.onPause();
	}




	@Override
	public void onStop()
	{
		Log.d(TAG, "onStop:true");
		super.onStop();
	}




	@Override
	public void onDetach()
	{
		Log.d(TAG, "onDetach:true");

		super.onDetach();
		mFragmentInteractionListener = null;
	}




	public interface OnFragmentInteractionListener
	{
		void displayEmailVerificationFragment(FirebaseUser user);


		void displaySignUpFragment(String email);



		void startMainActivity(FirebaseUser user, GoogleSignInAccount account, String userID);







	}







}
