package tfg.com.myapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class File_Manager extends Activity{

	private static final String TAG = "TFG:FileManager:Activy";
	protected Vector<String> path = new Vector<>(1, 1);
	protected List<ListaFM> li = new ArrayList<>();
	private ListView listV;
	private int pos;
	private String nombre;
	private boolean isTxt;
	
	private String namePrev = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_manager);
		
		File f = new File(Environment.getExternalStorageDirectory()	+ "/ECG-Analyzer");
		if(!f.isDirectory()){
			f.mkdirs();
		}

		path.add("/");
		path.add("ECG-Analyzer");
		listV = (ListView) findViewById(R.id.listView3);
		registerForContextMenu(listV);

		AdapFiles(listV, parsePath(path));

		listV.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> lv, View arg1, int position,
					long arg3) {

				ListaFM o = (ListaFM) lv.getItemAtPosition(position);

				if (o.getNombre().equals(parsePath(path) + "..")) {
					checkPath(listV, false);
				} else {


					File f = new File(Environment.getExternalStorageDirectory()
							+ parsePath(path) + "/" + o.getNombre());
					if (f.isDirectory()) {
						if (path.size() > 1) {
							path.add("/");
						}
						path.add(o.getNombre());

						li.clear();
						AdapFiles(listV, parsePath(path));
					} else {
						Toast.makeText(getApplicationContext(),	getString(R.string.LongCLick),Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		isTxt = false;
		li.clear();
		AdapFiles(listV, parsePath(path));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			checkPath(listV, true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void checkPath(ListView listV, boolean fin) {
		if (path.size() > 1) {
			if (path.size() > 2) {
				path.removeElementAt(path.size() - 1);
			}

			path.removeElementAt(path.size() - 1);
			li.clear();
			AdapFiles(listV, parsePath(path));
		} else if (fin) {
			finish();
		}
	}
	
	public void SepararYlistar(File[] files, ListView listV, boolean isDir) {
		for (int i = 0; i < files.length; i++) {

			if (files[i].isDirectory() && isDir) {
				li.add(new ListaFM(files[i].getName(), "dir"));
			} else if (!files[i].isDirectory() && !isDir && CheckExtension(files[i].getName())) {
				li.add(new ListaFM(files[i].getName(), "fich"));
			}
		}
		listV.setAdapter(new ListaAdapter(this, R.layout.single_item_file_manager, li));
	}
	
	public void AdapFiles(ListView listV, String p) {
		File f = new File(Environment.getExternalStorageDirectory() + p);//directorio que lista
		File[] files = f.listFiles();
		Arrays.sort(files);

		li.add(new ListaFM(p + "..", "FirstDir")); //Nombre que aparece arriba

		SepararYlistar(files, listV, true);//primero dir
		SepararYlistar(files, listV, false);//luego files
	}
	
	public String parsePath(Vector<String> v) {
		String s = "";
		for (int i = 0; i < v.size(); i++) {
			s = s + v.elementAt(i);
		}
		return s;
	}
	
	private enum Extensions {
		png, jpg, txt, NOVALUE;
				
		public static Extensions toExt(String str)
	    {
	        try {
	            return valueOf(str);
	        }
	        catch (IllegalArgumentException ex) {
	            return NOVALUE;
	        }
	    } 
	}
	
	public boolean CheckExtension (String p){
		String [] s = p.split("\\.");
		switch(Extensions.toExt(s[s.length-1])){
			case png:
				return true;
			case jpg:
				return true;
			case txt:
				return true;
	        default: 
	        	return false;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		
		pos = aInfo.position;
		nombre = li.get(pos).getNombre();
		namePrev = nombre;
		if(path.size()>1){
			nombre = "/"+nombre;
		}

		String [] s = li.get(pos).getNombre().split("\\.");
						
		if (s[s.length-1].equals("jpg")|| s[s.length-1].equals("png")) {
			nombre = Environment.getExternalStorageDirectory()+ parsePath(path)+nombre; //ruta del archivo

			menu.setHeaderTitle(getString(R.string.OptionsMenuFMC) + " " + li.get(pos).getNombre());
			menu.add(0, 1, 0, getString(R.string.OptionsMenuFMAnalyze));
			menu.add(0, 2, 1, getString(R.string.OptionsMenuFMView));
			menu.add(0, 3, 2, getString(R.string.OptionsMenuFMDelete));
			menu.add(0, 4, 3, getString(R.string.OptionsMenuFMDeleteAll));
			menu.add(0, 5, 4, getString(R.string.OptionsMenuFMCancel));
		}else if(s[s.length - 1].equals("txt")){
			nombre = Environment.getExternalStorageDirectory()+ parsePath(path)+nombre; //ruta del archivo
			isTxt = true;
			menu.setHeaderTitle(getString(R.string.OptionsMenuFMC) + " " + li.get(pos).getNombre());
			menu.add(0, 2, 0, getString(R.string.OptionsMenuFMView));
			menu.add(0, 3, 1, getString(R.string.OptionsMenuFMDelete));
			menu.add(0, 4, 2, getString(R.string.OptionsMenuFMDeleteAll));
			menu.add(0, 5, 3, getString(R.string.OptionsMenuFMCancel));
		}else{
			//aqui borrar directorios
			nombre = Environment.getExternalStorageDirectory()+ parsePath(path)+nombre; //ruta del archivo

			menu.setHeaderTitle(getString(R.string.OptionsMenuFMC) + " " + li.get(pos).getNombre());
			menu.add(0, 3, 0, getString(R.string.OptionsMenuFMDelete));
			menu.add(0, 4, 1, getString(R.string.OptionsMenuFMDeleteAll));
			menu.add(0, 5, 2, getString(R.string.OptionsMenuFMCancel));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item){
		int itemId = item.getItemId();

		if (itemId == 1) {//Analizar			

		}else if (itemId == 2){//ver
			if(isTxt){
				Intent i = new Intent(File_Manager.this, File_View.class);
				i.putExtra("filePath", nombre);
				startActivity(i);
			}else{
				Intent i = new Intent(File_Manager.this, Photo_Preview.class);
				i.putExtra("photoPath", nombre);
				i.putExtra("photoName", namePrev);
				startActivity(i);
			}

		}else if (itemId == 3){//borrar
			File fd = new File(nombre);
			if(fd.exists()) {
				deleteFile(fd);
				li.clear();
				AdapFiles(listV, parsePath(path));
			}

		}else if(itemId == 4){ //borrar todo
			deleteAll(parsePath(path));

	    	li.clear();
			AdapFiles(listV, parsePath(path));
		}
		return true;
	}

	public void deleteFile(File element) {
		if (element.isDirectory()) {
			for (File sub : element.listFiles()) {
				deleteFile(sub);
			}
		}
		element.delete();
	}
	
	public void deleteAll(String p) {
		File f = new File(Environment.getExternalStorageDirectory() + p);//directorio que lista
		for (File sub : f.listFiles()) {
			if (sub.exists()){
				deleteFile(sub);
			}
		}
	}
	
	private class ListaAdapter extends ArrayAdapter<ListaFM> {

		private List<ListaFM> item;

		public ListaAdapter(Context context, int textViewResourceId, List<ListaFM> item) {
			super(context, textViewResourceId, item);
			this.item = item;
		}

		// Create a title and detail, icon is created in the xml file
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				parent = null;
				v = vi.inflate(R.layout.single_item_file_manager, parent);
			}

			ListaFM o = item.get(position);
			TextView tt = (TextView) v.findViewById(R.id.textList);

			String [] s = o.getNombre().split("\\.");

			if (o.getTipo().equals("FirstDir")) {
				tt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_menu_archive, 0, 0, 0);
				tt.setTextColor(Color.parseColor("#FFFFFF"));
				tt.setBackgroundColor(Color.parseColor("#5e7974"));
			}else if (o.getTipo().equals("dir")) {
				tt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_menu_archive, 0, 0, 0);
			} else if(s[s.length-1].equals("txt")){
				tt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_txt, 0, 0, 0);
			} else{
				tt.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
			}
			tt.setText(o.getNombre());
			tt.setTag(o.getNombre());

			return v;
		}
	}

}
