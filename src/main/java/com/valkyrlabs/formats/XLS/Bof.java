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
package com.valkyrlabs.formats.XLS;

import com.valkyrlabs.formats.LEO.InvalidFileException;
import com.valkyrlabs.toolkit.ByteTools;
import com.valkyrlabs.toolkit.Logger;


/**
 * <pre><b>Bof: Beginning of File Stream 0x809</b><br>
 *
 * Marks the beginning of an XLS file Stream including Boundsheets
 *
 * <p><pre>
 * offset  name        size    contents
 * ---
 * 0       vers        1       version:
 * 1       bof         1       0x09
 * ...
 *
 * </p></pre>
 */
public final class Bof extends UnencryptedXLSRecord {


    private static final long serialVersionUID = 3005631881544437570L;
    short grbit;
    String xlsver = "";
    int oldlen = -1;

    public String toString() {
        return super.toString() + " lbplypos: " + this.getLbPlyPos();
    }

    /**
     * Set the offset for this BOF
     */
    public void setOffset(int s) {
        super.setOffset(s);
        if (worksheet != null)
            if (isSheetBof() || isVBModuleBof() || (worksheet.getMyBof() == this))
                worksheet.setLbPlyPos(this.getLbPlyPos());
    }

    boolean isValidBIFF8() {
        return oldlen == 20;
    }

    String getXLSVersionString() {
        return xlsver;
    }

    /**
     * Initialize the BOF record
     */
    public void init() {
        super.init();
        this.getData();
        grbit = ByteTools.readShort(this.getByteAt(2), this.getByteAt(3));
        int compat = ByteTools.readInt(this.getByteAt(12), this.getByteAt(13), this.getByteAt(14), this.getByteAt(15)); // 1996
        xlsver = compat + "";
        oldlen = this.getLength();
        if (oldlen < 16) {
            Logger.logErr("Not Excel '97 (BIFF8) or later version.  Unsupported file format.");
            throw new InvalidFileException("InvalidFileException: Not Excel '97 (BIFF8) or later version.  Unsupported file format.");
        }
    }

    /**
     * this is equal to the lbPlyPos stored in
     * the Boundsheet associated with this Bof
     */
    long getLbPlyPos() {
        if (!isValidBIFF8()) return offset + 8;
        return offset;
    }

    /**
     * @return Returns the sheetbof.
     */
    public boolean isSheetBof() {
        return (grbit & 0x10) == 0x10;
    }

    public boolean isVBModuleBof() {
        return (grbit & 0x06) == 0x06;

    }

    /**
     * @return Returns the sheetbof.
     */
    public boolean isChartBof() {
        return (grbit & 0x20) == 0x20;
    }

    /**
     * @param sheetbof The sheetbof to set.
     */
    public void setSheetBof() {
        grbit = 0x10;
    }
}