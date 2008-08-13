package nl.b3p.geotools.data.dxf.entities;

import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;

public class DXFVertex extends DXFPoint {

    private static final long serialVersionUID = 1L;
    protected double _bulge = 0;

    public DXFVertex(double x, double y, double b, int c, DXFLayer l, DXFPolyline refPolyline, int visibility) {
        super(x, y, c, l, visibility, 1);
        _bulge = b;
    }

    public DXFVertex() {
        super(0, 0, -1, null, 0, 1);
    }

    public DXFVertex(DXFVertex v) {
        this._bulge = v._bulge;
        this._color = v._color;
        this._point = v._point;
        this._refLayer = v._refLayer;
    }

    public DXFVertex(DXFVertex orig, boolean bis) {
        super(orig._point.x, orig._point.y, orig._color, orig._refLayer, 0, 1);
        _bulge = orig._bulge;
    }

    public static DXFVertex read(DXFBufferedReader br, DXFUnivers univers, DXFPolyline p) throws IOException {
        String ligne, ligne_temp;
        DXFLayer l = null;
        int visibility = 0, c = -1;
        double x = 0, y = 0, b = 0;
//        myLog.writeLog("> new Vertex");

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("42")) {
                b = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp);
            }
        }
        return new DXFVertex(x, y, b, c, l, p, visibility);
    }
}
