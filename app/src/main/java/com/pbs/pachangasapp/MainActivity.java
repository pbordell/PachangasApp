package com.pbs.pachangasapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pbs.pachangasapp.markers.MapItemizedOverlay;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity implements MarkerDialogFragment.OnMarkerCreatedListener {

    private MapView mapView;
    private MapItemizedOverlay itemizedoverlay;
    private MyLocationNewOverlay myLocationOverlay; // 🟢 Capa de ubicación actual

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    GeoPoint initialCenter = new GeoPoint(51.48, 0.0); // Ubicación por defecto si falla el GPS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setClickable(true);
        mapView.setMultiTouchControls(true); // Habilitar el zoom con dos dedos
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(initialCenter);

        Drawable drawable = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_location);
        itemizedoverlay = new MapItemizedOverlay(drawable, this);
        itemizedoverlay.setEnabled(true);

        OverlayItem initialItem = new OverlayItem("Center", "Center", initialCenter);
        itemizedoverlay.addOverlay(initialItem);
        mapView.getOverlays().add(itemizedoverlay);

        // Configurar gestos en el mapa
        org.osmdroid.events.MapEventsReceiver mReceive = new org.osmdroid.events.MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                MarkerDialogFragment dialog = MarkerDialogFragment.newInstance(p);
                dialog.show(getSupportFragmentManager(), "CreateMarker");
                return true;
            }
        };

        org.osmdroid.views.overlay.MapEventsOverlay mapEventsOverlay = new org.osmdroid.views.overlay.MapEventsOverlay(mReceive);
        mapView.getOverlays().add(0, mapEventsOverlay);

        // CONFIGURAR UBICACIÓN ACTUAL
        configurarUbicacionActual();
    }

    private void configurarUbicacionActual() {
        // Comprobar si tenemos permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si no los tenemos, los solicitamos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            // Si ya los tenemos, activamos la localización
            inicializarCapaUbicacion();
        }
    }

    private void inicializarCapaUbicacion() {
        // Inicializar el proveedor GPS y la capa
        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);

        myLocationOverlay = new MyLocationNewOverlay(provider, mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();

        mapView.getOverlays().add(myLocationOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                inicializarCapaUbicacion();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        // Reactivar ubicación al volver a la app
        if (myLocationOverlay != null) myLocationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        // Pausar ubicación para ahorrar batería [1]
        if (myLocationOverlay != null) myLocationOverlay.disableMyLocation();
    }

    @Override
    public void onMarkerCreated(String title, String description, GeoPoint point) {
        OverlayItem newItem = new OverlayItem(title, description, point);
        itemizedoverlay.addOverlay(newItem);
        mapView.invalidate();
    }

}
