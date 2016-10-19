package tfg.com.myapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class OptionGrid extends Activity {

    private Spinner spinner;
    private static final String TAG = "TFG:OptionGrid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_options);

        EditText mEditext = (EditText)findViewById(R.id.editText);
        mEditext.setText("10");

        spinner = (Spinner) findViewById(R.id.grid_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grid_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void continueGrid (View view) {
        float min = 5;
        float max = 20;

        EditText mEditext = (EditText)findViewById(R.id.editText);
        String textFromEd = mEditext.getText().toString();

        RadioButton rb = (RadioButton) findViewById(R.id.radioButton);

        if(textFromEd.equals("")) {
            Toast.makeText(OptionGrid.this, getString(R.string.MustIntro), Toast.LENGTH_LONG).show();
        }else if(Float.parseFloat(textFromEd)>= min && Float.parseFloat(textFromEd)<= max){
            String spinnerText = spinner.getSelectedItem().toString();
            String mmSeg = "";

            if(rb.isChecked()){
                mmSeg = "25";
            }else{
                mmSeg = "50";
            }
            int width = 1440;
            int height = 1080;
            //Get Resolution Selected camera
            CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
            try {

                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics
                            = manager.getCameraCharacteristics(cameraId);
                    StreamConfigurationMap map = characteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                    SharedPreferences pref = PreferenceManager
                            .getDefaultSharedPreferences(this);

                    int numBestPrev = Integer.parseInt(pref.getString("typeCal", "0"));
                    Size choosenSize = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)).get(numBestPrev);

                    width = choosenSize.getWidth();
                    height = choosenSize.getHeight();
                }
            }catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_LONG)
                        .show();
            }catch (Exception e){
                e.printStackTrace();
            }

            String path = Environment.getExternalStorageDirectory().getPath() + "/ECG-Analyzer/";


            String dataSet = "Datos para ECG.jpg:\n" + "Velocidad papel: " + mmSeg + "mm/s \n"
                    + "Voltaje: " + textFromEd + "mm/mV \n"
                    + "Tamaño: " + width + "x" + height + "\n"
                    +"Tamaño rejilla: " + spinnerText;

            try{
                File settings = new File(path, "sharedGridOptions.txt");

                FileOutputStream fos2 = new FileOutputStream(settings);

                fos2.write(dataSet.getBytes());
                fos2.close();
            } catch (java.io.IOException e) {
                Log.e(TAG, "Exception in photoCallback", e);
            }
            Intent i = new Intent(this, GridActivityCamOpBeta.class);
            mEditext.setInputType(InputType.TYPE_NULL);
            startActivity(i);
        }else{
            Toast.makeText(OptionGrid.this, getString(R.string.NotInRangeVolt), Toast.LENGTH_LONG).show();
        }


    }
}
