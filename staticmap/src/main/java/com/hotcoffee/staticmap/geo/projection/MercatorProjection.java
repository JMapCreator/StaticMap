/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hotcoffee.staticmap.geo.projection;

import com.hotcoffee.staticmap.geo.Location;
import com.hotcoffee.staticmap.geo.PointF;

/**
 * @author cbrasseur
 */
public final class MercatorProjection implements GeographicalProjection<PointF> {
    private static final int DEFAULT_TILE_SIZE = 256;

    private final int _tileSize;
    private final PointF _pixelOrigin;
    private final double _pixelsPerLonDegree;
    private final double _pixelsPerLonRadian;

    public MercatorProjection() {
        this._tileSize = DEFAULT_TILE_SIZE;
        this._pixelOrigin = new PointF(this._tileSize / 2.0, this._tileSize / 2.0);
        this._pixelsPerLonDegree = this._tileSize / 360.0;
        this._pixelsPerLonRadian = this._tileSize / (2 * Math.PI);
    }

    @Override
    public PointF unproject(Location location, int zoom) {
        double lat = location.mLatitude();
        double lng = location.mLongitude();

        double x = _pixelOrigin.x() + lng * _pixelsPerLonDegree;

        // Truncating to 0.9999 effectively limits latitude to 89.189. This is
        // about a third of a tile past the edge of the world tile.
        double siny = bound(Math.sin(Math.toRadians(lat)));
        double y = _pixelOrigin.y() + 0.5 * Math.log((1 + siny) / (1 - siny)) * -_pixelsPerLonRadian;

        int numTiles = 1 << zoom;
        x = x * numTiles;
        y = y * numTiles;
        return new PointF(x, y);
    }

    @Override
    public Location project(PointF pt, int zoom) {
        int numTiles = 1 << zoom;
        double x = pt.x() / (double) numTiles;
        double y = pt.y() / (double) numTiles;

        double lng = (x - _pixelOrigin.x()) / _pixelsPerLonDegree;

        double latRadians = (y - _pixelOrigin.y()) / -_pixelsPerLonRadian;
        double lat = Math.toDegrees(2 * Math.atan(Math.exp(latRadians)) - Math.PI / 2);
        return new Location(lat, lng);
    }

    public int getTileSize() {
        return _tileSize;
    }

    double bound(double val) {
        return Math.min(val, 0.9999);
    }

}
