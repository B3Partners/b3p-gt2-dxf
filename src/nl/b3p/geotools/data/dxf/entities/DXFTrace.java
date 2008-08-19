package nl.b3p.geotools.data.dxf.entities;

import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.io.IOException;

import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFTrace extends DXFSolid {

    private static final Log log = LogFactory.getLog(DXFTrace.class);

    public DXFTrace() {
        super();
    }

    public DXFTrace(DXFPoint p1, DXFPoint p2, DXFPoint p3, DXFPoint p4, double thickness, int c, DXFLayer l, int visibility, DXFLineType lineType) {
        super(p1, p2, p3, p4, thickness, c, l, visibility, lineType);
    }

    public DXFTrace(DXFTrace trace) {
        super(trace._p1, trace._p2, trace._p3, trace._p4, trace._thickness, trace._color, trace._refLayer, 0, trace._lineType);
    }

    public static DXFEntity read(DXFLineNumberReader br, DXFUnivers univers) throws IOException {
        int sln = br.getLineNumber();
        log.debug(">>Enter at line: " + sln);

        int visibility = 0;
        DXFSolid s = (DXFSolid) DXFSolid.read(br, univers);
        if (!s.isVisible) {
            visibility = 1;
        }
        DXFTrace e = new DXFTrace(s._p1, s._p2, s._p3, s._p4, s._thickness, s._color, s._refLayer, visibility, s._lineType);
        e.setType(DXFEntity.TYPE_UNSUPPORTED);
        e.setStartingLineNumber(sln);

        log.debug(e.toString());
        return e;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("DXFTrace [");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append(": ");
        s.append(", ");
        s.append("]");
        return s.toString();
    }
}
