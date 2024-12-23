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
package docs.samples.RoundTripReporting;

import java.sql.*;
import junit.framework.*;
import junit.runner.*;
import junit.textui.TestRunner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;

import com.valkyrlabs.ExtenBean.*;
import com.valkyrlabs.OpenXLS.*;
import com.valkyrlabs.OpenXLS.binder.*;
import com.valkyrlabs.dbutil.ConnectionPool;
import com.valkyrlabs.toolkit.LogOutputter;
import com.valkyrlabs.toolkit.Logger;


/** This  test program demonstrates the TimeSheet File Upload/Download functionality
 * 
 */
public class RoundTripReporting  implements LogOutputter{
    
    // the reportfactory handles CellBinding and output generation
    ExtenXLSReportFactory rptFactory = null;
    
    public static String UNICODEENCODING      = "UTF-16LE"; // "UnicodeLittleUnmarked";

    String wd = System.getProperty("user.dir")+"/docs/samples/RoundTripReporting/";

	ConnectionPool cp;
    

    public static void main(String[] args){
	    Logger.logInfo("TimeSheet Project Test Suite using OpenXLS v." + WorkBookHandle.getVersion());

		RoundTripReporting test = new RoundTripReporting();
		try {
			test.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.doit(1000);

    }


	
	/** THIS IS THE MAIN TEST METHOD
    
      */
    public void doit(int startnum) {

        /*     ---  Outbound (download) function ---
         	Read TimeSheet data objects passed from calling program.
         	TimeSheet will make one call to the converter for each TimeSheet to be processed
        */
        
        Map testdata = null;
        // setup TimeSheet data objects
        this.log("Loading DataObjects from data...");
        try {
            testdata = this.initTimeSheetDataObjects(startnum);
            if(testdata==null)return;
        }catch(DataAccessException ex) { // TODO: throw from empty TimeSheetInfos
            log("WARNING: Loading timeInfo from data failed: " + ex.toString());
        }
        this.log("done.");
        
//      Merge TimeSheet reference and user data with the  created Excel template into the correct cells of the Excel template
        this.log("Running reports...");
        	
//      initialize CellBinder with output RDF
        String file = "TimeSheet";
        com.valkyrlabs.OpenXLS.WorkBook outp = runreport(file, "_out"+startnum +".xls", testdata);
        this.log("done.");
        
/*     ---  Inbound (upload) function ---
        Read .xls file (consisting of the  template and user entered data)
		passed from the calling TimeSheet application. TimeSheet will make one call to the converter for each TimeSheet. 
*/
        this.log("Read in uploaded User template.");
   	String foutput = wd +"/output/" + file +"_out" + startnum + ".xls";
 	   	
   	// test a different output file
        WorkBookHandle input = new WorkBookHandle(foutput);
        this.log("done.");
        
        // get 'hidden' control values from a hidden sheet in the uploaded workbook
        // allows for versioning, session and user id tracking, etc.
        String type = "UNDEFINED";
        try {
            type = input.getWorkSheet("system").getCell("E1").getStringVal();
        }catch(Exception e) {
            ;
        }
        this.log("Uploaded Template Type is: " + type);
        
        DataObject uploaded_TimeSheet = null;

//     build the TimeSheet data objects with the user entered and calculated data from the Excel template (ignoring the reference fields)
        this.log("Convert to DataObject.");
        try {
//     read in template and generate output DataObject
           WorkBookBeanFactory fact = new WorkBookBeanFactory(this);
            uploaded_TimeSheet = fact.getInfoBeansFromWorkBook(input, rptFactory); // the main reverse-mapping method
        }catch(BeanInstantiationException e) {
            log("ERROR: DataObject Loading from input file " + input.toString() + " failed: " + e.toString());
        }
        this.log("done.");
    }

    
	
    public void setUp() 
	throws Exception{
		log("RoundTripReporting OpenXLS version: " + WorkBookHandle.getVersion());
		
		// install a custom ObjectValueAdapter if necessary
		if(false) {
		    ObjectValueAdapter adapto = null; // TODO: implement your own ObjectValueAdapter for custom bean value access 
		    System.getProperties().put("com.valkyrlabs.OpenXLS.binder.ObjectAdapter", adapto);
		}
		
	// Use the  JNDI Naming to set DataSource connection
	   try{
			// For testing purposes, use the Extentech InitialContextFactory
			//Properties env = System.getProperties();
			//env.put("java.naming.factory.initial" , "com.valkyrlabs.naming.InitialContextFactoryImpl");
			//Context initialContext = new InitialContext();
	   		
	        String driver="org.hsqldb.jdbcDriver", 
	   		dbURL="jdbc:hsqldb:hsql://localhost:6161", 
	   		username="sa" ,
	   		password="ECOMM_SOLUTION";
	       
			cp = new ConnectionPool(driver,dbURL,username,password);
			//initialContext.bind("jdbc/testDS", cp);
	   }catch(Exception e){
		   Logger.logErr("ERROR: Could not initialize the Connectionpool: " + e.toString());
			e.printStackTrace();
		}
	}

	private com.valkyrlabs.OpenXLS.WorkBook runreport(String rpt, String outfile, Map values){
	   // Create New Report Factory
	   String rdf = wd + rpt + ".rdf";

	   // Create New Report Factory
	   rptFactory = new ExtenXLSReportFactory(this, wd);
	   rptFactory.setDebugLevel(10);
	   /* The report Factory is initialized with a Report Definition File
		* which will determine the behavior and data of generated reports. 
		*/
	   try{
		   rptFactory.init(rdf);
	   }catch(Exception e){
		   log(e.toString());
	   }
	   String foutput = wd +"/output/" + rpt + outfile;
	   com.valkyrlabs.OpenXLS.WorkBook ret = null;
		try{
			// run the report, returns a byte array for an Excel file
			 ret = rptFactory.generateWorkBookHandle(values);
			 testWrite(ret,foutput);
		}catch(Exception e){
		    Logger.logErr("RoundTripReporting Failed: "+ e.toString());
		}
	
		//WorkBookHandle testbk = new WorkBookHandle(foutput,1);
		log("RoundTripReporting Report Generation SUCCEEDED.");
		return ret;
	}
     
	
	public void testWrite(com.valkyrlabs.OpenXLS.WorkBook b, String nm){
		try{
      	    java.io.File f = new java.io.File(nm);
            FileOutputStream fos = new FileOutputStream(f);
            BufferedOutputStream bbout = new BufferedOutputStream(fos);
            b.writeBytes(bbout);
            bbout.flush();
            bbout.close();
            fos.flush();
		    fos.close();
		    System.gc();
		} catch (java.io.IOException e){com.valkyrlabs.OpenXLS.util.Logger.log("IOException in Tester.  "+e);}  
	}   
	
	Connection con = null;
	PersistenceEngine dofact = new ExtenBeanFactory();
    
	/** This method creates the test data objects to be used as inputs to the
     *  CellBinder API for merging with template for output.
     * These CellRanges reference timeInfo objects passed into the ReportFactory.
     * 
     * @return
     */
    private Map initTimeSheetDataObjects(int rid) 
    throws DataAccessException{
        // we're going to use InfoBeanDataObjects for now...
        PersistenceEngine dofact = new ExtenBeanFactory();
             
        // connect to database
       
        // load a chart of InfoBeans
        DataObject timeInfo = new GenericDataObject();
        timeInfo.setId(rid);
  
        try {
            if(con==null)con = cp.getConnection();
            dofact.setConnection(con);
            
            timeInfo.setFactory(dofact);
            timeInfo.setTableName("CUSTOMERS");
            timeInfo.setKeyCol("CUSTOMER_ID");

            // initializing the entire bean happens in this one line
            timeInfo.load();
        
        }catch(SQLException e) {
            System.err.println(e);
        }

        // put in Map
        Map ret = new Hashtable();
        ret.put("timeInfo",timeInfo);
      
        return ret;
    }
	
	//################## BEGIN UTILITY METHODS ####################
    public void log(String msg, Exception e){
		Logger.logErr("TestCellBinder: " +msg , e);
	}


	public void log(String msg){
		Logger.logInfo("TestCellBinder: " +msg);
	}


	public void log(String msg, Exception e, boolean b){
		Logger.logErr("TestCellBinder: " +msg, e, b);
	}

	
}