# OpenXLS 12

[![Java CI with Maven](https://github.com/ValkyrLabs/OpenXLS/actions/workflows/maven.yml/badge.svg)](https://github.com/ValkyrLabs/OpenXLS/actions/workflows/maven.yml)

[![Stable releases in Maven Central](https://img.shields.io/maven-metadata/v/https/repo1.maven.org/maven2/ValkyrLabs/OpenXLS/maven-metadata.xml.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.valkyrlabs/OpenXLS%22)

## :star::star::star: [open tasks](https://github.com/ValkyrLabs/OpenXLS/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22) :star::star::star:

the open source Java Spreadsheet Library reborn for 2025
**_by Valkyr Labs Inc_**

> OpenXLS was originally born over 20 years ago in 2002 as ExtenXLS, a commercial Java Spreadsheet library at Extentech Inc.

> Extentech Inc. was purchased by Infoteria Corp. in 2012. OpenXLS has been largely abandoned since.

> In 2024, Valkyr Labs Inc (founded by Extentech CEO, John McMahon) took over development of this project.

## Table of Contents

- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [Architecture Overview](#architecture-overview)
- [Application Level Features](#application-level-features)
- [WorkBook Operations](#workbook-operations)
- [WorkSheet Operations](#worksheet-operations)
- [Cell Operations](#cell-operations)
- [Advanced Features](#advanced-features)
- [Performance Tuning](#performance-tuning)
- [Troubleshooting](#troubleshooting)
- [Resources](#resources)

## Introduction

OpenXLS is a powerful, pure Java API for reading, modifying, and creating Excel-compatible spreadsheets. Written entirely in Java, it provides seamless spreadsheet manipulation capabilities without requiring native code dependencies.

### Key Features

- Create new spreadsheets from scratch
- Use existing spreadsheets as templates
- Output to Excel-compatible XLS, XML, and HTML formats
- Comprehensive formula support
- Rich formatting capabilities
- Chart manipulation
- High-performance processing

### Common Use Cases

- Create Financial analysis reports
- Build Formula calculation APIs
- Extract data from legacy spreadsheets
- Import data from uploaded spreadsheet files
- Database-to-spreadsheet report generation
- Transforming Excel files to XML or HTML for further processing
- Validate and clean spreadsheets

## Getting Started

### System Requirements

- Java VM 1.4 or higher
- Valid OpenXLS license
- OpenXLS.jar in classpath

### Installation Steps

1. Add OpenXLS dependencies to your project:

   ```java
   // Copy these files to your project directory
   OpenXLS.jar
   openxls.lic
   ```

2. Import the OpenXLS package:

   ```java
   import com.valkyrlabs.OpenXLS.*;
   ```

3. Verify installation:
   ```java
   WorkBookHandle book = new WorkBookHandle();
   System.out.println("OpenXLS version " + book.getVersion() + " loaded.");
   ```

> **Note**: For EAR/WAR deployments, set the `com.valkyrlabs.openxls.jarloc` system property to specify the OpenXLS classes location.

### Quick Start Guide

1. Create a new workbook:

   ```java
   WorkBookHandle book = new WorkBookHandle();
   ```

2. Access a worksheet:

   ```java
   WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
   ```

3. Manipulate cells:

   ```java
   // Add new cell
   sheet.add("Hello OpenXLS", "A1");

   // Modify existing cell
   CellHandle cell = sheet.getCell("B2");
   cell.setVal(42);
   ```

4. Save the workbook:
   ```java
   book.write("output.xls");
   ```

## Architecture Overview

OpenXLS operates on four primary levels:

### 1. Application Level

- File format conversions (XLS, XML, HTML)
- Template processing
- Output management

### 2. WorkBook Level

- Workbook creation and modification
- Formula calculation
- Sheet management
- Named range operations

### 3. WorkSheet Level

- Chart manipulation
- Row and column operations
- Protection settings
- Header/footer management

### 4. Cell Level

- Value manipulation
- Formatting
- Formula management
- Hyperlink creation

## Application Level Features

### Multiple Output Format Support

#### XML Output

```java
WorkBookHandle book = new WorkBookHandle("input.xls");
book.writeXML("output.xml");
```

#### HTML Output

```java
WorkBookHandle book = new WorkBookHandle("input.xls");
book.writeHTML("output.html");
```

### Template Processing

```java
// Load template
WorkBookHandle template = new WorkBookHandle("template.xls");

// Modify template data
WorkSheetHandle sheet = template.getWorkSheet("Sheet1");
sheet.getCell("A1").setVal("Updated Value");

// Save as new file
template.write("processed_template.xls");
```

## WorkBook Operations

### Creating WorkBooks

```java
// Empty workbook with default sheets
WorkBookHandle book = new WorkBookHandle();

// From existing file
WorkBookHandle fromFile = new WorkBookHandle("/path/to/file.xls");

// From byte array
byte[] data = getSpreadsheetBytes();
WorkBookHandle fromBytes = new WorkBookHandle(data);
```

### Managing Sheets

```java
// Get specific sheet
WorkSheetHandle sheet = book.getWorkSheet("Sheet1");

// Get all sheets
WorkSheetHandle[] sheets = book.getWorkSheets();

// Create new sheet
book.createWorkSheet("NewSheet");

// Copy sheet
book.copyWorkSheet("Sheet1", "Sheet1Copy");

// Remove sheet
book.removeSheet("Sheet1");
```

### Named Ranges

```java
// Create named range
CellRange range = new CellRange("Sheet1!A1:B8", book);
NameHandle namedRange = new NameHandle("MyRange", range);

// Access named range
NameHandle existing = book.getNamedRange("MyRange");
CellHandle[] cells = existing.getCells();
```

## WorkSheet Operations

### Sheet Management

```java
// Rename sheet
sheet.setSheetName("NewName");

// Set sheet protection
sheet.setProtected(true);

// Set tab color
sheet.setTabColor(FormatHandle.COLOR_BLUE);
```

### Row and Column Operations

```java
// Insert row
sheet.insertRow(5);

// Insert column
sheet.insertCol("C");

// Hide row
RowHandle row = sheet.getRow(3);
row.setHidden(true);

// Set column width
ColHandle col = sheet.getCol("B");
col.setWidth(5000);
```

### Chart Operations

```java
// Get chart
ChartHandle chart = sheet.getChart("SalesChart");

// Modify chart
chart.setChartTitle("Updated Sales Data");
chart.changeSeriesRange("Sheet1!A1:A12", "Sheet1!A1:A24");
```

## Cell Operations

### Value Manipulation

```java
// Basic value operations
CellHandle cell = sheet.getCell("A1");
cell.setVal("Text Value");
cell.setVal(42);
cell.setVal(3.14159);

// Date handling
java.util.Date date = new java.util.Date();
cell.setVal(date);
```

### Formatting

```java
// Create format
FormatHandle format = new FormatHandle(book);
format.setFont("Arial", Font.BOLD, 12);
format.setForegroundColor(FormatHandle.COLOR_LT_BLUE);
format.setBackgroundColor(FormatHandle.COLOR_LIGHT_YELLOW);
format.setPattern(FormatHandle.PATTERN_SOLID);

// Apply format
cell.setFormat(format);

// Currency format
cell.setFormatPattern("$#,##0.00");
```

### Hyperlinks

```java
CellHandle cell = sheet.getCell("A1");
cell.setVal("Click Here");
cell.setURL("https://www.example.com");
```

## Advanced Features

### Formula Support

```java
// Add formula
sheet.add("=SUM(A1:A10)", "B1");

// Modify formula
CellHandle formula = sheet.getCell("B1");
formula.setFormula("=AVERAGE(A1:A10)");

// Calculate formulas
book.calculateFormulas();
```

### Cell Ranges

```java
// Create range
CellRange range = new CellRange("Sheet1!A1:D10", book);

// Modify range
CellHandle[] cells = range.getCells();
for (CellHandle cell : cells) {
    cell.setVal(0);
}
```

## Performance Tuning

### String Optimization

```java
// Set string encoding mode
book.setStringEncodingMode(WorkBookHandle.STRING_ENCODING_UNICODE);

// Configure string duplication
book.setDupeStringMode(WorkBookHandle.ALLOWDUPES);
```

### Memory Management

```java
// Disable blank cell conversion
System.setProperty("com.valkyrlabs.openxls.convertmulblanks", "false");

// Set debug level
book.setDebugLevel(10);
```

### Best Practices

- Use appropriate string encoding modes
- Minimize blank cell formatting
- Batch cell operations when possible
- Reuse format handles
- Use template files efficiently

## Troubleshooting

### Common Issues and Solutions

| Issue              | Cause             | Solution                   |
| ------------------ | ----------------- | -------------------------- |
| Missing Data       | Parse errors      | Check debug logs           |
| Duplicate Values   | Shared strings    | Use `ALLOWDUPES` mode      |
| Performance Issues | String encoding   | Optimize encoding settings |
| Memory Problems    | Large blank areas | Disable blank conversion   |

### Debug Output

```java
// Enable detailed logging
book.setDebugLevel(50);

// Write to custom log
book.setDebugOutputStream(new FileOutputStream("openxls.log"));
```

## Resources

### Support Channels

- Technical Support: support@valkyrlabs.com
- [Official Documentation](http://www.valkyrlabs.com/docs)

### API References
- [Official OpenXLS Documentation](https://valkyrlabs.com/docs/docs/Products/OpenXLS/openxls-quick-start)
- [Java API Documentation](http://www.oracle.com/technetwork/java/javase/documentation/index.html)
- [Excel File Format Documentation](https://learn.microsoft.com/en-us/openspecs/office_file_formats/ms-xls)
- [Support Portal](https://valkyrlabs.com/support)
- [GitHub Repository](https://github.com/valkyrlabs/openxls)

## License

OpenXLS is licensed under the LGPL License.

---

_This documentation is maintained by Valkyr Labs. For support, contact support@valkyrlabs.com_
