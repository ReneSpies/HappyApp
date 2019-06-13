package android.aresid.happyapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created on: 19.05.2019
 * For Project: HappyApp
 * Author: René Spies
 * Copyright: © 2019 Ares ID
 */
public class LegalitiesAdapter
		extends ArrayAdapter<Legalities> {

	private static final String  TAG = "LegalitiesAdapter";
	private              Context mContext;
	private              int     mResource;

	/**
	 * Constructor.
	 *
	 * @param context          Caller's context.
	 * @param resource         Layout to inflate.
	 * @param listOfLegalities Content to use.
	 */
	public LegalitiesAdapter(@NonNull Context context, int resource, @NonNull List<Legalities> listOfLegalities) {

		super(context, resource, listOfLegalities);
		Log.d(TAG, "LegalitiesAdapter:true");
		mContext = context;
		mResource = resource;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

		Log.d(TAG, "getView:true");
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(mResource, parent, false);
		TextView title = convertView.findViewById(R.id.legalities_list_view_title);
		TextView content = convertView.findViewById(R.id.legalities_list_view_content);
		Log.d(TAG, "getView: title = " + getItem(position).getTitle());
		Log.d(TAG, "getView: content = " + getItem(position).getContent());
		title.setText(getItem(position).getTitle());
		content.setText(getItem(position).getContent());
		return convertView;
	}
}
