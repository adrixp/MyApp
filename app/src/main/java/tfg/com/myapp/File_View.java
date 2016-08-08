package tfg.com.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class File_View  extends Activity {

    private static final String TAG = "TFG:FileView:Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_file_view);

        TextView tv = (TextView) findViewById(R.id.textViewFileView);

        Bundle extras = getIntent().getExtras();
        String path = extras.getString("filePath");

        BufferedReader br = null;
        String txtRead = "";
        try {
            br = new BufferedReader(new FileReader(path));
            String line = br.readLine();

            while (line != null) {
                txtRead = txtRead + line + "\n";
                line = br.readLine();
            }

            br.close();
        } catch (FileNotFoundException e ) {
            Log.i(TAG, "File not Found: " + e.getMessage());
        }catch (IOException e){
            Log.i(TAG, "BufferedReader error: " + e.getMessage());
        }

        tv.setText(txtRead);
    }
}
