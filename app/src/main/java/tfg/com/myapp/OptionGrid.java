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

    private Spinner spinnerGrid;
    private Spinner spinnerLines;
    private Spinner spinnerColor;
    private static final String TAG = "TFG:OptionGrid";
    EditText mEditext;
    EditText vEditext;
    EditText hEditext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_options);

        mEditext = (EditText)findViewById(R.id.editText);
        mEditext.setText("10");

        hEditext = (EditText)findViewById(R.id.editTextHoriz);
        hEditext.setText("4");

        vEditext = (EditText)findViewById(R.id.editTextVert);
        vEditext.setText("10");

        spinnerGrid = (Spinner) findViewById(R.id.grid_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.grid_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerGrid.setAdapter(adapter);

        //Spinner Lines

        spinnerLines = (Spinner) findViewById(R.id.lineWidth_spinner);
        ArrayAdapter<CharSequence> adapterLines = ArrayAdapter.createFromResource(this,
                R.array.line_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterLines.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerLines.setAdapter(adapterLines);

        spinnerColor = (Spinner) findViewById(R.id.color_spinner);
        ArrayAdapter<CharSequence> adapterColor = ArrayAdapter.createFromResource(this,
                R.array.color_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerColor.setAdapter(adapterColor);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEditext.setInputType(InputType.TYPE_CLASS_NUMBER);
        vEditext.setInputType(InputType.TYPE_CLASS_NUMBER);
        hEditext.setInputType(InputType.TYPE_CLASS_NUMBER);

    }

    public void continueGrid (View view) {
        float min = 5;
        float max = 20;

        String textFromEd = mEditext.getText().toString();
        String textNumberLH = hEditext.getText().toString();
        String textNumberLV = vEditext.getText().toString();

        RadioButton rb = (RadioButton) findViewById(R.id.radioButton);
        RadioButton rbDir = (RadioButton) findViewById(R.id.radioButtonHoriz);
        RadioButton rbDirB = (RadioButton) findViewById(R.id.radioButtonBoth);
        RadioButton rbDirG = (RadioButton) findViewById(R.id.radioButtonGrid);

        if(textFromEd.equals("")) {
            Toast.makeText(OptionGrid.this, getString(R.string.MustIntro), Toast.LENGTH_LONG).show();
        }else if(Float.parseFloat(textFromEd)>= min && Float.parseFloat(textFromEd)<= max){
            String spinnerGridText = spinnerGrid.getSelectedItem().toString();
            String spinnerLineText = spinnerLines.getSelectedItem().toString();
            String spinnerColorText = spinnerColor.getSelectedItem().toString();


            if(spinnerGridText.equals("1mm")){
                spinnerGridText = "4x4 px (1mm)";
            }else if(spinnerGridText.equals("5mm")){
                spinnerGridText = "20x20 px (5mm)";
            }else{
                spinnerGridText = "40x40 px (10mm)";
            }

            String mmSeg = "";

            if(rb.isChecked()){
                mmSeg = "25";
            }else{
                mmSeg = "50";
            }

            String direccion = "";

            if(rbDir.isChecked()){
                direccion = "Horizontal";
            }else if (rbDirB.isChecked()){
                direccion = "Both";
            }else if (rbDirG.isChecked()){
                direccion = "Grid";
            }else{
                direccion = "Vertical";
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


            String dataSet = "Velocidad papel: " + mmSeg + "mm/s \n"
                    + "Voltaje: " + textFromEd + "mm/mV \n"
                    + "Color: " + spinnerColorText + "\n"
                    + "Lineas Numero(h/v): " + textNumberLH + "/" + textNumberLV + "\n"
                    + "Lineas Direccion: " + direccion  + "\n"
                    + "Lineas grosor: " + spinnerLineText + "\n"
                    + "Tamaño: " + width + "x" + height + "\n"
                    + "Tamaño rejilla: " + spinnerGridText;

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
            hEditext.setInputType(InputType.TYPE_NULL);
            vEditext.setInputType(InputType.TYPE_NULL);
            startActivity(i);
        }else{
            Toast.makeText(OptionGrid.this, getString(R.string.NotInRangeVolt), Toast.LENGTH_LONG).show();
        }


    }
}
