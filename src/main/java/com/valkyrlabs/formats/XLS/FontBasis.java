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

import com.valkyrlabs.toolkit.ByteTools;

/**
 *  FBI: Font Basis (1060h)

	The FBI record stores font metrics.
	Chart use only.
	
Offset | Name | Size | Contents

4 | dmixBasis | 2 | Width of basis when font was applied

6 | dmiyBasis | 2 | Height of basis when font was applied

8 | twpHeightBasis | 2 | Font height applied

10 | scab | 2 | Scale basis

12 | ifnt | 2 | Index number into the font table	
 *
 */
public class FontBasis extends XLSRecord {
    /** 
	* serialVersionUID
	*/
	private static final long serialVersionUID = 6984935185426785077L;

	public void init(){
        super.init();        
    }
    public int getFontIndex() {
    	return ByteTools.readShort(this.getData()[8], this.getData()[9]);
    }
    
    public void setFontIndex(int id) {
        byte[] b = ByteTools.shortToLEBytes((short)id);
        this.getData()[8] = b[0];
        this.getData()[9] = b[1];    	
    }    

}
