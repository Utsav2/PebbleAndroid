package utsav2.researchproject;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int DATA_KEY = 0;
    private static final int SELECT_BUTTON_KEY = 0;
    private static final int UP_BUTTON_KEY = 1;
    private static final int DOWN_BUTTON_KEY = 2;

    private static final int BUFFER_LENGTH = 128;


    private RecordingListAdapter mAdapter;
    private ListView listView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecordingSerializer recordingSerializer;
    private ResearchDataHandler researchDataHandler;

    private TextToSpeech ttobj;

    private ArrayList<JSONObject> items;

    private static final int ADD_KEY = 0x0;
    private static final int DELETE_KEY = 0x1;
    private static final int DELETE_ALL = 0x2;


    //Use the same UUID as on the watch

    public static final UUID APP_UUID = UUID.fromString("6b65b8d3-b4f9-4026-a4e7-cfacd4c1d8e3");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        PebbleKit.startAppOnPebble(getApplicationContext(), APP_UUID);
        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        setUpView();
        MyApplication myapp = (MyApplication)getApplicationContext();
        myapp.mainActivity = this;
        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());
        Toast.makeText(this, "Connected - " + Boolean.toString(connected), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish(){
        recordingSerializer.setData(items);
        super.finish();
    }

    private void setUpView(){
        mAdapter = new RecordingListAdapter(this);
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(mAdapter);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeWorkerLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        recordingSerializer = new RecordingSerializer(getApplicationContext());
        Button addButton = (Button)findViewById(R.id.addButton);
        items = new ArrayList<>();
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), InputNewWordActivity.class);
                startActivityForResult(i, 1);
            }
        });
        showPreviousData();
        refreshAdapter();
        display();
    }

    public void speak(PebbleDictionary data){

        String text = data.getString(MainActivity.DATA_KEY);
        /* ttobj.speak("A", TextToSpeech.QUEUE_FLUSH, null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */

        boolean recordingFlag = false;

        try {
            for (int i = 0; i < items.size(); i++) {
                String word = items.get(i).getString("word");
                if(word.equals(text) && !items.get(i).isNull("recording")){
                    recordingFlag = true;
                    playRecording(items.get(i).getString("recording"));
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!recordingFlag)
            ttobj.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void showPreviousData(){
        items.addAll(recordingSerializer.getData());
        for(int i = 0; i < items.size(); i++){
            addToAdapter(items.get(i));
        }
    }


    private void sendAllStringsToPebble() {

        PebbleDictionary dictionary = new PebbleDictionary();

        for(int i = 0; i < items.size(); i++) {
            try {
                dictionary.addString(ADD_KEY, items.get(i).getString("word"));
            } catch (JSONException e) {
                Log.e("JSON", e.getMessage() + "");
            }

        }
        //Send the Dictionary
        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dictionary);
    }

    private void sendToPebble(int key, String val){
        PebbleDictionary dictionary = new PebbleDictionary();
        dictionary.addString(key, val);
        //Send the Dictionary
        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, dictionary);

    }


    private void addToAdapter(JSONObject request){
        mAdapter.add(request);
    }

    private void refreshAdapter(){
        mAdapter.clear();
        mAdapter.addAll(items);
    }

    private void display(){
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    public void deleteItem(int index){

        try {
            sendToPebble(DELETE_KEY, items.get(index).getString("word"));
        } catch (JSONException e) {
            //never going to happen
            e.printStackTrace();
        }
        items.remove(index);
        refreshAdapter();
        display();

    }

    private JSONObject createNewWord(String result, String recordingPath){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("word", result);
            if(recordingPath != null)
                jsonObject.put("recording", recordingPath);
        }
        catch (JSONException e){
            //never going to happen
            Log.e("JSON", e.getMessage() + "");
        }
        return jsonObject;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            String result = data.getStringExtra("newWord");
            String path = null;
            if(data.hasExtra("recording")) {
                path = data.getStringExtra("recording");
            }
            items.add(createNewWord(result, path));
            sendToPebble(ADD_KEY, result);
            refreshAdapter();
            display();
            onRefresh();
        }
        if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
        }

    }//onActivityResult

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void playRecording(int index){

        try{
            String path = items.get(index).getString("recording");
            playRecording(path);

        } catch (JSONException e) {
            Log.e("JSON", e.getMessage() + "");
        }

    }

    private void playRecording(String path){
        MediaPlayer mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("Audio", "prepare() failed with path " + path);
        }
    }

    @Override
    public void onRefresh() {
        sendToPebble(DELETE_ALL, "");
    }

}
