/*
 * --------- BEGIN COPYRIGHT NOTICE ---------
 * Copyright 2002-2012 Extentech Inc.
 * Copyright 2013 Infoteria America Corp.
 *
 * This file is part of OpenXLS.
 *
 * OpenXLS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * OpenXLS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with OpenXLS.  If not, see
 * <http://www.gnu.org/licenses/>.
 * ---------- END COPYRIGHT NOTICE ----------
 */
package com.valkyrlabs.formats.XLS.formulas;

import com.valkyrlabs.toolkit.ByteTools;


/*
    Ptg that stores an integer value
    
    Offset  Name       Size     Contents
    ------------------------------------
    0       w           2       An unsigned integer value
    
 * @see Ptg
 * @see Formula

    
*/
public class PtgInt extends GenericPtg implements Ptg {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2129624418815329359L;
    int val;

    public PtgInt() {
    }

    /*
     * constructer to create ptgint's on the fly, from formulas
     */
    public PtgInt(int i) {
        val = i;
        this.updateRecord();
    }

    public boolean getIsOperand() {
        return true;
    }

    /**
     * return the human-readable String representation of
     */
    public String getString() {
        return String.valueOf(val);
    }

    public void init(byte[] b) {
        ptgId = b[0];
        record = b;
        this.populateVals();
    }

    // 0 to 65535 - outside of these bounds must be a PtgNumber
    private void populateVals() {
        byte b = 0;
        int s = ByteTools.readInt(record[1], record[2], b, b);
        val = s;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int i) {
        val = i;
        this.updateRecord();
    }

    public int getIntVal() {
        return val;
    }

    public Object getValue() {
        Integer i = Integer.valueOf(val);
        return i;
    }

    public boolean getBooleanVal() {
        return val == 1;
    }

    public void updateRecord() {
        byte[] tmp = new byte[1];
        tmp[0] = PTG_INT;
        byte[] brow = ByteTools.shortToLEBytes((short) val);
        tmp = ByteTools.append(brow, tmp);
        record = tmp;
    }

    public int getLength() {
        return PTG_INT_LENGTH;
    }

    public String toString() {
        return String.valueOf(this.getVal());
    }

}