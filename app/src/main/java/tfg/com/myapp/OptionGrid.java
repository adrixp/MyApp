package tfg.com.myapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

public class OptionGrid extends Activity {

    private Spinner spinner;

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
            String parts [] = spinnerText.split(" ");
            System.out.println("Muestralo: " + parts[0]);

            Intent i = new Intent(this, GridActivityCamOpBeta.class);
            i.putExtra("mmVolt", textFromEd);
            if(rb.isChecked()){
                i.putExtra("mmSeg", "25");
            }else{
                i.putExtra("mmSeg", "50");
            }

            mEditext.setInputType(InputType.TYPE_NULL);

            startActivity(i);
        }else{
            Toast.makeText(OptionGrid.this, getString(R.string.NotInRangeVolt), Toast.LENGTH_LONG).show();
        }


    }
}
