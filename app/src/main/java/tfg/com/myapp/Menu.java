package tfg.com.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.io.File;

public class Menu extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu);

		File folder = new File(Environment.getExternalStorageDirectory()	+ "/ECG-Analyzer");
		if(!folder.isDirectory()) {
			if (folder.mkdir()) {
				System.out.println("Se ha creado el directorio ECG-Analyzer");
			}
		}
	}
	
	public void openCam(View view) {
		//Intent mainIntent = new Intent(this,TakePhoto.class);
        //startActivity(mainIntent);
	}
	
	public void searchPhoto(View view) {
		Intent i = new Intent(this, File_Manager.class);
		startActivity(i);
	}
	
	public void config(View view) {
		Intent i = new Intent(this, Settings.class);
		startActivity(i);
	}
	
	public void howTo(View view) {
		Intent i = new Intent(this, HowTo.class);
		startActivity(i);
	}
	
	public void about(View view) {
		Intent i = new Intent(this, About.class);
		startActivity(i);
	}
	
	public void log(View view) {
		//Intent i = new Intent(this, GridActivityCamOpBeta.class);
		//startActivity(i);
	}

	public void gridActivity(View view) {
		Intent i = new Intent(this, OptionGrid.class);
		startActivity(i);
	}
}
