package android.aresid.happyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;


public class LegalFragment
		extends Fragment
		implements Button.OnClickListener
{
	private static final String ARG_USER = "firebaseUser";
	private static final String ARG_ACCOUNT = "googleSignInAccount";
	private static final String ARG_USER_ID = "userID";
	private final String TAG = getTag();
	private OnFragmentInteractionListener mFragmentInteractionListener;
	private FirebaseUser mFirebaseUser;
	private ListView mTermsAndConditionsListView;
	private ViewGroup mContainer;

	public LegalFragment()
	{
		Log.d(TAG, "LegalFragment:empty");
		// Required empty public constructor
	}

	public static LegalFragment newInstance(FirebaseUser user, GoogleSignInAccount account, String userID)
	{
		Log.d("static", "newInstance:true");
		LegalFragment fragment = new LegalFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_USER, user);
		args.putParcelable(ARG_ACCOUNT, account);
		args.putString(ARG_USER_ID, userID);
		fragment.setArguments(args);
		return fragment;
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
		if (getArguments() != null)
		{
			mFirebaseUser = getArguments().getParcelable(ARG_USER);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.d(TAG, "onCreateView:true");

		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_legal, container, false);
		mContainer = rootView.findViewById(R.id.sign_up_fragment);

		//		displayPrivacyPolicyDialog();

		// Init all the views for this fragment
		Button confirmButton = rootView.findViewById(R.id.legal_confirm_button);

		confirmButton.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.d(TAG, "onActivityCreated:true");
		super.onActivityCreated(savedInstanceState);

		//		// Test-populate the ListView
		//		List<String> legalListViewContent = new ArrayList<>();
		//		legalListViewContent.add("§ 1 We don't take any responsibility.");
		//		legalListViewContent.add("§ 2 We only want money.");
		//		legalListViewContent.add("§ 3 If you have any questions, keep it for you.");
		//		legalListViewContent.add("§ 5 jfdsklöfsdjkläösdfajklsdfajklsdfa");
		//
		//		ArrayAdapter<String> legalListViewAdapter = new ArrayAdapter<>(getContext(), R.layout.item_legalities, legalListViewContent);
		//
		//		mTermsAndConditionsListView.setAdapter(legalListViewAdapter);
	}

	@Override
	public void onDetach()
	{
		Log.d(TAG, "onDetach:true");
		super.onDetach();
		mFragmentInteractionListener = null;
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.legal_confirm_button)
		{
			if (getArguments() != null)
			{
				FirebaseUser user = getArguments().getParcelable(ARG_USER);
				GoogleSignInAccount account = getArguments().getParcelable(ARG_ACCOUNT);
				String userID = getArguments().getString(ARG_USER_ID);

				mFragmentInteractionListener.startMainActivity(user, account, userID);
			}
			else
			{
				Log.d(TAG, "onClick:getArguments() == null");
			}
		}
	}

	public interface OnFragmentInteractionListener
	{

		void startMainActivity(FirebaseUser user, GoogleSignInAccount account, String userID);

		void displayPrivacyPolicyDialog();







	}







}
