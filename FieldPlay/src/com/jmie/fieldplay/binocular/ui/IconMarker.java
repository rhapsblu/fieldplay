package com.jmie.fieldplay.binocular.ui;

import com.jmie.fieldplay.binocular.ui.objects.PaintableIcon;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This class extends Marker and draws an icon instead of a circle for it's
 * visual representation.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class IconMarker extends Marker {

    private Bitmap bitmap = null;

    public IconMarker(String name, String description, double latitude, double longitude, double altitude, int color, Bitmap bitmap) {
        super(name, description, latitude, longitude, altitude, color);
        this.bitmap = bitmap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawIcon(Canvas canvas) {
        if (canvas == null || bitmap == null) throw new NullPointerException();

        if (gpsSymbol == null) gpsSymbol = new PaintableIcon(bitmap, 96, 96);
        super.drawIcon(canvas);
    }
}
