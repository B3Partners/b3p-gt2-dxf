package nl.b3p.geotools.data.dxf.entities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.algorithm.Angle;
import nl.b3p.geotools.data.GeometryType;
import nl.b3p.geotools.data.dxf.header.DXFBlockReference;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.parser.DXFColor;
import nl.b3p.geotools.data.dxf.parser.DXFConstants;
import nl.b3p.geotools.data.dxf.parser.DXFUnivers;


public abstract class DXFEntity implements DXFConstants {

    private static final Log log = LogFactory.getLog(DXFEntity.class); 
    protected GeometryType geometryType;

    /* feature write */
    protected String _name = null;

    protected String _text = null;
    protected String _textposhorizontal = null;
    protected String _textposvertical = null;
    protected Double _textheight = null;
    protected Double _textrotation = null;

    protected DXFLayer _refLayer;

    protected int _color;
    protected DXFLineType _lineType;
    protected double _thickness;

    protected boolean visible = true;

    protected int startingLineNumber = -1;
    protected String errorDescription = null;
    
    protected Geometry geometry = null;

    /* dxf read */
    protected DXFUnivers univers;

    private double _entRotationAngle = 0.0;
    protected Coordinate _entBase = new Coordinate(0.0, 0.0);

    public DXFEntity(DXFEntity newEntity) {
        this(newEntity.getColor(), newEntity.getRefLayer(), 1, newEntity.getLineType(), newEntity.getThickness());
    }

    public DXFEntity(int c, DXFLayer l, int visibility, DXFLineType lineType, double thickness) {

        _refLayer = l;

        if (lineType != null && lineType._name.equalsIgnoreCase("BYLAYER") && _refLayer != null) {
            //TODO waar zit linetype in layer?
        }
        _lineType = lineType;

        if (!(this instanceof DXFBlockReference) && !(this instanceof DXFLayer)) {
            if ((c < 0) || (c == 255 && _refLayer != null)) {
                if (_refLayer == null) {
                    c = DXFColor.getDefaultColorIndex();
                } else {
                    c = _refLayer._color;
                }
            }
        }
        _color = c;
        _thickness = thickness;

        if (visibility == 0) {
            visible = true;
        } else {
            visible = false;
        }
    }

    public void setBase(Coordinate coord) {
        this._entBase = coord;
    }

    public void setAngle(double angle) {
        this._entRotationAngle = angle;
    }

    abstract public DXFEntity translate(double x, double y);

    protected Coordinate rotateAndPlace(Coordinate coord) {
        Coordinate[] array = new Coordinate[1];
        array[0] = coord;

        return rotateAndPlace(array)[0];
    }

    protected Coordinate[] rotateAndPlace(Coordinate[] coordarray) {
        for (int i = 0; i < coordarray.length; i++) {
            coordarray[i] = rotateCoordDegrees(coordarray[i], _entRotationAngle);
            coordarray[i].x += _entBase.x;
            coordarray[i].y += _entBase.y;
        }
        return coordarray;
    }

    private Coordinate rotateCoordDegrees(Coordinate coord, double angle) {
        angle = Angle.toRadians(angle);
        angle = Angle.angle(coord) + angle;

        Coordinate newCoord = new Coordinate(coord);
        double radius = Math.sqrt(Math.pow(coord.x, 2) + Math.pow(coord.y, 2));

        newCoord.x = radius * Math.cos(angle);
        newCoord.y = radius * Math.sin(angle);

        return newCoord;
    }

    @Override
    abstract public DXFEntity clone();

    public Geometry getGeometry() {
        if (geometry == null) {
            updateGeometry();
        }
        return geometry;
    }

    public void updateGeometry() {
        geometry = getUnivers().getErrorGeometry();
        addError("error geometry created for: " + this.getClass().toString());
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public String getText() {
        return _text;
    }

    public void setText(String _text) {
        this._text = _text;
    }

    public Double getTextheight() {
        return _textheight;
    }

    public void setTextheight(Double _textheight) {
        this._textheight = _textheight;
    }

    public String getTextposhorizontal() {
        return _textposhorizontal;
    }

    public void setTextposhorizontal(String _textposhorizontal) {
        this._textposhorizontal = _textposhorizontal;
    }

    public String getTextposvertical() {
        return _textposvertical;
    }

    public void setTextposvertical(String _textposvertical) {
        this._textposvertical = _textposvertical;
    }

    public Double getTextrotation() {
        return _textrotation;
    }

    public void setTextrotation(Double _textrotation) {
        this._textrotation = _textrotation;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public int getStartingLineNumber() {
        return startingLineNumber;
    }

    public void setStartingLineNumber(int startingLineNumber) {
        this.startingLineNumber = startingLineNumber;
    }

    public GeometryType getType() {
        return geometryType;
    }

    public void setType(GeometryType geometryType) {
        this.geometryType = geometryType;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public DXFLineType getLineType() {
        return _lineType;
    }

    public String getLineTypeName() {
        if (_lineType == null) {
            return DXFLineType.DEFAULT_NAME;
        }
        return _lineType._name;
    }

    public void setLineType(DXFLineType lineType) {
        this._lineType = lineType;
    }

    public int getColor() {
        return _color;
    }

    public String getColorRGB() {
        return DXFColor.getColorRGB(_color);
    }

    public void setColor(int color) {
        this._color = color;
    }

    public DXFLayer getRefLayer() {
        return _refLayer;
    }

    public String getRefLayerName() {
        if (_refLayer == null) {
            return DXFLayer.DEFAULT_NAME;
        }
        return _refLayer.getName();
    }

    public void setRefLayer(DXFLayer refLayer) {
        this._refLayer = refLayer;
    }

    public double getThickness() {
        return _thickness;
    }

    public void setThickness(double thickness) {
        this._thickness = thickness;
    }

    public DXFUnivers getUnivers() {
        return univers;
    }

    public void setUnivers(DXFUnivers univers) {
        this.univers = univers;
    }

    /**
     * Called when an error occurs but that error is constrained to a single
     * feature/subgeometry. Try to continue parsing features, but do set parseError
     * property to true and add and error message.
     * @param msg
     */
    public void addError(String msg) {
        if (errorDescription == null) {
            errorDescription = "entry starting line " + getStartingLineNumber() + ": " + msg;
        } else {
            errorDescription += "; " + msg;
        }
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
