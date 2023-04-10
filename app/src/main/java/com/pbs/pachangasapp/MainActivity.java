package com.pbs.pachangasapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.pbs.pachangasapp.markers.MapItemizedOverlay;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

public class MainActivity extends AppCompatActivity {

  private MapView mapView;
  private Context ctx;

  GeoPoint initialCenter = new GeoPoint(51.48, 0.0);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ctx = getApplicationContext();

    mapView = findViewById(R.id.mapview);
    mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
    mapView.setClickable(true);
    mapView.getController().setZoom(15.0);
    mapView.getController().setCenter(initialCenter);

    Drawable drawable = getResources().getDrawable(R.drawable.ic_menu_mylocation);

    OverlayItem overlayitem = new OverlayItem("Center", "Center", initialCenter);
    MapItemizedOverlay itemizedoverlay = new MapItemizedOverlay(drawable, this);
    itemizedoverlay.setEnabled(true);
    itemizedoverlay.addOverlay(overlayitem);

    mapView.getOverlays().add(itemizedoverlay);

    setContentView(R.layout.activity_main);
  }
}
