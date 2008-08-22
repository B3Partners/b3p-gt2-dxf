/*
 * [ 1719398 ] First shot at LWPOLYLINE
 * Peter Hopfgartner - hopfgartner
 *  
 */
package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Point2D;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFLwVertex extends DXFPoint {
    private static final Log log = LogFactory.getLog(DXFLwVertex.class);

    double _bulge;

    public DXFLwVertex(Point2D.Double p, double bulge) {
        super(p);
        this._bulge = bulge;
        setName("DXFLwPolyline");
    }

    public DXFLwVertex(double x, double y, double bulge) {
        super(new Point2D.Double(x, y));
        this._bulge = bulge;
        setName("DXFLwPolyline");
    }

    public DXFLwVertex(DXFLwVertex orig, boolean bis) {
        super(orig._point.x, orig._point.y, orig.getColor(), orig.getRefLayer(), 0, 1);
        _bulge = orig._bulge;
        setName("DXFLwPolyline");
        
    }

    public double getBulge() {
        return _bulge;
    }
}
