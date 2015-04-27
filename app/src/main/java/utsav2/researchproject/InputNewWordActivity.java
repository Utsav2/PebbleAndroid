package utsav2.researchproject;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;


public class InputNewWordActivity extends ActionBarActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_new_word);
        Button addWordButton = (Button)findViewById(R.id.addWordButton);
        final EditText wordEditText = (EditText)findViewById(R.id.wordEditText);
        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newWord = wordEditText.getText().toString();
                if(newWord.length() != 0){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("newWord", newWord);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    } */

    boolean hasRecordedSomething = false;
    boolean mStartRecording = true;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_new_word, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String mFileName = null;

    private MediaRecorder mRecorder = null;

    private MediaPlayer mPlayer = null;

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("Audio", "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Audio", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mFileName = getApplicationContext().getFilesDir().getAbsolutePath() + "/temp.3gp";
        setContentView(R.layout.activity_input_new_word);

        final Button recordButton = (Button)findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordButton.setText("Stop recording");
                } else {
                    recordButton.setText("Record");
                }
                hasRecordedSomething = true;
                mStartRecording = !mStartRecording;
            }
        });

        final Button playButton = (Button)findViewById(R.id.playButton);
        playButton.setText("Play");

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasRecordedSomething)
                    return;
                onPlay(true);
            }
        });

        final Button deleteButton = (Button)findViewById(R.id.deleteRecordingButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasRecordedSomething)
                    return;
                File t = new File(mFileName);
                t.delete();
                hasRecordedSomething = false;
            }
        });

        Button addWordButton = (Button)findViewById(R.id.addWordButton);
        final EditText wordEditText = (EditText)findViewById(R.id.wordEditText);
        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newWord = wordEditText.getText().toString();
                if(newWord.length() != 0){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("newWord", newWord);
                    if(hasRecordedSomething){
                        File from = new File(mFileName);
                        String toPath = getApplicationContext().getFilesDir() + "/" + newWord + ".3gp";
                        File to = new File(toPath);
                        from.renameTo(to);
                        returnIntent.putExtra("recording", toPath);
                    }
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

}
