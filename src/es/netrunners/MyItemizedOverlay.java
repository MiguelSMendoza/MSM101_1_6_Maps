package es.netrunners;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	// Array que contendrá los objetos OverlayItem
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	private Context mContext;
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context)
	{
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  // Mostramos información del OverlayItem en un AlertDialog
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}

	// Constructor sencillo que recibe como parámetro el icono que será común en
	// todos los OverlayItems del array mOverlays
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	// Este método nos permitirá añadir objetos OverlayItem
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		// Lee todos los OverlayItems actuales y los prepara para ser dibujados
		// en el mapa
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
