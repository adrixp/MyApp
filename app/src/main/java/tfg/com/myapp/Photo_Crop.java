package tfg.com.myapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import me.littlecheesecake.croplayout.EditPhotoView;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;
import tfg.com.helpers.BitmapWorkerTask;
import tfg.com.helpers.DrawBoxCrop;

public class Photo_Crop extends Activity {

    private static final String TAG = "TFG:Crop:Activity";
    DrawBoxCrop drawBoxCrop;
	
	private String path;
    private String name;

    private int xIni = 0;
    private int yIni = 0;
    private int xFin = 640;
    private int yFin = 880;

    private int xFinReal = 640;
    private int yFinReal = 880;

    private int isJpeg = 0;
    private int numcomp = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_crop);

		Bundle extras = getIntent().getExtras();
		path = extras.getString("photoPath");
		name = extras.getString("photoName");

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        numcomp = Integer.parseInt(pref.getString("typeComp", "0")) * 10 + 10;
        isJpeg = Integer.parseInt(pref.getString("typeFormat", "0"));


        EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);
        EditableImage image = new EditableImage(path);
        String [] parts = readFile().split("\n");
        String [] partsEnd = parts[parts.length -2].split(" ")[1].split("x");

        xFinReal = Integer.parseInt(partsEnd[0]);
        yFinReal = Integer.parseInt(partsEnd[1]);

        xFin = Integer.parseInt(partsEnd[0]);
        yFin = Integer.parseInt(partsEnd[1])/3;

        image.setBox(new ScalableBox(0,0, xFin, yFin));
        imageView.initView(this, image);

        drawBoxCrop = (DrawBoxCrop) findViewById(R.id.drawCropLines);

        imageView.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                xIni = x1;
                xFin = x2;
                yIni = y1;
                yFin = y2;
            }
        });

	}

	@Override
	public void onPause() {
		super.onPause();
	}

    public void crop(View view) {
        System.out.println("box: [" + xIni + "," + yIni +"],[" + xFin + "," + yFin + "]");

        drawBoxCrop.positions.add(xIni + "," + yIni +"," + xFin + "," + yFin + "," + xFinReal + "," + yFinReal);
        drawBoxCrop.reDraw();

        String pathReal = path.substring(0, path.indexOf(name)) + "Derivaciones";
        File folder = new File(pathReal);

        String ext = "";
        if (name.equals("ECG.jpg")){
            ext = ".jpg";
            isJpeg = 1;
        }else{
            ext = ".png";
            isJpeg = 0;
        }

        Boolean notfailed = true;
        if(!folder.isDirectory()) {
            if (folder.mkdir()) {
                //Se crea el directorio y la derivación
                System.out.println("Se crea el directorio derivaciones");
            }else{
                System.out.println("Fallo al crear el directorio derivaciones");
                notfailed = false;
            }
        }

        if (notfailed){
            int numeroDist;
            if(folder.listFiles().length<2){
                numeroDist = folder.listFiles().length + 1;
            }else{
                numeroDist = folder.listFiles().length/2 + 1;
            }
            Button bt = (Button) findViewById(R.id.cropButton);
            bt.setEnabled(false);
            bt.setBackgroundColor(Color.parseColor("#BDBDBD"));
            //Recortamos la foto

            BitmapWorkerTask task = new BitmapWorkerTask(path, pathReal, "Derivacion_" + numeroDist + ext, xIni, yIni, xFin, yFin, bt, false, null, "", null, numcomp, isJpeg);
            task.execute(1);

            //Recortamos la rejilla
            String gridPath = path.substring(0, path.indexOf(name)) + "Grid" + ext;
            BitmapWorkerTask taskgrid = new BitmapWorkerTask(gridPath, pathReal, "Grid_" + numeroDist + ext, xIni, yIni, xFin, yFin, bt, true, getApplicationContext(), "Derivacion_" + numeroDist + ext, this, numcomp, isJpeg);
            taskgrid.execute(1);

            Toast.makeText(this, getString(R.string.working), Toast.LENGTH_LONG).show();
        }

    }

    public String readFile(){

        File file = new File(path.substring(0, path.indexOf(name)), "settings.txt");
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
