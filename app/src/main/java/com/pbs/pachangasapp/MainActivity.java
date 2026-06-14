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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pbs.pachangasapp.markers.MapItemizedOverlay;


import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MarkerDialogFragment.OnMarkerCreatedListener {

    private MapView mapView;
    private MapItemizedOverlay itemizedoverlay;
    private MyLocationNewOverlay myLocationOverlay;
    private DatabaseReference mDatabase;
    private List<Match> currentMatchesList = new ArrayList<>();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    GeoPoint initialCenter = new GeoPoint(51.48, 0.0);

    // CONFIGURACIÓN DEL FILTRO DE RADIO (En kilómetros)
    private static final double MAX_RADIO_KM = 25.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        String urlFirebaseEuropa = "https://pachangasapp-f3283-default-rtdb.europe-west1.firebasedatabase.app";
        mDatabase = FirebaseDatabase.getInstance(urlFirebaseEuropa).getReference("matches");

        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setClickable(true);
        mapView.setMultiTouchControls(true); // Habilitar el zoom con dos dedos
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(initialCenter);

        Drawable drawable = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_location);
        itemizedoverlay = new MapItemizedOverlay(drawable, this);
        itemizedoverlay.setEnabled(true);

        mapView.getOverlays().add(itemizedoverlay);

        // Configurar gestos en el mapa
        org.osmdroid.events.MapEventsReceiver mReceive = new org.osmdroid.events.MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing() && !isDestroyed()) {
                            MarkerDialogFragment dialog = MarkerDialogFragment.newInstance(p);
                            dialog.show(getSupportFragmentManager(), "CreateMarker");
                        }
                    }
                });
                return true;
            }
        };

        org.osmdroid.views.overlay.MapEventsOverlay mapEventsOverlay = new org.osmdroid.views.overlay.MapEventsOverlay(mReceive);
        mapView.getOverlays().add(0, mapEventsOverlay);

        // CONFIGURAR UBICACIÓN ACTUAL
        configurarUbicacionActual();

        // EMPEZAR A ESCUCHAR LOS PARTIDOS DE LA NUBE EN TIEMPO REAL
        listenToCloudMatches();
    }

    // ESCUCHA GLOBAL (Inmune a fallos de distancias en emulador)
    private void listenToCloudMatches() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentMatchesList.clear();
                recrearOverlayMarcadores();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Match match = snapshot.getValue(Match.class);

                    if (match != null && "ACTIVO".equals(match.getStatus())) {
                        currentMatchesList.add(match);

                        GeoPoint matchPoint = new GeoPoint(match.getLatitude(), match.getLongitude());
                        OverlayItem item = new OverlayItem(match.getTitle(), match.getDescription(), matchPoint);
                        itemizedoverlay.addOverlay(item);
                    }
                }
                // Forzar refresco físico de la pantalla
                mapView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // ESCUCHAR ÚNICAMENTE PARTIDOS DENTRO DEL RADIO GEOGRÁFICO
    private void listenToMatchesInRadio(GeoPoint centroZona) {
        // 1. Convertir kilómetros a variaciones aproximadas de grados de latitud/longitud
        double offsetLat = MAX_RADIO_KM / 111.0;
        double offsetLng = MAX_RADIO_KM / (111.0 * Math.cos(Math.toRadians(centroZona.getLatitude())));


        // 2. Calcular los límites máximos y mínimos de la caja (bounding box)
        double minLat = centroZona.getLatitude() - offsetLat;
        double maxLat = centroZona.getLatitude() + offsetLat;
        double minLng = centroZona.getLongitude() - offsetLng;
        double maxLng = centroZona.getLongitude() + offsetLng;

        // 3. Consultar y ordenar en Firebase indexando por la latitud (gracias a las reglas que guardamos)
        mDatabase.orderByChild("latitude")
                .startAt(minLat)
                .endAt(maxLat)
                .addValueEventListener(new ValueEventListener() { // Usa addValueEventListener para actualizaciones en vivo
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentMatchesList.clear();
                        recrearOverlayMarcadores();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Match match = snapshot.getValue(Match.class);

                            // 4. Doble verificación para comprobar que se encuentra también dentro del rango de longitud
                            if (match != null && "ACTIVO".equals(match.getStatus())) {
                                if (match.getLongitude() >= minLng && match.getLongitude() <= maxLng) {
                                    currentMatchesList.add(match);

                                    GeoPoint matchPoint = new GeoPoint(match.getLatitude(), match.getLongitude());
                                    OverlayItem item = new OverlayItem(match.getTitle(), match.getDescription(), matchPoint);
                                    itemizedoverlay.addOverlay(item);
                                }
                            }
                        }
                        mapView.invalidate();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


    }

    // Método de asistencia para limpiar el mapa visual al actualizar datos de red
    private void recrearOverlayMarcadores() {
        if (mapView != null && itemizedoverlay != null) {
            mapView.getOverlays().remove(itemizedoverlay);
        }
        Drawable drawable = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_location);
        itemizedoverlay = new MapItemizedOverlay(drawable, this);
        itemizedoverlay.setEnabled(true);
        mapView.getOverlays().add(itemizedoverlay);
    }

    // 🟢 RECEPTOR QUE SUBE EL NUEVO PARTIDO CON AUDITORÍA A FIREBASE
    @Override
    public void onMarkerCreated(Match newMatch) {
        // 1. Forzar la conexión a tu servidor de Europa de la captura
        String urlFirebaseEuropa = "https://pachangasapp-f3283-default-rtdb.europe-west1.firebasedatabase.app";
        DatabaseReference mDatabaseEuropa = FirebaseDatabase.getInstance(urlFirebaseEuropa).getReference("matches");

        // 2. Pedir la clave única
        String matchId = mDatabaseEuropa.push().getKey();
        if (matchId != null) {
            newMatch.setId(matchId);

            // 3. Subir el objeto a internet
            mDatabaseEuropa.child(matchId).setValue(newMatch);
        }
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

        GpsMyLocationProvider provider = new GpsMyLocationProvider(this);
        // Habilitar respaldo por red celular además del satélite para pruebas rápidas
        provider.addLocationSource(android.location.LocationManager.NETWORK_PROVIDER);

        myLocationOverlay = new MyLocationNewOverlay(provider, mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();

        // FORZAR ACTIVACIÓN DEL FILTRO LOCAL CUANDO EL GPS TENGA LA COORDENADA FÍSICA
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GeoPoint miUbicacionActual = myLocationOverlay.getMyLocation();
                        if (miUbicacionActual != null) {
                            // Centramos la cámara del usuario en su zona
                            mapView.getController().setZoom(14.0);
                            mapView.getController().animateTo(miUbicacionActual);

                            // Iniciamos la escucha en tiempo real acotada exclusivamente a sus coordenadas
                            listenToMatchesInRadio(miUbicacionActual);
                        }
                    }
                });
            }
        });

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

}
