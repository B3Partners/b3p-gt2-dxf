package nl.b3p.geotools.data.dxf.entities;

import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFBlock;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;

public class DXFDimension extends DXFBlockReference {

    public double _angle = 0;//50
    public String _dimension = "<>";//1
    public DXFPoint _point_WCS = new DXFPoint();//10,20

    public DXFDimension(double a, String dim, double x, double y, DXFBlock refBlock, String nomBlock, DXFLayer l, int visibility, int c, DXFLineType lineType) {
        super(c, l, visibility, null, nomBlock, refBlock);
        _angle = a;
        _dimension = dim;
        _point_WCS = new DXFPoint(x, y, c, null, visibility, 1);
    }

    public DXFDimension() {
        super(-1, null, 0, null, "", null);
    }

    public static DXFDimension read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        String ligne = "", ligne_temp = "", dimension = "", nomBlock = "";
        DXFDimension d = null;
        DXFLayer l = null;
        DXFBlock refBlock = null;
        double angle = 0, x = 0, y = 0;
        int visibility = 0, c = -1;
        DXFLineType lineType = null;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("1")) {
                dimension = ligne;
            } else if (ligne_temp.equalsIgnoreCase("50")) {
                angle = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("2")) {
                nomBlock = ligne;
                refBlock = univers.findBlock(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            }
        }
        d = new DXFDimension(angle, dimension, x, y, refBlock, nomBlock, l, visibility, c, lineType);

        if ((refBlock == null) || (refBlock != null && !refBlock._name.equalsIgnoreCase(nomBlock))) {
            univers.addRefBlockForUpdate(d);
        }
        return d;
    }


}
