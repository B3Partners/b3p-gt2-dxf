/*
 * [ 1719398 ] First shot at LWPOLYLINE
 * Peter Hopfgartner - hopfgartner
 *  
 */
package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Point2D;

public class DXFLwVertex extends DXFPoint {

    double _bulge;

    public DXFLwVertex(Point2D.Double p, double bulge) {
        super(p);
        this._bulge = bulge;
    }

    public DXFLwVertex(double x, double y, double bulge) {
        super(new Point2D.Double(x, y));
        this._bulge = bulge;
    }

    public DXFLwVertex(DXFLwVertex orig, boolean bis) {
        super(orig._point.x, orig._point.y, orig._color, orig._refLayer, 0, 1);
        _bulge = orig._bulge;
    }

    public double getBulge() {
        return _bulge;
    }
}
