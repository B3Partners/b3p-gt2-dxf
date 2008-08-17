package nl.b3p.geotools.data.dxf.header;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import nl.b3p.geotools.data.dxf.entities.DXFEntity;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;

public class DXFLayer extends DXFEntity implements DXFConstants {

    public int _flag = 0;
    public String _nom;
    public Vector<DXFEntity> theEnt = new Vector<DXFEntity>();

    public DXFLayer(String nom, int c) {
        super(c, null, 0, null, DXFTables.defaultThickness);
        _nom = nom;
    }

    public DXFLayer(String nom, int c, int flag) {
        super(c, null, 0, null, DXFTables.defaultThickness);
        _nom = nom;
        _flag = flag;
    }

    @Override
    public void setVisible(boolean bool) {
        isVisible = bool;
        for (int i = 0; i < theEnt.size(); i++) {
            ((DXFEntity) theEnt.get(i)).setVisible(bool);
        }
    }

    public static DXFLayer read(DXFLineNumberReader br) throws NumberFormatException, IOException {
        String name = "";
        int f = 0, color = 0;

        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error", ex);
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                case VARIABLE_NAME:
                    br.reset();
                    doLoop = false;
                    break;
                case NAME:
                    name = cvp.getStringValue();
                    break;
                case COLOR:
                    color = cvp.getShortValue();
                    break;
                case INT_1:
                    f = cvp.getIntValue();
                    break;
                default:
            }
        }

        DXFLayer l = new DXFLayer(name, color, f);
        if (color < 0) {
            l.setVisible(false);
        }
        return l;
    }
}

