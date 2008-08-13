package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Arc2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFEllipse extends DXFEntity {

    private static final long serialVersionUID = 5630853252028026450L;
    public DXFPoint _centre = new DXFPoint();
    public DXFPoint _point = new DXFPoint();
    public double _ratio = 0;
    public double _start = 0;
    public double _end = 0;
    private Arc2D.Double _e = new Arc2D.Double();

    public DXFEllipse(DXFPoint centre, DXFPoint p, double r, double s, double e, int c, DXFLayer l, int visibility, DXFLineType typeLine) {
        super(c, l, visibility, typeLine, DXFTable.defaultThickness);
        _centre = centre;
        _point = p;
        _ratio = r;
        _end = e;
        _start = s;
        _e.setArcType(Arc2D.OPEN);

    }

    public DXFEllipse() {
        super(-1, null, 0, null, DXFTable.defaultThickness);

    }

    public static DXFEllipse read(DXFBufferedReader br, DXFUnivers univers) throws NumberFormatException, IOException {

        String ligne, ligne_temp;
        int visibility = 0, c = 0;
        double x = 0, y = 0, x1 = 0, y1 = 0, r = 0, s = 0, e = 0;
        DXFLayer l = null;
        DXFLineType lineType = null;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("40")) {
                r = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("41")) {
                s = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("42")) {
                e = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("11")) {
                x1 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("21")) {
                y1 = Double.parseDouble(ligne);
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        return new DXFEllipse(
                new DXFPoint(x, y, c, l, visibility, 1),
                new DXFPoint(x1, y1, c, l, visibility, 1),
                r, s, e, c, l, visibility, lineType);
    }


}
