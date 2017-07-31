package tfg.com.myapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by adria on 25/01/2017.
 */

public class Photo_Preview  extends Activity {

    private static final String TAG = "TFG:Preview:Activity";

    private String path;
    private String name;
    private int numComp;


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
            String [] parts = readFile().split("\n");
            numComp = Integer.parseInt(parts[parts.length -9].split(" ")[1]);
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
        i.putExtra("photoComp", numComp);
        startActivity(i);
    }

    public String readFile(){

        File file = new File(path.substring(0, path.indexOf("Erased Derivations/" + name)), "settings.txt");
        //Read text from file
        String line;
        String text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                text = text + line + "\n";
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            text = "";
        }
        return text;
    }
}
