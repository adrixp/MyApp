package tfg.com.myapp;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by adria on 25/01/2017.
 */

public class Photo_EraseMarks extends Activity {

    private static final String TAG = "TFG:Erase:Activity";

    private String path;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");

        setContentView(R.layout.layout_photo_erase_marks);

        ImageView iV = (ImageView) findViewById(R.id.photoErase);

        Uri myUri = Uri.parse(path);
        iV.setImageURI(myUri);

        TextView tv = (TextView) findViewById(R.id.textViewErase);
        tv.setText(name);
    }
}
