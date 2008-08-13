package nl.b3p.geotools.data.dxf.entities;

import java.awt.geom.GeneralPath;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFSolid extends DXFEntity {

    private static final long serialVersionUID = 2567756283532200546L;
    public DXFPoint _p1 = new DXFPoint();
    public DXFPoint _p2 = new DXFPoint();
    public DXFPoint _p3 = new DXFPoint();
    public DXFPoint _p4 = null;
    public GeneralPath g;

    public DXFSolid() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFSolid(DXFPoint p1, DXFPoint p2, DXFPoint p3, DXFPoint p4,
            double thickness, int c, DXFLayer l, int visibility, DXFLineType lineType) {
        super(c, l, visibility, lineType, thickness);

        _p1 = p1;
        _p2 = p2;
        _p3 = p3;

        if (p4 == null) {
            _p4 = p3;
        } else {
            _p4 = p4;
        }
    }

    public DXFSolid(DXFSolid solid) {
        super(solid._color, solid._refLayer, 0, solid._lineType, solid._thickness);

        _p1 = new DXFPoint(solid._p1);
        _p2 = new DXFPoint(solid._p2);
        _p3 = new DXFPoint(solid._p3);
        _p4 = new DXFPoint(solid._p4);
    }

    public static DXFEntity read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        double p1_x = 0, p2_x = 0, p3_x = 0, p4_x = 0, p1_y = 0, p2_y = 0, p3_y = 0, p4_y = 0;
        double thickness = 0;
        int visibility = 0, c = -1;
        String ligne = "", ligne_tmp = "";
        DXFLayer l = null;
        DXFLineType lineType = null;

//        myLog.writeLog(">> mySolid");
        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_tmp = ligne;
            ligne = br.readLine();

            if (ligne_tmp.equalsIgnoreCase("10")) {
                p1_x = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("11")) {
                p2_x = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("12")) {
                p3_x = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("13")) {
                p4_x = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("20")) {
                p1_y = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("21")) {
                p2_y = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("22")) {
                p3_y = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("23")) {
                p4_y = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("39")) {
                thickness = Double.parseDouble(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_tmp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            }
        }

        return new DXFSolid(
                new DXFPoint(p1_x, p1_y, c, null, visibility, 1),
                new DXFPoint(p2_x, p2_y, c, null, visibility, 1),
                new DXFPoint(p3_x, p3_y, c, null, visibility, 1),
                new DXFPoint(p4_x, p4_y, c, null, visibility, 1),
                thickness,
                c,
                l,
                visibility,
                lineType);
    }


}
