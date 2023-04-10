package com.pbs.pachangasapp.markers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;
import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

public class MapItemizedOverlay extends ItemizedOverlay<OverlayItem> {
  private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
  private Context mContext;

  public MapItemizedOverlay(Drawable defaultMarker, Context context) {
    super(defaultMarker);
    mContext = context;
  }

  public void addOverlay(OverlayItem overlay) {
    mOverlays.add(overlay);
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

  protected boolean onTap(int index) {
    OverlayItem item = mOverlays.get(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
    dialog.setTitle(item.getTitle());
    dialog.setMessage(item.getSnippet());
    dialog.show();
    return true;
  }

  @Override
  public boolean onSnapToItem(int x, int y, Point snapPoint, IMapView mapView) {
    return false;
  }
}
