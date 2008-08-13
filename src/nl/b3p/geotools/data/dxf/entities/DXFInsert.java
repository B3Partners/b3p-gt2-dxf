package nl.b3p.geotools.data.dxf.entities;

import java.io.IOException;

import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFBlock;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;

public class DXFInsert extends DXFBlockReference {

    public DXFPoint _point = new DXFPoint();

    public DXFInsert(double x, double y, String nomBlock, DXFBlock refBlock, DXFLayer l, int visibility, int c, DXFLineType lineType) {
        super(c, l, visibility, lineType, nomBlock, refBlock);
        _point = new DXFPoint(x, y, c, null, visibility, 1);

    }

    public DXFInsert(DXFPoint p, String nomBlock, DXFBlock refBlock, DXFLayer l, int visibility, DXFLineType lineType) {
        super(-1, l, visibility, lineType, nomBlock, refBlock);
        _point = p;

    }

    public DXFInsert() {
        super(-1, null, 0, null, "", null);

    }

    public static DXFInsert read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        String ligne = "", ligne_temp = "", nomBlock = "";
        DXFInsert m = null;
        DXFLayer l = null;
        double x = 0, y = 0;
        int visibility = 0, c = -1;
        DXFBlock refBlock = null;
        DXFLineType lineType = null;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("2")) {
                nomBlock = ligne;
                refBlock = univers.findBlock(nomBlock);
            } else if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            }
        }

        m = new DXFInsert(x, y, nomBlock, refBlock, l, visibility, c, lineType);

        if ((refBlock == null) || (refBlock != null && !refBlock._name.equalsIgnoreCase(nomBlock))) {
            univers.addRefBlockForUpdate(m);
        }

        return m;
    }
}
