package nl.b3p.geotools.data.dxf.header;

import java.io.IOException;
import java.util.Vector;


import nl.b3p.geotools.data.dxf.entities.DXFBufferedReader;

public class DXFLineType {

    private float _motif[] = parseTxt("_");
    public String _name = "myLineType.0";	// 2
    public String _value = "";					// 3
    public float _length = 0;					// 40
    public float _count = 0;					// 73
    public Vector<Float> _spacing = new Vector<Float>();	// 49

    //http://java.sun.com/developer/JDCTechTips/2003/tt0520.html
    public DXFLineType() {
    }

    public DXFLineType(String nom, String value, float length, float count, Vector<Float> spacing) {
        _name = nom;
        _value = value;
        _length = length;
        _count = count;

        if (spacing != null) {
            _spacing = spacing;
        }
        _motif = parseDXF();

    }

    public static DXFLineType read(DXFBufferedReader br) throws IOException {
        String ligne, ligne_temp, value = "", name = "";
        Vector<Float> spacing = new Vector<Float>();
        float count = 0, length = 0;

        while ((ligne = br.readLine()) != null && !ligne.equalsIgnoreCase("0")) {
            ligne_temp = ligne;
            ligne = br.readLine();

            if (ligne_temp.equalsIgnoreCase("2")) {
                name = ligne;
            } else if (ligne_temp.equalsIgnoreCase("3")) {
                value = ligne;
            } else if (ligne_temp.equalsIgnoreCase("73")) {
                count = Float.parseFloat(ligne);
            } else if (ligne_temp.equalsIgnoreCase("40")) {
                length = Float.parseFloat(ligne);
                ;
            } else if (ligne_temp.equalsIgnoreCase("49")) {
                spacing.add(Float.parseFloat(ligne));
            } else {
//                myLog.writeLog("Unknown :" + ligne_temp + " (" + ligne + ")");
            }
        }

        if (value.equals("") && name.equals("")) {
            return null;
        } else {
            return new DXFLineType(name, value, length, count, spacing);
        }
    }

    public String toString() {
        return _value + " (" + _name + ")";
    }

    public static float[] parseTxt(String s) {
        boolean no = false;
        boolean incr = false;
        s = s.trim();
        if (s.length() % 2 == 1) {
            s += " ";
        }
        char[] strChr = new char[s.length()];
        strChr = s.toCharArray();
        int end = strChr.length - (strChr.length % 2);

        float[] floatLine = new float[end * 2];
        int j = 0;
        for (int i = 0; i < floatLine.length; i++) {
            if (strChr[j] == ' ') {
                floatLine[i] = 0.0f;
                incr = true;
            } else if (strChr[j] == '.') {
                floatLine[i] = 1.0f;
            } else if (strChr[j] == '-') {
                floatLine[i] = 5.0f;
            } else if (strChr[j] == '_') {
                floatLine[i] = 10.0f;
                no = true;
            } else {
                floatLine[i] = 3.0f;
            }
            i++;
            if (no) {
                floatLine[i] = 0.0f;
            } else if (incr) {
                floatLine[i] = 10.0f;
            } else {
                floatLine[i] = 3.0f;
            }
            j++;
            no = false;
            incr = false;
        }
        if (floatLine.length > 0) {
            return floatLine;
        } else {
            return new float[]{1.0f, 0.0f};
        }
    }

    public float[] parseDXF() {
        if (_count != this._spacing.size()) {
            return DXFTable.defautMotif;
        }
        float[] ret = new float[(int) _count];

        for (int i = 0; i < _count; i++) {
            ret[i] = ((Math.abs(_spacing.elementAt(i)) * 100) / this._length) / 10;
        }

        if (ret.length == 0) {
            ret = DXFTable.defautMotif;
        }
        return ret;
    }

    public float[] getMotif() {
        return _motif;
    }
}
