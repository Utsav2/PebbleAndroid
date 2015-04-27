package utsav2.researchproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

class RecordingListAdapter extends ArrayAdapter<JSONObject> {

    MainActivity mActivity;

    public RecordingListAdapter(MainActivity activity) {

        super(activity, R.layout.list_item);

        mActivity = activity;

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // convertView has not been inflated; inflate it
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item, parent, false);

        }

        // convertView has been inflated
        TextView tv = (TextView) convertView.findViewById(R.id.textView1);

        Button deleteButton = (Button)convertView.findViewById(R.id.deleteButton);

        Button playButton = (Button)convertView.findViewById(R.id.playListButton);

        playButton.setVisibility(View.VISIBLE);

        /*TextView tv3 = (TextView) convertView.findViewById(R.id.sideTextView);

        TextView tv4 = (TextView) convertView.findViewById(R.id.changeTextView);

        TextView tv5 = (TextView) convertView.findViewById(R.id.timeTextView); */

        try{
            JSONObject object = getItem(position);
            tv.setText(object.getString("word"));
            if(!object.isNull("recording")){
                playButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.playRecording(position);
                    }
                });
            }
            else{
                playButton.setVisibility(View.INVISIBLE);
            }
        }
        catch (JSONException e){
            Log.e("JSON", "Got incorrectly formatted stuff in List Adapter");
        }

        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.deleteItem(position);
                mActivity.onRefresh();
            }
        });

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

}