package nl.b3p.geotools.data.dxf.header;

import java.awt.BasicStroke;
import java.io.IOException;
import java.util.Vector;

import nl.b3p.geotools.data.dxf.entities.DXFBufferedReader;
import nl.b3p.geotools.data.dxf.DXFUnivers;

public class DXFTable {

    public static final double defaultThickness = 1.0f;
    public static final float defautMotif[] = {1.0f, 0.0f};
    private static final float zoomDash[] = DXFLineType.parseTxt("_");
    public static int CAP = BasicStroke.CAP_ROUND;
    public static int JOIN = BasicStroke.JOIN_ROUND;
    public static final BasicStroke defaultStroke = new BasicStroke((float) defaultThickness, CAP, JOIN, 10.0f, defautMotif, 0.0f);
    public static final BasicStroke zoomStroke = new BasicStroke((float) defaultThickness, CAP, JOIN, 10.0f, zoomDash, 0.0f);
    public Vector<DXFLayer> theLayers = new Vector<DXFLayer>();
    public Vector<DXFLineType> theLineTypes = new Vector<DXFLineType>();

    public DXFTable() {
        //theLayers.add(new DXFLayer("default", DXF_Color.getDefaultColorIndex()));
    }

    public DXFTable(DXFBufferedReader br, DXFUnivers u) throws IOException {
        String ligne;
        Object obj;

        while ((ligne = br.readLine().trim()) != null && !ligne.equals("ENDTAB")) {
            if (ligne.toUpperCase().trim().equalsIgnoreCase("LAYER")) {
                obj = DXFLayer.read(br, u);

                if (obj != null) {
                    theLayers.addElement((DXFLayer) obj);
                }
            } else if (ligne.equalsIgnoreCase("LTYPE")) {
                obj = DXFLineType.read(br);

                if (obj != null) {
                    theLineTypes.addElement((DXFLineType) obj);

                }
            }
        }
    }
}
