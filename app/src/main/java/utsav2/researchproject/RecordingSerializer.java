package utsav2.researchproject;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 *
 * Created by utsav on 4/25/15.
 */
public class RecordingSerializer {

    private static final String METADATA_FILENAME = "data.json";
    private File metadata;
    private ArrayList<JSONObject> data;

    Context mContext;

    public RecordingSerializer(Context context){
        mContext = context;
        metadata = new File(context.getFilesDir(), METADATA_FILENAME);
        data = new ArrayList<JSONObject>();
        try {
            if (!metadata.exists()) {
                metadata.createNewFile();
            } else {
                parseData(new FileInputStream(metadata));
            }
        }
        catch (IOException | JSONException e){
            Log.e("Serializer", e.getMessage() + "");
        }
    }

    private void parseData(FileInputStream stream) throws JSONException, IOException{

        FileChannel fc = stream.getChannel();
        String jString = null;
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        jString = Charset.defaultCharset().decode(bb).toString();
        stream.close();

        JSONArray json_data = new JSONArray(jString);
        for(int i = 0; i < json_data.length(); i++){
            data.add(json_data.getJSONObject(i));
        }

    }

    public ArrayList<JSONObject> getData(){
        return data;
    }

    public void setData(ArrayList<JSONObject> input) {
        try {
            BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(metadata, false));

            JSONArray items = new JSONArray();

            for(int i = 0; i < input.size(); i++){
                items.put(input.get(i));
            }

            buf.write(items.toString().getBytes());
            buf.flush();
            buf.close();
        }
        catch (IOException e){
            Log.e("Serializer", e.getMessage() + "");
        }
    }
}
