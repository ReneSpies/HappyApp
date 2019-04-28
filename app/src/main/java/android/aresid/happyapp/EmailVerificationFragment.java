package android.aresid.happyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailVerificationFragment
		extends Fragment
		implements View.OnClickListener
{
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_FIREBASE_USER = "firebaseUser";
	private static final String PREFERENCES_ID = "userID";
	private static final String FIRESTORE_FIRST_NAME = "firstName";
	private static final String FIRESTORE_SURNAME = "surname";
	private static final String FIRESTORE_EMAIL = "email";
	private final String TAG = getClass().getSimpleName();
	private Context mContext;
	private FirebaseUser mFirebaseUser;
	private Button mBackButton;
	private Button mSendEmailAgainButton;
	private OnFragmentInteractionListener mFragmentInteractionListener;

	public EmailVerificationFragment()
	{
		// Required empty public constructor
	}

	public static EmailVerificationFragment newInstance(FirebaseUser user)
	{
		EmailVerificationFragment emailFragment = new EmailVerificationFragment();

		Bundle args = new Bundle();
		args.putParcelable(ARG_FIREBASE_USER, user);
		emailFragment.setArguments(args);

		return emailFragment;
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

		this.mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Init FirebaseUser and send him the verification email later
		if (getArguments() != null)
		{
			mFirebaseUser = getArguments().getParcelable(ARG_FIREBASE_USER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView:true");

		View rootView = inflater.inflate(R.layout.fragment_email_verification, container, false);

		// Init all the views for this fragment
		mBackButton = rootView.findViewById(R.id.emailverification_back_button);
		mSendEmailAgainButton = rootView.findViewById(R.id.emailverification_send_again_button);

		// Add onClick listener to the buttons
		mBackButton.setOnClickListener(this);
		mSendEmailAgainButton.setOnClickListener(this);

		// Inflate the layout for this fragment
		return rootView;
	}

	@Override
	public void onStart()
	{
		Log.d(TAG, "onStart:true");
		super.onStart();

		updateUI(mFirebaseUser);
	}

	@Override
	public void onDetach()
	{
		Log.d(TAG, "onDetach:true");
		super.onDetach();
		mFragmentInteractionListener = null;
	}

	private void updateUI(FirebaseUser user)
	{
		Log.d(TAG, "updateUI:true");
		if (user != null)
		{
			final FirebaseUser fUser = user;
			// Reload the user's information
			user.reload()
			    .addOnCompleteListener(new OnCompleteListener<Void>()
			    {
				    @Override
				    public void onComplete(@NonNull Task<Void> task)
				    {
					    Log.d(TAG, "onComplete:true");
					    Log.e(TAG, "onComplete: ", task.getException());

					    if (fUser.isEmailVerified())
					    {
						    mFragmentInteractionListener.displayLoginFragment();
					    }
					    else
					    {
						    sendEmailVerification();
					    }
				    }
			    });
		}
		else
		{
			// TODO: Implement user = null handling!
		}
	}


	private void sendEmailVerification()
	{
		Log.d(TAG, "sendEmailVerification:true");

		// Send verification Email
		final FirebaseUser user = mFirebaseUser;

		Log.d(TAG, "sendEmailVerification: " + user.getEmail());

		user.sendEmailVerification()
		    .addOnSuccessListener(new OnSuccessListener<Void>()
		    {
			    @Override
			    public void onSuccess(Void aVoid)
			    {
				    Log.d(TAG, "onSuccess:true");

				    Toast.makeText(mContext, "Verification Email sent to " + user.getEmail(), Toast.LENGTH_SHORT)
				         .show();
			    }
		    })
		    .addOnFailureListener(new OnFailureListener()
		    {
			    @Override
			    public void onFailure(@NonNull Exception e)
			    {
				    // TODO: onFailure handling!
				    Log.e(TAG, "onFailure:true", e);

				    if (e instanceof com.google.firebase.FirebaseTooManyRequestsException)
				    {
					    Toast.makeText(mContext, "Hey Flash, not so fast. Try again in a minute.", Toast.LENGTH_SHORT)
					         .show();
					    return;
				    }
				    else
				    {
					    Toast.makeText(getContext(), getString(R.string.verification_email_fail), Toast.LENGTH_LONG)
					         .show();
				    }
			    }
		    });
	}

	@Override
	public void onClick(View v)
	{
		Log.d(TAG, "onClick:true");

		switch (v.getId())
		{
			case R.id.emailverification_back_button:
			{
				Log.d(TAG, "onClick:emailverification_back_button");
				onBackButtonClick();
				break;
			}

			case R.id.emailverification_send_again_button:
			{
				Log.d(TAG, "onClick:emailverification_send_again_button");
				sendEmailVerification();
				break;
			}
		}
	}

	private void onBackButtonClick()
	{
		Log.d(TAG, "onBackButtonClick:true");
		fetchUserIncredentialsFromFirestore();
	}

	private void fetchUserIncredentialsFromFirestore()
	{
		Log.d(TAG, "fetchUserIncredentialsFromFirestore:true");

		String firestoreID = getIDFromSharedPreferences();
		FirebaseFirestore db = FirebaseFirestore.getInstance();

		Log.d(TAG, "fetchUserIncredentialsFromFirestore: id " + firestoreID);

		db.collection("users")
		  .document(firestoreID)
		  .get()
		  .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
		  {
			  @Override
			  public void onComplete(@NonNull Task<DocumentSnapshot> task)
			  {
				  Log.d(TAG, "isSuccessful:" + task.isSuccessful());
				  String firstName = task.getResult()
				                         .getString(FIRESTORE_FIRST_NAME);
				  String surname = task.getResult()
				                       .getString(FIRESTORE_SURNAME);
				  String email = task.getResult()
				                     .getString(FIRESTORE_EMAIL);

				  mFragmentInteractionListener.displaySignUpFragment(firstName, surname, email);
			  }
		  });
	}

	private String getIDFromSharedPreferences()
	{
		return getActivity().getSharedPreferences("android.aresid.happyapp", Context.MODE_PRIVATE)
		                    .getString(PREFERENCES_ID, null);
	}


	public interface OnFragmentInteractionListener
	{
		void displayLoginFragment();

		void displaySignUpFragment(String firstName, String surname, String email);







	}







}
