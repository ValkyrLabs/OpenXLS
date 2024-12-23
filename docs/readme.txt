OpenXLS 11.0 Release Notes
-----------------------------------------
Thank you for installing OpenXLS Excel-compatible Java Spreadsheet Library.

This document contains notes regarding this release that may not
have been available in time to include with the release documentation.

I - Release Notes
OpenXLS 11.0 introduces few major tangible improvements over prior versions, however it does reflect new stewardship of the project from ValkyrLabs Inc.

Please visit our GitHub page for the most recent updates.

II - Change History

11.0 ## Changes ##
	1. Bug fixes, cleanup
	2. Move to com.valkyrlabs package heirarchy
	3. Cleanup maven
	4. Setup GitHub project and refresh dependencies

10.3 ## Changes ##
	1. New API for Streaming Encrypted Writable Parser
	2. Sortable CellRange allows sorting on any column or row in the range
	3. Pluggable Tempfile Manager now cleans up OpenXLS tempfiles automatically
	4. Changed shifting algorithm for named ranges to make performance better
	5. Implement fill method on CellRange and make copy adjust formulas.
	6. Added "Fix Number Stored As String" method to WorkSheetHandle.add() to handle numbers entered as text and convert to a proper number rec with pattern.
	7. New Chart types added: PyramidBar, CylinderBar, Cone, RadarArea
	
10.2 ## Changes ##
	1. Performance Enhancements
	2. Bug Fixes
	3. Increase the performance of processing complex ranges
	
10.1 ## Changes ##

	1. Bug Fixes
	2. SVG Chart output from ChartHandle
	3. PDF Export now contains Charts
	4. Improvements to PDF Export fidelity
	5. Enhancements to Cell locking and Sheet protection 
	6. New Formula functions implemented
		NORMINV
		NORMSINV
		NORMDIST
		NORMSDIST
		ERF
	

10.0 ## Changes ##

	1. Introduction of the WorkBook "Parser" which provides an event-driven reader for high performance read-only situations
	2. Implemented MHTML read/write capability for reading and writing to MHTML spreadsheets
	3. Changes to serialversionUID serialized instances of these classes may not be compatible with prior versions
    4. XLSX/OOXML fidelity and performance improvements
    5. Implemented "Phonetic" record
    6. Additional complex cell range expression parsing
    7. Overhaul of sheet protection handling
    8. Adjust setPrintArea to allow for multiple print titles + adjust print records addition to be more specific
    9. 25 New Formula functions implemented:
    	YEARFRAC
		TIMEVALUE
		LOOKUP
		TRANSPOSE
		CELL
		INFO
		AVERAGEIF
		AVERAGEIFS
		COUNTIF
		COUNTIFS
		SUMIFS
		AREAS
		CLEAN
		MAXA
		MINA
		TYPE
		HYPERLINK
		IFERROR
		OFFSET
		DBCS
		ASC
		FINDB
		LEFTB
		LENB
		SEARCHB
    
9.2 ## Changes ##
    * XLSX and formula performance improvements.
    * Performance enhancements to XLSRecordFactory.
    * Major re-factoring: handling of complex ranges, string range processing errors, and much more!
    * Shared formula movement tracking.
    * Now PtgRef.createPtgRefFromString handles all types of locations!
    * More robust handling of complex ranges.
    * Added WorkBookHandle.REFTRACK_PROP system property to disable reference tracking for read-only workbook performance.
    * initSharingOnStrings to input dupeSstEntry.

9.1 ## Changes ##
    * XLSX parsing issues with XML files containing BOM characters.
    * OOXML parse optimizations, creates fewer blanks, SST handling with fast adding of cells.
    * out of spec bigblock sized file.
    
