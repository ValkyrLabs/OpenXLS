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
import java.util.ArrayList;

import com.valkyrlabs.toolkit.ByteTools;


/** <b>Palette: Defined Colors (92h)</b><br>

   Describe Colors in the file.
   
   <p><pre>
    offset  name            size    contents
    ---
    4       ccv             2       count of  colors
    var     rgch            var     4 byte color data
    
    </p></pre>

**/
public final class Palette extends com.valkyrlabs.formats.XLS.XLSRecord 
{
    /** 
	* serialVersionUID
	*/
	private static final long serialVersionUID = 157670739931392705L;
	ArrayList colorvect = new ArrayList();
    int ccv = -1;
    
	public void init(){
        super.init(); 
        ccv = ByteTools.readShort(this.getByteAt(0),this.getByteAt(1));
        this.getData();
        int pos = 2;
        for(int d = 0;d<ccv;d++){
            this.getColorTable()[d+8]= new java.awt.Color((data[pos]<0?255+data[pos]:data[pos]), (data[pos+1]<0?255+data[pos+1]:data[pos+1]), (data[pos+2]<0?255+data[pos+2]:data[pos+2]));
            pos+=4;
        }
    }

}