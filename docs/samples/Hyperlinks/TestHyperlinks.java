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
﻿package docs.samples.Hyperlinks;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.valkyrlabs.OpenXLS.CellHandle;
import com.valkyrlabs.OpenXLS.WorkBookHandle;
import com.valkyrlabs.OpenXLS.WorkSheetHandle;
import com.valkyrlabs.formats.XLS.CellNotFoundException;
import com.valkyrlabs.formats.XLS.CellPositionConflictException;
import com.valkyrlabs.formats.XLS.SheetNotFoundException;
import com.valkyrlabs.toolkit.Logger;

/**
 * This Class Demonstrates the basic functionality of Hyperlink handling in OpenXLS.
 */
public class TestHyperlinks{
	String wd = System.getProperty("user.dir")+"/docs/samples/Hyperlinks/"; 

    /**
     * Demonstrates setting hyperlinks on cells with 
     * various settings.
     * 
     * Jan 18, 2010
     */
    public static void main(String[] args){
        TestHyperlinks t = new TestHyperlinks();
        Logger.logInfo("Testing creation of hyperlinks.");
		t.testHyperlinks();
		Logger.logInfo("Done testing hyperlinks.");
    }
    
    /**
     * run test
     * 
     * Jan 18, 2010
     */
    void testHyperlinks(){    
        WorkBookHandle tbo = new WorkBookHandle();
        String sheetname = "Sheet1";
        WorkSheetHandle sheet1 = null;
        try{
            sheet1 = tbo.getWorkSheet(sheetname);
        }catch (SheetNotFoundException e){com.valkyrlabs.OpenXLS.util.Logger.log("couldn't find worksheet" + e);}            
        
        try{
            
           tbo.copyWorkSheet(sheetname,"copy of "+sheetname);
        }catch(SheetNotFoundExceptioncom.valkyrlabs.OpenXLS.util.Logger.log(e);}

        String st4 = "G";
        
        try{
            // CellHandle salary3 = sheet1.getCell(st4);
            String ht="E3:E10";
            for(int t = 3; t<=10;t++){
                sheet1.add("OpenXLS Home Page","E" + t);
                CellHandle link1 = sheet1.getCell("E"+t);
                sheet1.moveCell(link1,st4 + t);
                link1.setURL("http://www.valkyrlabs.com/estore/product_detail.jsp?product_group_id=1");
            }

        }catch(CellNotFoundException e){com.valkyrlabs.OpenXLS.util.Logger.log(e);}
        catch(CellPositionConflictException r){com.valkyrlabs.OpenXLS.util.Logger.log(r);} 
        testWrite(tbo);
    }
    
    public void testWrite(WorkBookHandle b){
        try{
      	    java.io.File f = new java.io.File(wd + "testHyperlinks_out.xls");
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bbout = new BufferedOutputStream(fos);
            b.write(bbout);
            bbout.flush();
		    fos.close();
      	} catch (java.io.IOException e){Logger.logInfo("IOException in Tester.  "+e);}  
    }
    
   
}