9.0 ## Changes ##
	1. Support for Encrypting and Decrypting XLS and XLSX workbooks with up to 256bit encryption
	2. Create, Edit, and Delete Cell comments (CommentHandle)
	3. Create, Edit, Delete and evaluate AutoFilters (AutoFilterHandle)
	4. Major Formula execution CPU and memory performance improvements
	5. Complete overhaul of Swing UI performance and functionality
	6. PrinterSettingsHandle fixes and additional handling for printing support from Excel
	7. Ability to reset the password used for sheet protection
	8. CellHandle.getVal() now returns calculated data type for Formula cells (used to return only Strings)
	9. Changes to CALC_EXPLICIT mode (see: http://valkyrlabs.com/nimbledocs/4520/2010/September/09/upgrading_to_extenxls_9)
	10. Over a dozen new formula functions support calculation:
			AVERAGEIF
			AVERAGEIFS
			COUNTIFS
			SUMIFS
			OFFSET
			IFERROR
			AREAS
			CLEAN
			MAXA
			MINA
			TYPE
			TIMEVALUE
			HLINK
	11. Generate WorkBook Thumbnail Images with WorkBookHandle.getImage() method
	12. Bug fixes
	
8.2 ## Changes ##
	1. Improved JavaDoc comments
	2. Add isBlank() method to CellHandle.
	3. DateHandle Rounding Issues
	4. Added support for unscoped Named Ranges
	5. Bug fixes

8.1 ## Changes ##
	1. Fix for Windows 7 OS-Level Corruption in otherwise Working Excel Files (same files open in same version of Excel on Vista and XP.)
	2. Swing Gui overhaul, including support for vertical text in cells and major performance boosts
	3. Fixes to our locking and password handling, both in XLS and XLSX
	
8.0 ## Changes ##
	1. Added support for AutoFilter
	2. Added support for Cell Comments
	3. Added support for Conditional Formatting
	4. New PrinterSettingsHandle controls printing from Excel
	5. Added support for Output to PDF files
	6. Added support for Import from CSV files
	7. Totally revamped Sample code files	
	8. Improved Documentation and API methods
	9. New improved XLSX memory and performance optimizations 
	10. Improved complex XLSX handling and inter-format conversions
	11. Numerous bug fixes

7.4 ## Changes ##
	1. Performance fixes for XLSX reading/writing
	2. Fixes for XLSX reading / writing
	3. Additional Sample code
	
7.3 ## Changes ##
	1. Performance and Bug fixes Formula Calculation
	2. Fixes to color table for Cells needed for XLSX support
	3. Added LARGE function
	4. Added NETWORKDAYS function
	5. Added EDATE function
	6. Added EOMONTH function
	7. Added DATEVALUE function
	8. Added WEEKNUM function
	9. Added WORKDAY function
	10. Added ROWS function
	11. Added COLUMNS function
	12. Added INDIRECT function
	13. Added ability to import CSV files into Spreadsheet
	14. Added ability to instantiate WorkBookHandles from a URL path
	
7.2 ### Changes ###
	1. Added SUMPRODUCT function
	2. Fixed 2 rare Stringtable bugs
	3. Additional XLSX support

7.1 ### Changes ###
	1. Added ROWS and INDIRECT functions
	2. Fixed ADDRESS function
	3. Created Validation sample code and docs
	4. Overhaul of DateConverter to correct date issues
	5. Overhaul of Date-referencing Formulas to offset DateConverter changes
	6. Refactor of ChartHandle class and additional functionality
	7. getChart(String chartname) enhanced to allow "named" charts without title

7.0 ### Changes ###
	1. Support for reading and writing of XLSX/OOXML (Excel 2007) XML file formats
	2. Support for printer settings
	3. Improved handling of Shared Formulas
	4. Improved formula String parsing
	5. Improved JSON/XML output
	6. Handle two Chart value axes by adding "XVALAXIS"
	7. Record ordering fix for MSO drawing objects out of order
	8. Handle issue of same chart name on multiple sheets
	9. Modify algorithm for setting zoom to use fractions to be in line with Excel
	10. Added Formathandle.setBorderLineStyle(int[]) convenience method
	11. Fixed MAX formulas func
	12. Optimized math calcs
	13. Fixes for black/white foreground/background
	14. Fixed error in LEFT formula func
	15. Significant overhaul of formula string parsing
	
6.2 ### Changes ###
	1. Changes to allow better handling of insertion of rowcols in merged cell ranges
	2. Major refactor of SharedFormula handling
	3. Handle "preserved" strings
	4. Fixed externsheet handling for add in formulas
	5. Added indented text handling
	6. Added indent and wrapped text handling
	7. Fix for double-byte strings in formulas and ability to add functions that we don't support calculation for.

6.0 ### Changes ###
1. Implemented ImageHandle object to allow for insertion, extraction, and manipulation of spreadsheet images.
	Insert and Extract JPG, GIF, and PNG images 
	Allows total control of image sizing and position 
	Images automatically shift down when rows are inserted, and shift up when rows are deleted
2. Implemented improved Chart functionality
	Complete Chart Support
	Insert, delete and modify pie, bubble, stack, 3d charts, and more! 
	Total control over chart series, titles, formatting, size and positioning 
	XML chart serialization, allows copying charts between workbooks and eases XSLT projects
3. Numerous helper methods added and new features supported
	Workbook search-and-replace 
	Set borders around cellrange objects with one line of code 
	Improved row and column sizing methods 
	Column auto-sizing 
	Fast row inserts with new insert object array as row method
	Worksheet Freeze panes 
	Worksheet Split panes
4. Numerous bug fixes
	row inserts and deletes shift image and chart objects
	improved row deletes shift up cell and formula references
5. Web2.0-enabled APIs, get on the fast-track, create spreadsheets in your Web apps 
	Output spreadsheet objects to JSON formatting with a single line of code 
	WebWorkBook messaging sessions allow for collaborative messaging and data updates between concurrent spreadsheet users
6. Improved Java Swing Spreadsheet Component -- embed a great mini-spreadsheet in your Java Swing Apps 

5.5 ### Changes ###
1. Performance speed and memory optimizations for cell adds up to 1000x faster using sheet.setFastAddCells(true) method
2. Numerous Memory and speed enhancements

5.4 ### Changes ###
1. Implemented formula caching enhancements. DB lookups and recursive formulas especially improved -- up to 1000%.
2. New WorkSheetHandle.add(value, int, int, int) method allows for setting of a Format ID during cell insert, thus avoiding CellHandle manipulation
and improving memory use by 100% in some cases.
3. Bug fixes

5.2 ### Changes ###
1. Bug fixes and optimizations.
2. Added WorkBookHandle is1904 method to check for 1904 date system

5.1 ### Changes ###
1. Implemented streaming XLS bytes directly to OutputStream -- performance boost in 10x range for writing files and much better memory use
2. Implemented Validation Handle and support for spreadsheet Cell Validations
3. Improved algorithm for shared string table allows for faster string adds while sharing strings and handling of dupe string modifications
4. FormatHandle packing fixes "too many formats" errors
5. Formula cacheing performance enhancements
6. Numerous bug fixes

5.0 ### Changes ###
1. Improved CellBinder automation including support for copying merged cell ranges, fine-grained Chart handling,
support for copying conditionally formatted cells, extensible Object Mapping with "Round-Trip", mapping to XML data sources
2. High-performance XML data merging using CellBinder API
3. WebWorkBook introduced which allows loading of XML workbooks over HTTP such as from Sheetster.com,
leveraging group-based access control, collaboration features, centralized workbook repository, and version control
4. WorkBook versioning, allows storing of diffs between workbook versions, and rolling changes back and forward
5. OpenXLS Hyperlinks now have the ability to point to file system and intra-WorkBook URLs
6. Up to 30% faster Formula execution cycles
7. 80 new Formula functions supported including Engineering and Financial packs
8. New customer support website and online documentation and weblog with discussion groups at:
	http://valkyrlabs.com/uimodules/docs/docs_home.jsp	

4.0 ### Changes ###
1. Support for unlimited file sizes
2. Implemented row and column grouping
3. Added control for Cell Font alignment and borders\
4. Disk-based IO reduces need for in-memory storage and improves scalability
5. Conversion of XLS to XML and HTML formats
6. New CellBinder user guide documentation
7. Improved CellBinder functionality including Hyperlink drilldowns and JNDI datasources
8. Numerous bug fixes
9. New WorkBook and WorkSheet interfaces allow for custom implementations and forward-compatibility
with OpenXLS Enterprise (est Q3 '05)
10. Totally revised User Manual and API Documentation
11. New Sample code demonstrating CellBinder
12. Improved GUI components -- merged cells
13. Support for explicit Formula calculation control
14. API defaults now tuned for performance
15. Improved color and pattern handling
16. Flexible cross-platform installer including Server Deploy and Custom install sets
17. File-based memory architecture for better memory performance

3.2 ### Changes ###
1. Added property setting for automating Blank record conversions to allow faster edits
2. Approx 5% performance improvement in writing workbook streams
3. Additional Formula Functions implemented
4. Implemented internal Excel Numeric conversion when updating Rk values
5. Fixed bug in adding Formulas to Columns > 0
6. Further refinements to Far-East String handling
7. Fixed bug in creating new Hyperlinks
8. added setCellBackgroundColor() method

3.1 ### Changes ###
1. Row and Column Outlining and Grouping
2. Merging Cells
3. Named Range creation from scratch
4. Chart Copying now supported
5. Swing UI SpreadSheet components
6. JDBC CellBinder API
7. 5x Faster Cell Adds
8. Up to 50% smaller memory footprint handling Strings
9. Improved String handling
10. Improved Formula Calculation
11. Simple Formula Parsing -- creation of new Formulas from scratch

3.0 ### Changes ###
1. ChartHandle objects give you total control over Excel Charts
2. Improved Cell Formatting
3. In-memory Formula Calculation
4. FormatHandle Object for easier formatting
5. Set Header and Footer text for printing WorkSheets
6. String handling settings boost performance up to 100 times during workbook writes
7. 30% reduction of in-memory workbook size
8. 10% speed increase in read/write of complex workbooks
9. Numerous bug fixes throughout
10. Automated Formula cell reference updates when Formula ranges are modified
11. 100 Formula Functions Supported
12. New Sample code demonstrating Chart and Formatting methods

2.4 ### Changes ###
1. performance improvements
2. bug fixes
3. added empty-workbook template creation methods 'getNoSheetWorkBook()'

2.2 ### Changes ###
1. added Named Range Support
2. added WorkSheet Tab Index methods
3. implemented XML output format of WorkBooks
4. created command-line implementation of OpenXLS
5. added sheet password Protection capability
6. additional String Table compatibility fixes

2.1 ### Changes ###
1. Added setHidden and setVeryHidden methods in WorkSheetHandle.
2. Fixed Sst error when traversing a boundary with a rich string, 
   half compressed.  Also fixed internal link Hlink error, and SB
   trash block issue.
3. Updated cellhandle javadoc API.
4. Fixed Rk setFloatVal problem converting to INTs when original
   type was INT
5. Fixed XF record index loss when setVal on a Mulblank.
6. Fixed rare outofbounds error in ArrayList when adding
   cell and writing out complex file.
7. Fixed SerializeSheet Recursion Stack overflows.
8. Fixed HLink junk bytes at end of link rec.
9. Added setUnderlined" to CellHandle.
10.Implemented changing Ai pointers to newly added WorkSheets. 
11.Fixed Rk float->double conversion error.

2.0 ### Changes ###
1. Improved ability to Add/Copy Worksheets
2. Implemented Dynamic Cell Formatting
3. Implemented Hyperlinks
4. Increased performance of Cell adds
5. Increased performance of Workbook reads
6. Implemented many new API methods
7. Increased robustness of Extended String Handling
8. Implemented ability to add Dates directly to WorkSheets
9. Improved Formula handling
10. Fixed installer issues with JDK1.4
11. Set row and col size defaults for new Workbooks
12. Serialization of Worksheets and Insertion of Serialized
    bytes into WorkBooks
13. Improved handling of CellNotFoundExceptions
14. Improved conversion of existing Blank recs to value recs
15. complete implementation of Excel Font attributes
16. Streamlining of byte code, reduced .jar size
17. Lots of new Sample code and Templates
18. Completely revised and updated documentation
19. Completely updated API java docs