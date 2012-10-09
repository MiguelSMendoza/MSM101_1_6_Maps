package es.netrunners;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;

public class MapsActivity extends MapActivity {

	MapView mapView;

	MyItemizedOverlay overlay;

	long touchTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // No se mostrará el
														// título de la
														// aplicación
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.getController().setZoom(14);
		// Centra el mapa en la posición Lat: 39.008510, Long:-1.863080
		mapView.getController().setCenter(getGeoPoint(39.008510, -1.863080));
		setMyLocation();
		mapView.setBuiltInZoomControls(false);

		mapView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Guardamos la hora actual
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					touchTime = System.currentTimeMillis();
				}
				// Comprobamos si el usuario ha levantado el dedo del Mapa y si
				// ha pasado más de 1 segundo
				if (event.getAction() == MotionEvent.ACTION_UP
						&& ((System.currentTimeMillis() - touchTime) > 1000)) {
					// Con la información de posición del evento obtenemos la
					// posición en el mapa
					final GeoPoint p = mapView.getProjection().fromPixels(
							(int) event.getX(), (int) event.getY());
					showNewPointDialog(p);
				}
				return false;
			}
		});
		// Obtenemos los Overlays del Mapa
		List<Overlay> mapOverlays = mapView.getOverlays();
		// Creamos un objeto Drawable con un icono
		Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
		// Creamos una instancia de nuestra clase MyItemizedOverlay con el icono
		// creado
		overlay = new MyItemizedOverlay(drawable, this);
		// Creamos un punto en el mapa
		GeoPoint point = getGeoPoint(38.940713, -1.865999);
		// Creamos un OverlayItem con el punto creado, titulo, y mensaje
		OverlayItem overlayitem = new OverlayItem(point, "Hola Mundo!",
				"Esto es un Snippet");
		// Añadimos OverlayItem a nuestro MyItemizedOverlay
		overlay.addOverlay(overlayitem);
		// Añadimos el Overlay al mapa para mostrar los puntos contenidos
		mapOverlays.add(overlay);

	}

	private void showNewPointDialog(final GeoPoint p) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null);
		builder.setView(textEntryView);
		builder.setTitle("Nuevo Punto")
				.setCancelable(false)
				.setPositiveButton("Añadir",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								EditText titulo = (EditText) textEntryView
										.findViewById(R.id.titulo);
								EditText descripcion = (EditText) textEntryView
										.findViewById(R.id.descripcion);
								// Creamos el GeoPoint con las
								// coordenadas del punto
								// seleccionado por el usuario

								// Creamos un OverlayItem con el
								// punto creado, titulo, y mensaje
								OverlayItem overlayitem = new OverlayItem(p,
										titulo.getText().toString(),
										descripcion.getText().toString());
								overlay.addOverlay(overlayitem);
								// Recargamos el mapa para que se muestre el
								// nuevo punto
								mapView.postInvalidate();
							}

						})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public GeoPoint getGeoPoint(Double latitude, Double longitude) {
		Double lat = latitude;
		lat = lat * 1E6;
		Double lng = longitude;
		lng = lng * 1E6;
		GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
		return point;
	}

	private MyLocationOverlay myLocationOverlay;

	private void setMyLocation() {
		// Creamos el objeto asociándolo al mapa de nuestra actividad
		myLocationOverlay = new MyLocationOverlay(getApplicationContext(),
				mapView);
		myLocationOverlay.enableMyLocation(); // Habilitamos la Localización
		// Creamos un hilo que se ejecutará en segundo plano cuando nuestra
		// actividad reciba la posición actual desde el dispositivo
		myLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				// Obtenemos nuestra posición actual
				GeoPoint currentLocation = myLocationOverlay.getMyLocation();
				// Desplazamos el mapa hacia la posición actual y hacemos zoom
				mapView.getController().animateTo(currentLocation);
				mapView.getController().setZoom(9);
				// Añade un punto en el mapa señalando nuestra localización
				// actual
				mapView.getOverlays().add(myLocationOverlay);
			}
		});
	}

	@Override
	protected void onResume() {
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
