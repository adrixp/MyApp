package tfg.com.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by adria on 19/01/2017.
 */

public class Photo_Rotate extends Activity{

    private String path;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rotate);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");

        ImageView iV = (ImageView) findViewById(R.id.photoRot);

        Uri myUri = Uri.parse(path);
        iV.setImageURI(myUri);


    }

    public void rotate(View view) {
        lanzarOptions(path,name,this);
    }


    public void lanzarOptions(final String p, final String n, final Activity activity) {

        new AlertDialog.Builder(activity).setTitle(getString(R.string.rotated))
                .setMessage(getString(R.string.cropOrnot))
                .setPositiveButton(getString(R.string.proccesOrnotYes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        Intent i = new Intent(activity, Photo_Preview.class);
                        i.putExtra("photoPath", p);
                        i.putExtra("photoName", n);
                        startActivity(i);
                    }
                })
                .setNegativeButton(getString(R.string.proccesOrnotLater), null)
                .show();
    }
}
