package utsav2.researchproject;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean connected = PebbleKit.isWatchConnected(getApplicationContext());

        Toast.makeText(this, "Connected - " + Boolean.toString(connected), Toast.LENGTH_SHORT).show();

        final Button testButton = (Button)findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRandomInt((int) Math.round(Math.random() * 100));
            }
        });
    }

    private static final int TEMP_KEY = 0x0;

    private static final UUID APP_UUID = UUID.fromString("6b65b8d3-b4f9-4026-a4e7-cfacd4c1d8e3");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sendRandomInt(int message) {
        // Build up a Pebble dictionary containing the weather icon and the current temperature in degrees celsius
        PebbleDictionary data = new PebbleDictionary();
        data.addString(TEMP_KEY, String.format("%d", message));
        // Send the assembled dictionary to the weather watch-app; this is a no-op if the app isn't running or is not
        // installed
        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, data);
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
}
