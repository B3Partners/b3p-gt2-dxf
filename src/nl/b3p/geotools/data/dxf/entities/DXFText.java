package nl.b3p.geotools.data.dxf.entities;

import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.io.IOException;


import nl.b3p.geotools.data.dxf.DXFUnivers;
import nl.b3p.geotools.data.dxf.header.DXFLayer;
import nl.b3p.geotools.data.dxf.header.DXFLineType;
import nl.b3p.geotools.data.dxf.header.DXFTable;

public class DXFText extends DXFEntity {

    public DXFPoint _point = new DXFPoint(); // 10 ,20
    protected String _value = ""; // 1
    protected double _height = 0; // 40
    protected double _rotation = 0; // 50
    protected int _align = 0; // 72
    protected String _style = ""; // 7
    protected double _angle = 0; // 51
    protected double _zoomfactor = 1; // 41
    protected Rectangle2D.Double _r = new Rectangle2D.Double();

    public DXFText(double x, double y, String value, double rotation, double thickness, double height, int align, String style, int c, DXFLayer l, double angle, double zoomFactor, int visibility, DXFLineType lineType) {
        super(c, l, visibility, lineType, thickness);
        _point = new DXFPoint(x, y, c, l, visibility, thickness);
        _value = value;
        _rotation = rotation;
        _height = height;
        _align = align;
        _style = style;
        _angle = angle;
        _zoomfactor = zoomFactor;
    }

    public DXFText() {
        super(-1, null, 0, null, DXFTable.defaultThickness);
    }

    public DXFText(DXFText text) {
        super(text._color, text._refLayer, 0, text._lineType, text._thickness);
        _point = new DXFPoint(text._point.X(), text._point.Y(), text._color, text._refLayer, 0, text._thickness);
        _value = text._value;
        _rotation = text._rotation;
        _height = text._height;
        _align = text._align;
        _style = text._style;
        _angle = text._angle;
        _zoomfactor = text._zoomfactor;
    }

    public void setVal(String s) {
        _value = s;
    }

    public String getVal() {
        return _value;
    }

    public void appendVal(char c) {
        _value += c;
    }

    public void delChar() {
        _value = _value.substring(0, _value.length() - 1);
    }

    private double getWeight(double zoomfactor) {

        double value = TextAttribute.WEIGHT_REGULAR;

        if (zoomfactor <= 0.5) {
            value = TextAttribute.WEIGHT_EXTRA_LIGHT;
        } else if (zoomfactor <= 0.75) {
            value = TextAttribute.WEIGHT_LIGHT;
        } else if (zoomfactor <= 0.875) {
            value = TextAttribute.WEIGHT_DEMILIGHT;
        } else if (zoomfactor <= 1) {
            value = TextAttribute.WEIGHT_REGULAR;
        } else if (zoomfactor <= 1.25) {
            value = TextAttribute.WEIGHT_SEMIBOLD;
        } else if (zoomfactor <= 1.5) {
            value = TextAttribute.WEIGHT_MEDIUM;
        } else if (zoomfactor <= 1.75) {
            value = TextAttribute.WEIGHT_DEMIBOLD;
        } else if (zoomfactor <= 2.0) {
            value = TextAttribute.WEIGHT_BOLD;
        } else if (zoomfactor <= 2.25) {
            value = TextAttribute.WEIGHT_HEAVY;
        } else if (zoomfactor <= 2.50) {
            value = TextAttribute.WEIGHT_EXTRABOLD;
        } else {
            value = TextAttribute.WEIGHT_ULTRABOLD;
        }

        return value;
    }

    private double getWidth(double zoomfactor) {

        double value = TextAttribute.WIDTH_REGULAR;

        if (zoomfactor <= 0.75) {
            value = TextAttribute.WIDTH_CONDENSED;
        } else if (zoomfactor <= 0.875) {
            value = TextAttribute.WIDTH_SEMI_CONDENSED;
        } else if (zoomfactor <= 1.0) {
            value = TextAttribute.WIDTH_REGULAR;
        } else if (zoomfactor <= 1.25) {
            value = TextAttribute.WIDTH_SEMI_EXTENDED;
        } else {
            value = TextAttribute.WIDTH_EXTENDED;
        }

        return value;
    }

    public static DXFText read(DXFBufferedReader br, DXFUnivers univers) throws IOException {
        DXFLayer l = null;
        String ligne = "",
                ligne_temp = "",
                value = "",
                style = "STANDARD";
        int visibility = 0, align = 0, c = -1;
        DXFLineType lineType = null;
        double x = 0,
                y = 0,
                angle = 0,
                rotation = 0,
                zoomfactor = 1,
                thickness = DXFTable.defaultThickness,
                height = 0;

//		myLog.writeLog(">> myText");
        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("10")) {
                x = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("20")) {
                y = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("1")) {
                value = ligne;
            } else if (ligne_temp.equalsIgnoreCase("50")) {
                rotation = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("39")) {
                thickness = (float) Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("40")) {
                height = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("51")) {
                angle = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("41")) {
                zoomfactor = Double.parseDouble(ligne);
            } else if (ligne_temp.equalsIgnoreCase("72")) {
                align = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("8")) {
                l = univers.findLayer(ligne);
            } else if (ligne_temp.equalsIgnoreCase("62")) {
                c = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("7")) {
                style = ligne;
            } else if (ligne_temp.equalsIgnoreCase("60")) {
                visibility = Integer.parseInt(ligne);
            } else if (ligne_temp.equalsIgnoreCase("6")) {
                lineType = univers.findLType(ligne);
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + "(" + ligne + ")");
            }
        }
        return new DXFText(x, y, value, rotation, thickness, height, align, style, c, l, angle, zoomfactor, visibility, lineType);
    }
}
