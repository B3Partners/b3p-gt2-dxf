package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFPoint extends DXFEntity {

    private static final long serialVersionUID = 1L;
    private Line2D.Double _l = new Line2D.Double();
    public Point2D.Double _point = new Point2D.Double(0, 0);

    public DXFPoint(Point2D.Double p, int c, DXFLayer l, int visibility, float thickness) {
        super(c, l, visibility, null, thickness);
        if (p == null) {
            p = new Point2D.Double(0, 0);
        }
        _point = p;
        _thickness = thickness;

    }

    public DXFPoint(Point2D.Double p) {
        super(-1, null, 0, null, DXFTable.defaultThickness);
        if (p == null) {
            p = new Point2D.Double(0, 0);
        }
        _point = p;
    }

    public DXFPoint() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFPoint(double x, double y, int c, DXFLayer l, int visibility, double thickness) {
        super(c, l, visibility, null, DXFTable.defaultThickness);
        _point = new Point2D.Double(x, y);
    }

    public DXFPoint(DXFPoint _a) {
        super(_a._color, _a._refLayer, 0, null, DXFTable.defaultThickness);
        _point = new Point2D.Double(_a.X(), _a.Y());
    }

    public void setX(double x) {
        _point.x = x;
    }

    public void setY(double y) {
        _point.y = y;
    }

    public double X() {
        return _point.getX();
    }

    public double Y() {
        return _point.getY();
    }

    public static DXFPoint read(DXFBufferedReader br, DXFUnivers univers) throws NumberFormatException, IOException {
        String ligne, ligne_temp;
        DXFLayer l = null;
        int visibility = 0, color = -1;
        double x = 0, y = 0, thickness = 0;

//        myLog.writeLog("> new myPoint");
        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                color = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("39")) {
                thickness = Double.parseDouble(ligne);
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        return new DXFPoint(x, y, color, l, visibility, thickness);
    }


}