package nl.b3p.geotools.data.dxf.header;

import java.awt.geom.Point2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.entities.DXFBufferedReader;
import nl.b3p.geotools.data.dxf.entities.DXFPoint;

public class DXFHeader {

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

    public static DXFHeader read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        String ligne, version = "AC1006";
        double x = 0, y = 0;
        Point2D.Double limmin = null;
        Point2D.Double limmax = null;
        Point2D.Double extmin = null;
        Point2D.Double extmax = null;
        int fillmode = 0;

        while ((ligne = br.readLine()) != null && !ligne.equals("0")) {
            ligne = br.readLine();
            if (ligne.equals("0")) {
                break;
            } else if (ligne.equalsIgnoreCase("$LIMMIN")) {
                x = 0;
                y = 0;
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("10")) {
                    ligne = br.readLine();
                    x = Double.parseDouble(ligne);
                }
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("20")) {
                    ligne = br.readLine();
                    y = Double.parseDouble(ligne);
                }

                limmin = new Point2D.Double(x, y);
            } else if (ligne.equalsIgnoreCase("$LIMMAX")) {
                x = 0;
                y = 0;
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("10")) {
                    ligne = br.readLine();
                    x = Double.parseDouble(ligne);
                }
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("20")) {
                    ligne = br.readLine();
                    y = Double.parseDouble(ligne);
                }

                limmax = new Point2D.Double(x, y);
            } else if (ligne.equalsIgnoreCase("$EXTMIN")) {
                x = 0;
                y = 0;
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("10")) {
                    ligne = br.readLine();
                    x = Double.parseDouble(ligne);
                }
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("20")) {
                    ligne = br.readLine();
                    y = Double.parseDouble(ligne);
                }

                extmin = new Point2D.Double(x, y);
            } else if (ligne.equalsIgnoreCase("$EXTMAX")) {
                x = 0;
                y = 0;
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("10")) {
                    ligne = br.readLine();
                    x = Double.parseDouble(ligne);
                }
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("20")) {
                    ligne = br.readLine();
                    y = Double.parseDouble(ligne);
                }

                extmax = new Point2D.Double(x, y);
            } else if (ligne.equalsIgnoreCase("$ACADVER")) {
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("1")) {
                    ligne = br.readLine();
                    version = ligne;
                }
            } else if (ligne.equalsIgnoreCase("$FILLMODE")) {
                ligne = br.readLine();
                if (ligne.equalsIgnoreCase("70")) {
                    ligne = br.readLine();
                    if (!ligne.equalsIgnoreCase("0")) {
                        fillmode = Integer.parseInt(ligne);
                    }
                }
            }
        }

        return new DXFHeader(new DXFPoint(limmin, -1, null, 1, 1),
                new DXFPoint(limmax, -1, null, 1, 1),
                new DXFPoint(extmin, -1, null, 1, 1),
                new DXFPoint(extmax, -1, null, 1, 1),
                fillmode, version);
    }


}
