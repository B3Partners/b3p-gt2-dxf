package nl.b3p.geotools.data.dxf.entities;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import nl.b3p.geotools.data.dxf.parser.DXFLineNumberReader;
import java.io.EOFException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import nl.b3p.geotools.data.GeometryType;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTables;
import nl.b3p.geotools.data.dxf.parser.DXFCodeValuePair;
import nl.b3p.geotools.data.dxf.parser.DXFGroupCode;
import nl.b3p.geotools.data.dxf.parser.DXFParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DXFText extends DXFEntity {

    private static final Log log = LogFactory.getLog(DXFText.class);

    private Double x = null, y = null;

    public DXFText(DXFText newText) {
        this(newText.getColor(), newText.getRefLayer(), 0, newText.getLineType(), 0.0);

        setStartingLineNumber(newText.getStartingLineNumber());
        setType(newText.getType());
        setUnivers(newText.getUnivers());
    } 

    public DXFText(int c, DXFLayer l, int visibility, DXFLineType lineType, double thickness) {
        super(c, l , visibility, lineType, thickness);
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public static DXFText read(DXFLineNumberReader br, DXFUnivers univers, boolean isMText) throws IOException {

        DXFText t = new DXFText(0, null, 0, null, DXFTables.defaultThickness);
        t.setUnivers(univers);
        t.setName(isMText ? "DXFMText" : "DXFText");
        t.setTextrotation(0.0);

        t.setStartingLineNumber(br.getLineNumber());

        DXFCodeValuePair cvp = null;
        DXFGroupCode gc = null;

        // MTEXT direction vector
        Double directionX = null, directionY = null;

        String textposhor = "left";
        String textposver = "bottom";

        boolean doLoop = true;
        while (doLoop) {
            cvp = new DXFCodeValuePair();
            try {
                gc = cvp.read(br);
            } catch (DXFParseException ex) {
                throw new IOException("DXF parse error" + ex.getLocalizedMessage());
            } catch (EOFException e) {
                doLoop = false;
                break;
            }

            switch (gc) {
                case TYPE:
                    // geldt voor alle waarden van type
                    br.reset();
                    doLoop = false;
                    break;
                case X_1: //"10"
                    t.setX(cvp.getDoubleValue());
                    break;
                case Y_1: //"20"
                    t.setY(cvp.getDoubleValue());
                    break;
                case TEXT: //"1"
                    t.setText(processOrStripTextCodes(cvp.getStringValue()));
                    break;
                case ANGLE_1: //"50"
                    t.setTextrotation(cvp.getDoubleValue());
                    break;
                case X_2: // 11, X-axis direction vector
                    directionX = cvp.getDoubleValue();
                    break;
                case Y_2: // 21, Y-axis direction vector
                    directionY = cvp.getDoubleValue();
                    break;
                case THICKNESS: //"39"
                    t.setThickness(cvp.getDoubleValue());
                    break;
                case DOUBLE_1: //"40"
                    t.setTextheight(cvp.getDoubleValue());
                    break;
                case INT_2: // 71: MTEXT attachment point
                    switch(cvp.getShortValue()) {
                        case 1: textposver = "top";    textposhor = "left"; break;
                        case 2: textposver = "top";    textposhor = "center"; break;
                        case 3: textposver = "top";    textposhor = "right"; break;
                        case 4: textposver = "middle"; textposhor = "left"; break;
                        case 5: textposver = "middle"; textposhor = "center"; break;
                        case 6: textposver = "middle"; textposhor = "right"; break;
                        case 7: textposver = "bottom"; textposhor = "left"; break;
                        case 8: textposver = "bottom"; textposhor = "center"; break;
                        case 9: textposver = "bottom"; textposhor = "right"; break;
                    }
                    break;
                case INT_3: // 72: TEXT horizontal text justification type
                    
                    // komen niet helemaal overeen, maar maak voor TEXT en MTEXT hetzelfde 
                    
                    switch(cvp.getShortValue()) {
                        case 0: textposhor = "left"; break;
                        case 1: textposhor = "center"; break;
                        case 2: textposhor = "right"; break;
                        case 3: // aligned
                        case 4: // middle
                        case 5: // fit
                            // negeer, maar hier "center" van
                            textposhor = "center";
                    }
                    break;
                case INT_4:
                    switch(cvp.getShortValue()) {
                        case 0: textposver = "bottom"; break; // eigenlijk baseline
                        case 1: textposver = "bottom"; break;
                        case 2: textposver = "middle"; break;
                        case 3: textposver = "top"; break;
                    }
                    break;
                case LAYER_NAME: //"8"
                    t._refLayer = univers.findLayer(cvp.getStringValue());
                    break;
                case COLOR: //"62"
                    t.setColor(cvp.getShortValue());
                    break;
                case VISIBILITY: //"60"
                    t.setVisible(cvp.getShortValue() == 0);
                    break;
                default:
                    break;
            }
        }

        t.setTextposvertical(textposver);
        t.setTextposhorizontal(textposhor);

        if(isMText && directionX != null && directionY != null) {

            t.setTextrotation(calculateRotationFromDirectionVector(directionX, directionY));
            if(log.isDebugEnabled()) {
                log.debug(MessageFormat.format("MTEXT entity at line number %d: text pos (%.4f,%.4f), direction vector (%.4f,%.4f), calculated text rotation %.2f degrees",
                        t.getStartingLineNumber(),
                        t.getX(), t.getY(),
                        directionX, directionY,
                        t.getTextrotation()));
            }
        }

        t.setType(GeometryType.POINT);

        return t;
    }

    private static String processOrStripTextCodes(String text) {
        if(text == null) {
            return null;
        }

        // http://docs.autodesk.com/ACD/2010/ENU/AutoCAD%202010%20User%20Documentation/index.html?url=WS1a9193826455f5ffa23ce210c4a30acaf-63b9.htm,topicNumber=d0e123454

        text = text.replaceAll("%%[cC]", "Ø");

        text = text.replaceAll("\\\\[Pp]", "\r\n");
        text = text.replaceAll("\\\\[Ll~]", "");
        text = text.replaceAll(Pattern.quote("\\\\"), "\\");
        text = text.replaceAll(Pattern.quote("\\{"), "{");
        text = text.replaceAll(Pattern.quote("\\}"), "}");
        text = text.replaceAll("\\\\[CcFfHhTtQqWwAa].*;", "");
        return text;
    }

    private static double calculateRotationFromDirectionVector(double x, double y) {
        double rotation;

        // Hoek tussen vector (1,0) en de direction vector uit MText als theta:

        // arccos (theta) = inproduct(A,B) / lengte(A).lengte(B)
        // arccos (theta) = Bx / wortel(Bx^2 + By^2)

        // indien hoek in kwadrant III of IV, dan theta = -(theta-2PI)

        double length = Math.sqrt(x*x + y*y);

        if(length == 0) {
            rotation = 0;
        } else {
            double theta = Math.acos(x / length);

            if((x <= 0 && y <= 0) || (x >= 0 && y <= 0)) {
                theta = -(theta - 2*Math.PI);
            }

            // conversie van radialen naar graden
            rotation = theta * (180/Math.PI);

            if(Math.abs(360 - rotation) < 1e-4) {
                rotation = 0;
            }
        }
        return rotation;
    }

    @Override
    public Geometry getGeometry() {
        if (geometry == null) {
            updateGeometry();
        }
        return geometry;
    }

    @Override
    public void updateGeometry() {
        if(x != null && y != null) {
            Coordinate c = rotateAndPlace(new Coordinate(x, y));
            setGeometry(getUnivers().getGeometryFactory().createPoint(c));
        } else {
            setGeometry(null);
        }
    }

    @Override
    public DXFEntity translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public DXFEntity clone() {
        return new DXFText(this);
        //throw new UnsupportedOperationException();
    }
}
