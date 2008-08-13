package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Arc2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFArc extends DXFEntity {

    private static final long serialVersionUID = 1L;
    public DXFPoint _point = new DXFPoint();
    public double _radius = 0;
    protected double _angle1 = 0;
    protected double _angle2 = 0;
    private Arc2D.Double _a = new Arc2D.Double();

    public DXFArc(double a1, double a2, DXFPoint p, double r, DXFLineType lineType, int c, DXFLayer l, int visibility, double thickness) {
        super(c, l, visibility, lineType, thickness);
        _point = p;
        _radius = r;
        _angle1 = a1;
        _angle2 = a2;
        _thickness = thickness;

    }

    public DXFArc() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
        _point = new DXFPoint();
        _radius = 0;

    }

    public DXFArc(DXFArc orig) {
        super(orig._color, orig._refLayer, 0, orig._lineType, orig._thickness);
        _point = new DXFPoint(orig._point);
        _radius = orig._radius;
        _angle1 = orig._angle1;
        _angle2 = orig._angle2;
    }

    public static DXFArc read(DXFBufferedReader br, DXFUnivers univers) throws NumberFormatException, IOException {

        String ligne, ligne_temp = "";
        double x = 0, y = 0, r = 0, a1 = 0, a2 = 0, thickness = 0;
        int visibility = 0, c = 0;
        DXFLineType lineType = null;
        DXFLayer l = null;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("40")) {
                r = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("50")) {
                a1 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("51")) {
                a2 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("39")) {
                thickness = Double.parseDouble(ligne);
            }
        }
        return new DXFArc(a1, a2, new DXFPoint(x, y, c, null, visibility, 1), r, lineType, c, l, visibility, thickness);
    }
}
