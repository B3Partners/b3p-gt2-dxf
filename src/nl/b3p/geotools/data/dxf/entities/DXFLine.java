package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.Line2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFLine extends DXFEntity {

    private static final long serialVersionUID = 1L;
    public DXFPoint _a = new DXFPoint();
    public DXFPoint _b = new DXFPoint();

    public DXFLine(DXFPoint a, DXFPoint b, int c, DXFLayer l, DXFLineType lineType, double thickness, int visibility) {
        super(c, l, visibility, lineType, thickness);
        _a = a;
        _b = b;

    }

    public DXFLine() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFLine(DXFLine original) {
        super(original._color, original._refLayer, 0, original._lineType, original._thickness);
        _a = new DXFPoint(original._a);
        _b = new DXFPoint(original._b);

    }

    public static DXFLine read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        String ligne = "", ligne_temp = "";
        DXFLayer l = null;
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0, thickness = 0;
        DXFLineType lineType = null;
        int visibility = 0, c = -1;

        while ((ligne = br.readLine()) != null && !ligne.trim().equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("10")) {
                x1 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y1 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("11")) {
                x2 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("21")) {
                y2 = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("39")) {
                thickness = Double.parseDouble(ligne);
                ;
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        return new DXFLine(new DXFPoint(x1, y1, c, l, visibility, 1),
                new DXFPoint(x2, y2, c, l, visibility, 1),
                c,
                l,
                lineType,
                thickness,
                visibility);
    }


}
