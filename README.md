# OpenXLS 12

[![Java CI with Maven](https://github.com/ValkyrLabs/OpenXLS/actions/workflows/maven.yml/badge.svg)](https://github.com/ValkyrLabs/OpenXLS/actions/workflows/maven.yml)

[![Stable releases in Maven Central](https://img.shields.io/maven-metadata/v/https/repo1.maven.org/maven2/ValkyrLabs/OpenXLS/maven-metadata.xml.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22ValkyrLabs/OpenXLS%22)

:star::star::star: [open tasks](https://github.com/ValkyrLabs/OpenXLS/issues?q=is%3Aopen+is%3Aissue+label%3A%22help+wanted%22) :star::star::star:
---

the open source Java Spreadsheet Library reborn for 2025
***by Valkyr Labs Inc***

> OpenXLS was originally born over 20 years ago in 2002 as ExtenXLS, a commercial Java Spreadsheet library at Extentech Inc. 

> Extentech Inc. was purchased by Infoteria Corp. in 2012. OpenXLS has been largely abandoned since.

> In 2024, Valkyr Labs Inc (founded by Extentech CEO, John McMahon) took over development of this project. 

# OpenXLS Quick Start Technical Documentation

## Table of Contents
- [Introduction](#introduction)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Quick Start Guide](#quick-start-guide)
- [Core Concepts](#core-concepts)
  - [WorkBook Operations](#workbook-operations)
  - [WorkSheet Operations](#worksheet-operations)
  - [Cell Operations](#cell-operations)
- [Advanced Features](#advanced-features)
  - [Performance Optimization](#performance-optimization)
  - [Template Management](#template-management)
  - [Charts and Visualization](#charts-and-visualization)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [API Reference](#api-reference)

## Introduction

OpenXLS is a powerful Java API designed for reading, modifying, and creating Excel-compatible spreadsheets. Built entirely in Java, it offers seamless integration with your applications while maintaining high performance and compatibility.

### Key Features

- **Pure Java Implementation**: No native dependencies or DLLs required
- **Excel Compatibility**: Create and modify Excel-compatible files
- **Template Support**: Use existing spreadsheets as templates
- **Multiple Output Formats**: Generate XLS, XML, and HTML outputs
- **High Performance**: Optimized for handling large datasets
- **Cross-Platform**: Works on any Java-supported platform

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 1.4 or higher
- Valid OpenXLS license
- OpenXLS.jar in your project classpath

### Installation

1. Add OpenXLS.jar to your project directory
2. Include openxls.lic in the same directory
3. Add OpenXLS.jar to your project's classpath

```xml
<!-- Maven dependency example -->
<dependency>
    <groupId>com.valkyrlabs</groupId>
    <artifactId>openxls</artifactId>
    <version>latest</version>
</dependency>
```

### Quick Start Guide

1. Import the required packages:

```java
import com.valkyrlabs.OpenXLS.*;
```

2. Create or load a workbook:

```java
// Create new workbook
WorkBookHandle newBook = new WorkBookHandle();

// Or load existing workbook
WorkBookHandle existingBook = new WorkBookHandle("path/to/template.xls");
```

3. Access worksheets:

```java
WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
```

4. Manipulate cells:

```java
// Add new cell
sheet.add("Hello World", "A1");

// Access existing cell
CellHandle cell = sheet.getCell("B2");
cell.setVal("New Value");
```

5. Save your work:

```java
// Write to file
FileOutputStream fos = new FileOutputStream("output.xls");
fos.write(book.getBytes());
fos.close();
```

## Core Concepts

### WorkBook Operations

WorkBooks are the top-level containers in OpenXLS. They provide methods for managing sheets, formats, and global settings.

#### Creating WorkBooks

```java
// Create empty workbook
WorkBookHandle book = new WorkBookHandle();

// Load from file
WorkBookHandle template = new WorkBookHandle("/path/to/template.xls");

// Create from byte array
WorkBookHandle fromBytes = new WorkBookHandle(byteArray);
```

#### Setting Global Properties

```java
// Set calculation mode
book.setFormulaCalculationMode(WorkBookHandle.CALCULATE_EXPLICIT);

// Set string encoding
book.setStringEncodingMode(WorkBookHandle.AUTOMATIC);
```

### Performance Optimization

OpenXLS provides several ways to optimize performance based on your specific needs:

#### String Handling Optimization

```java
// For Unicode strings
book.setStringEncodingMode(WorkBookHandle.STRING_ENCODING_UNICODE);

// For compressed strings
book.setStringEncodingMode(WorkBookHandle.STRING_ENCODING_COMPRESSED);

// For mixed content (slower but more flexible)
book.setStringEncodingMode(WorkBookHandle.STRING_ENCODING_AUTO);
```

#### Memory Management

To reduce memory usage when dealing with large spreadsheets:

```java
// Disable blank cell conversion
System.getProperties().put("com.valkyrlabs.openxls.convertmulblanks", "false");

// Control string duplication
book.setDupeStringMode(WorkBookHandle.ALLOWDUPES);
```

## Best Practices

1. **Template Design**
   - Use unique placeholder values in template cells
   - Minimize empty formatted cells
   - Keep templates in BIFF8 format (Excel 97 or later)

2. **Performance**
   - Use appropriate string encoding modes
   - Manage blank cell conversion
   - Batch operations when possible

3. **Error Handling**
   - Always catch specific exceptions
   - Implement proper cleanup in finally blocks
   - Log meaningful error messages

```java
try {
    WorkSheetHandle sheet = book.getWorkSheet("Sheet1");
    CellHandle cell = sheet.getCell("A1");
    cell.setVal("New Value");
} catch (WorkSheetNotFoundException e) {
    logger.error("Sheet not found", e);
} catch (CellNotFoundException e) {
    logger.error("Cell not found", e);
} finally {
    // Cleanup resources
}
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Missing Data | Check system logs for warnings and errors |
| Duplicate Values | Verify string sharing settings and template unique values |
| Performance Issues | Review string encoding mode and blank cell conversion settings |
| Memory Problems | Enable garbage collection logging and monitor heap usage |

## Resources

- [Official Documentation](https://valkyrlabs.com/docs/docs/Products/OpenXLS/openxls-quick-start)
- [API Reference](https://api.valkyrlabs.com)
- [Support Portal](https://support.valkyrlabs.com)
- [GitHub Repository](https://github.com/valkyrlabs/openxls)

## License

OpenXLS requires a valid license for use. Contact [Valkyr Labs](https://valkyrlabs.com) for licensing information.

---

*This documentation is maintained by Valkyr Labs. For support, contact support@valkyrlabs.com*
