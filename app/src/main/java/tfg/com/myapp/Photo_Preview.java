package tfg.com.myapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by adria on 25/01/2017.
 */

public class Photo_Preview  extends Activity {

    private static final String TAG = "TFG:Preview:Activity";

    private String path;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");

        setContentView(R.layout.layout_photo_preview);

        ImageView iV = (ImageView) findViewById(R.id.photoPrev);

        Uri myUri = Uri.parse(path);
        iV.setImageURI(myUri);

        Button bt = (Button) findViewById(R.id.prevButton);

        if(name.startsWith("Erased")){
            bt.setText(getString(R.string.OptionsMenuFMUpload));
            bt.setEnabled(true);

        }else{
            bt.setVisibility(View.INVISIBLE);
        }

        TextView tv = (TextView) findViewById(R.id.textViewPrev);
        tv.setText(name);


    }

    public void upload (View view){
        Intent i = new Intent(this, Photo_Upload.class);
        i.putExtra("photoPath", path);
        i.putExtra("photoName", name);
        startActivity(i);
    }

}
