package tfg.com.myapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import tfg.com.helpers.BitmapCropTask;
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

    private float xIniD = 0f;
    private float yIniD = 0f;
    private float xFinD = 640f;
    private float yFinD = 880f;

    private int xFinReal = 640;
    private int yFinReal = 880;

    private int numComp = 0;
    private float adjIni = 0f;
    private float adjFin = 1f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_photo_crop);

		Bundle extras = getIntent().getExtras();
		path = extras.getString("photoPath");
		name = extras.getString("photoName");


        EditPhotoView imageView = (EditPhotoView) findViewById(R.id.editable_image);
        EditableImage image = new EditableImage(path);
        String [] parts = readFile().split("\n");

        Bitmap bmp = BitmapFactory.decodeFile(path);

        numComp = Integer.parseInt(parts[parts.length -9].split(" ")[1]);

        final boolean notFit = !(bmp.getWidth() == 1920);

        if(notFit){ //Not whole screen on my device...

            xFinReal = bmp.getWidth();
            yFinReal = bmp.getHeight();

            if(bmp.getWidth() > 1920 && bmp.getWidth() < 2100){
                xFinD = bmp.getWidth()*14/15f;
                xIniD = bmp.getWidth()/15;
                adjIni = xIniD;
                adjFin = 14/15f;
            }else if(name.startsWith("ECG_")){
                xFinD = bmp.getWidth()*5/6;
                xIniD = bmp.getWidth()/6;
                adjIni = xIniD;
                adjFin = 5/6f;
            }else{
                xFinD = bmp.getWidth()*7/8;
                xIniD = bmp.getWidth()/8;
                adjIni = xIniD;
                adjFin = 7/8f;
            }

            yFinD = bmp.getHeight()/3;

            xFin = bmp.getWidth();
            yFin = bmp.getHeight()/3;

            image.setBox(new ScalableBox(xIni, yIni, xFin, yFin));
            imageView.initView(this, image);
        }else{
            xFinReal = bmp.getWidth();
            yFinReal = bmp.getHeight();

            xFinD = bmp.getWidth();
            yFinD = bmp.getHeight()/3;

            xFin = bmp.getWidth();
            yFin = bmp.getHeight()/3;

            image.setBox(new ScalableBox(xIni, yIni, xFin, yFin));
            imageView.initView(this, image);
        }


        drawBoxCrop = (DrawBoxCrop) findViewById(R.id.drawCropLines);

        imageView.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                xIni = x1;
                xFin = x2;
                yIni = y1;
                yFin = y2;
                if(notFit) {
                    xIniD = x1 + adjIni;
                    xFinD = x2 * adjFin;
                    yIniD = y1;
                    yFinD = y2;
                }else{
                    xIniD = x1;
                    xFinD = x2;
                    yIniD = y1;
                    yFinD = y2;
                }
            }
        });

	}

	@Override
	public void onPause() {
		super.onPause();
	}

    public void undo(View view) {
        if(drawBoxCrop.positions.size() > 0){
            Log.i(TAG, "Traza y: Entra al botton ");
            drawBoxCrop.positions.remove(drawBoxCrop.positions.size() -1 );
            drawBoxCrop.reDraw();

            String pathReal = path.substring(0, path.indexOf(name)) + "Derivaciones";
            File folder = new File(pathReal);

            String ext = "";
            if (name.endsWith(".jpg")){
                ext = ".jpg";
            }else if (name.endsWith(".png")){
                ext = ".png";
            }

            int numeroDist = folder.listFiles().length/2;

            Log.i(TAG, "Traza y: " + pathReal + "/Derivacion_" + numeroDist + ext);
            Log.i(TAG, "Traza y: Grid_" + numeroDist + ext);
            File fd = new File(pathReal + "/Derivacion_" + numeroDist + ext);
            if(fd.exists()){
                fd.delete();
            }


            File fdGrid = new File(pathReal + "/Grid_" + numeroDist + ext);
            if(fdGrid.exists()){
                fdGrid.delete();
            }

        }

    }

    public void crop(View view) {
        System.out.println("box: [" + xIni + "," + yIni +"],[" + xFin + "," + yFin + "]");

        drawBoxCrop.positions.add(xIniD + "," + yIniD +"," + xFinD + "," + yFinD + "," + xFinReal + "," + yFinReal);
        drawBoxCrop.reDraw();

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

        String ext = "";
        int isJpeg = 0;
        if (name.endsWith(".jpg")){
            ext = ".jpg";
            isJpeg = 1;
        }else if (name.endsWith(".png")){
            ext = ".png";
            isJpeg = 0;
        }else{
            notfailed = false;
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
            //String ecgPath = path.substring(0, path.indexOf(name)) + "ECG" + ext;
            BitmapCropTask task = new BitmapCropTask(path, pathReal, "Derivacion_" + numeroDist + ext, xIni, yIni, xFin, yFin, bt, false, null, "", null, numComp, isJpeg);
            task.execute(1);

            //Recortamos la rejilla
            boolean isRotated = name.startsWith("ECG_");
            String gridPath = path.substring(0, path.indexOf(name)) + "Grid" + ext;
            if (isRotated){
                gridPath = path.substring(0, path.indexOf(name)) + "Grid_Rotated" + ext;
            }

            BitmapCropTask taskgrid = new BitmapCropTask(gridPath, pathReal, "Grid_" + numeroDist + ext, xIni, yIni, xFin, yFin, bt, true, getApplicationContext(), "Derivacion_" + numeroDist + ext, this, numComp, isJpeg);
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
