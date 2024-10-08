/*
 * Copyright (C) 2017 doubotis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.hotcoffee.staticmap.layers;

import com.hotcoffee.staticmap.StaticMap;
import com.hotcoffee.staticmap.geo.Location;
import com.hotcoffee.staticmap.geo.LocationBounds;
import com.hotcoffee.staticmap.geo.PointF;
import com.hotcoffee.staticmap.geo.projection.MercatorProjection;

import java.awt.*;

/**
 * @author Christophe
 */
public class WMSLayer extends TMSLayer {
    protected int mMaxZoom = 11;
    protected float mOpacity = 1.0f;
    protected String mHost;
    protected String[] mLayers;
    protected String mFilter;
    private final int mMinZoom = 4;
    private StaticMap mMapPicture;

    public WMSLayer(String host, String[] layers) {
        super(host);

        mHost = mPattern;
        mLayers = layers;
    }

    public void setLayers(String[] layers) {
        mLayers = layers;
    }

    @Override
    protected String buildURL(int tileX, int tileY, int tileZ) {

        MercatorProjection proj = mMapPicture.getProjection();

        StringBuilder pattern = new StringBuilder();
        pattern.append(mHost);
        pattern.append("?service=WMS&version=1.1.1&request=GetMap&Layers=");
        for (int i = 0; i < mLayers.length; i++) {
            pattern.append(mLayers[i]);
            if (i < mLayers.length - 1)
                pattern.append(",");
        }

        // Compute locations corners.
        double lat = latitudeFromTile(tileY, tileZ);
        double lon = longitudeFromTile(tileX, tileZ);
        Location topLeftLocation = new Location(lat, lon);

        PointF topLeftCorner = proj.unproject(topLeftLocation, tileZ);
        PointF bottomRightCorner = new PointF(topLeftCorner.x() + proj.getTileSize(),
                topLeftCorner.y() + proj.getTileSize());
        Location bottomRightLocation = proj.project(bottomRightCorner, tileZ);

        LocationBounds bounds = new LocationBounds(
                topLeftLocation.mLongitude(),
                bottomRightLocation.mLongitude(),
                topLeftLocation.mLatitude(),
                bottomRightLocation.mLatitude());

        pattern.append("&Styles=&SRS=EPSG:4326");
        pattern.append("&BBOX=").append(bounds.xmin).append(",").append(bounds.ymax).append(",").append(bounds.xmax).append(",").append(bounds.ymin);
        pattern.append("&width=").append(proj.getTileSize());
        pattern.append("&height=").append(proj.getTileSize());
        pattern.append("&format=image/png");
        pattern.append("&TRANSPARENT=" + "TRUE");
        pattern.append((mFilter == null) ? "" : "&cql_Filter=" + mFilter);

        return pattern.toString();

    }

    @Override
    public void draw(Graphics2D graphics, StaticMap mp) {
        mMapPicture = mp;
        super.draw(graphics, mp);

    }


}
