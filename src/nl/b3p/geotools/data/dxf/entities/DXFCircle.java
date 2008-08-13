package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Ellipse2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFCircle extends DXFEntity {

    private static final long serialVersionUID = 1L;
    private Ellipse2D.Double _e = new Ellipse2D.Double();
    public DXFPoint _point = new DXFPoint();
    public double _radius = 0;

    public DXFCircle(DXFPoint p, double r, DXFLineType lineType, int c, DXFLayer l, int visibility, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _point = p;
        _radius = r;

    }

    public DXFCircle() {
        super(0, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFCircle(DXFCircle orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _point = new DXFPoint(orig._point);
        _radius = orig._radius;

    }

    public static DXFCircle read(DXFBufferedReader br, DXFUnivers univers) throws NumberFormatException, IOException {

        String ligne, ligne_temp;
        int visibility = 0, color = 0;
        double x = 0, y = 0, r = 0, thickness = 1;
        DXFLayer l = null;
        DXFLineType lineType = null;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                color = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("40")) {
                r = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("39")) {
                thickness = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        return new DXFCircle(new DXFPoint(x, y, color, l, visibility, 1), r, lineType, color, l, visibility, thickness);
    }
}
