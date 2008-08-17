package nl.b3p.geotools.data.dxf.header;

import java.awt.geom.Point2D;
import java.io.EOFException;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import nl.b3p.geotools.data.dxf.DXFLineNumberReader;
import nl.b3p.geotools.data.dxf.entities.DXFPoint;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;

public class DXFHeader implements DXFConstants {

    public DXFPoint _LIMMIN;
    public DXFPoint _LIMMAX;
    public DXFPoint _EXTMIN;
    public DXFPoint _EXTMAX;
    public int _FILLMODE;
    public String _ACADVER;

    public DXFHeader() {
        _LIMMIN = new DXFPoint(new Point2D.Double(0, 0));
        _LIMMAX = new DXFPoint(new Point2D.Double(100, 100));
        _EXTMIN = new DXFPoint(new Point2D.Double(100, 100));
        _EXTMAX = new DXFPoint(new Point2D.Double(50, 50));
        _FILLMODE = 0;
        _ACADVER = "AC1006";
    }

    public DXFHeader(DXFPoint limmin, DXFPoint limmax, DXFPoint extmin, DXFPoint extmax, int fillmode, String version) {
        _LIMMIN = limmin;
        _LIMMAX = limmax;
        _EXTMIN = extmin;
        _EXTMAX = extmax;
        _FILLMODE = fillmode;
        _ACADVER = version;
    }

    public static DXFHeader read(DXFLineNumberReader br) throws IOException {
        Point2D.Double limmin = null;
        Point2D.Double limmax = null;
        Point2D.Double extmin = null;
        Point2D.Double extmax = null;
        int fillmode = 0;
        String version = "AC1006";


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
                    String type = cvp.getStringValue();
                    if (type.equals(ENDSEC)) {
                        doLoop = false;
                        break;
                    }
                    break;
                case VARIABLE_NAME:
                    String variableName = cvp.getStringValue();
                    double x = 0,
                     y = 0;
                    int tfillmode = 0;
                    String tversion = null;

                    boolean doLoop2 = true;
                    while (doLoop2) {
                        cvp = new DXFCodeValuePair();
                        try {
                            gc = cvp.read(br);
                        } catch (DXFParseException ex) {
                            throw new IOException("DXF parse error", ex);
                        } catch (EOFException e) {
                            doLoop2 = false;
                            doLoop = false;
                            break;
                        }

                        switch (gc) {
                            case TYPE:
                                type = cvp.getStringValue();
                                if (type.equals(ENDSEC)) {
                                    doLoop = false;
                                    doLoop2 = false;
                                    break;
                                }
                                break;
                            case X_1:
                                x = cvp.getDoubleValue();
                                break;
                            case Y_1:
                                y = cvp.getDoubleValue();
                                break;
                            case TEXT:
                                tversion = cvp.getStringValue();
                                break;
                            case INT_1:
                                tfillmode = cvp.getIntValue();
                                break;
                            default:
                        }
                    }
                    if (variableName.equals($LIMMIN)) {
                        limmin = new Point2D.Double(x, y);
                    } else if (variableName.equals($LIMMAX)) {
                        limmax = new Point2D.Double(x, y);
                    } else if (variableName.equals($EXTMIN)) {
                        extmin = new Point2D.Double(x, y);
                    } else if (variableName.equals($EXTMAX)) {
                        extmax = new Point2D.Double(x, y);
                    } else if (variableName.equals($ACADVER)) {
                        version = tversion;
                    } else if (variableName.equals($FILLMODE)) {
                        fillmode = tfillmode;
                    }
                    break;
                default:
                    break;
            }
        }

        return new DXFHeader(new DXFPoint(limmin, -1, null, 1, 1),
                new DXFPoint(limmax, -1, null, 1, 1),
                new DXFPoint(extmin, -1, null, 1, 1),
                new DXFPoint(extmax, -1, null, 1, 1),
                fillmode, version);
    }
}
