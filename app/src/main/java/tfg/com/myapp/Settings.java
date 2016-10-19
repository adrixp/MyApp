package tfg.com.myapp;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Size;
import android.widget.Toast;

public class Settings extends PreferenceActivity {
	

	static List<String> listItemsEntries;
	static List<String> listItemsEntriesVal;
	static int counter =0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        try {

            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                //Opciones disponibles para foto
                Size[] choices = map.getOutputSizes(ImageFormat.JPEG);

                listItemsEntries = new ArrayList<String>();
                listItemsEntriesVal = new ArrayList<String>();
                counter = 0;

                for (Size s: choices) {
                    listItemsEntries.add(s.getWidth() + "x" + s.getHeight());
                    listItemsEntriesVal.add(Integer.toString(counter));
                    counter = counter + 1;
                }
                break;
            }
        }catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_LONG)
                    .show();
        }catch (Exception e){
            e.printStackTrace();
        }
		
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();
	}

	@Override
	public void onPause() {
		counter =0;
		Toast.makeText(this, getString(R.string.SavedPref), Toast.LENGTH_LONG)
				.show();
		super.onPause();
	}

	public static class PrefsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.settings);
			
			final ListPreference listPreference = (ListPreference) findPreference("typeCal");
			
			setListPreferenceData(listPreference);		
		}
		
		protected static void setListPreferenceData(ListPreference lp) {
			
            final CharSequence[] entries = listItemsEntries.toArray(new CharSequence[listItemsEntries.size()]);
            final CharSequence[] entryValues = listItemsEntriesVal.toArray(new CharSequence[listItemsEntriesVal.size()]);
            lp.setEntries(entries);
            lp.setEntryValues(entryValues);
            lp.setDefaultValue(entries[0]);

		}
	}

}
