package tfg.com.myapp;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import me.littlecheesecake.croplayout.EditPhotoView;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;

public class Photo_Preview extends Activity {
	
	private String path;
    private String name;

    private int xIni = 0;
    private int yIni = 0;
    private int xFin = 640;
    private int yFin = 880;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		path = extras.getString("photoPath");
		name = extras.getString("photoName");

        if (name.equals("ECG.jpg")){
            setContentView(R.layout.layout_photo_preview);

            EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);
            EditableImage image = new EditableImage(path);
            String [] parts = readFile().split("\n")[3].split(" ")[1].split("x");
            System.out.println(parts[0] + " " + parts[1]);
            xFin = Integer.parseInt(parts[0]);
            yFin = Integer.parseInt(parts[1])/3;

            image.setBox(new ScalableBox(0,0, xFin, yFin));
            imageView.initView(this, image);

            imageView.setOnBoxChangedListener(new OnBoxChangedListener() {
                @Override
                public void onChanged(int x1, int y1, int x2, int y2) {
                    xIni = x1;
                    xFin = x2;
                    yIni = y1;
                    yFin = y2;
                }
            });
        }else{
            setContentView(R.layout.layout_photo_preview_b);

            ImageView iV = (ImageView) findViewById(R.id.photoPrev);

            Uri myUri = Uri.parse(path);
            iV.setImageURI(myUri);

            Button bt = (Button) findViewById(R.id.cropButton);
            bt.setText(getString(R.string.OptionsMenuFMAnalyze));
            bt.setEnabled(false);
            bt.setBackgroundColor(Color.parseColor("#BDBDBD"));
        }

	}

	@Override
	public void onPause() {
		super.onPause();
	}

    //Ver con tamaños muy grandes que pasa -> Asynctask

    //Dibujar un rectangulo azul donde se ha recortado

    public void crop(View view) {
        System.out.println("box: [" + xIni + "," + yIni +"],[" + xFin + "," + yFin + "]");
        String pathReal = path.substring(0, path.indexOf(name)) + "Derivaciones";
        File folder = new File(pathReal);

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

            BitmapWorkerTask task = new BitmapWorkerTask(path, pathReal, "Derivacion_" + numeroDist + ".png", xIni, yIni, xFin, yFin, bt, false);
            task.execute(1);

            //Recortamos la rejilla
            String gridPath = path.substring(0, path.indexOf(name)) + "Grid.png";
            BitmapWorkerTask taskgrid = new BitmapWorkerTask(gridPath, pathReal, "Grid_" + numeroDist + ".png", xIni, yIni, xFin, yFin, bt, true);
            taskgrid.execute(1);

            Toast.makeText(this, getString(R.string.photoCropped), Toast.LENGTH_LONG).show();
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
