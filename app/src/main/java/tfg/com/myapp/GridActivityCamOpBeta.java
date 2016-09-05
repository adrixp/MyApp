package tfg.com.myapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by PIZARROSAEXT on 29/08/2016.
 */
public class GridActivityCamOpBeta extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }


    }
}