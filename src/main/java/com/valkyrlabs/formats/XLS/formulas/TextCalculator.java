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

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Date;

import com.valkyrlabs.OpenXLS.DateConverter;
import com.valkyrlabs.OpenXLS.ExcelTools;
import com.valkyrlabs.OpenXLS.WorkBookHandle;
import com.valkyrlabs.formats.XLS.FormatConstants;
import com.valkyrlabs.formats.XLS.Formula;
import com.valkyrlabs.formats.XLS.XLSConstants;
import com.valkyrlabs.formats.XLS.Xf;
import com.valkyrlabs.toolkit.Logger;
import com.valkyrlabs.toolkit.StringTool;

/**
 * TextCalculator is a collection of static methods emulating various Microsoft Excel
 * text functions (ASC, CHAR, CLEAN, etc.). Each method expects an array of Ptg operands
 * and returns a Ptg representing the computed value.
 * <p>
 * These methods handle string manipulation, double-byte character set (DBCS) operations,
 * locale-specific transformations, and more.
 */
public class TextCalculator {

    /**
     * ASC function.
     * <p>
     * For DBCS languages, this changes full-width (double-byte) characters
     * to half-width (single-byte) characters.
     * <br>
     * <strong>Syntax (Excel):</strong> ASC(text).
     *
     * @param operands An array of Ptgs; operands[0] is the string to convert.
     * @return A PtgStr representing the converted value, or a PtgErr if invalid.
     */
    protected static Ptg calcAsc(Ptg[] operands) {
        if (operands == null || operands[0] == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }

        // Determine if Excel's language is set up for DBCS; if not, returns normal string.
        com.valkyrlabs.formats.XLS.WorkBook workbook = operands[0].getParentRec().getWorkBook();
        if (workbook.defaultLanguageIsDBCS()) {
            byte[] strBytes = getUnicodeBytesFromOp(operands[0]);
            if (strBytes == null) {
                strBytes = operands[0].getValue().toString().getBytes();
            }
            try {
                return new PtgStr(new String(strBytes, XLSConstants.UNICODEENCODING));
            } catch (Exception e) {
                Logger.logWarn("calcAsc: Unable to convert using UNICODEENCODING: " + e.getMessage());
            }
        }
        return new PtgStr(operands[0].getValue().toString());
    }

    /**
     * CHAR function.
     * <p>
     * Returns the character specified by the code number (1 - 255).
     * <br>
     * <strong>Syntax (Excel):</strong> CHAR(number).
     *
     * @param operands An array of Ptgs; operands[0] is the numeric code.
     * @return A PtgStr with the corresponding character, or a PtgErr on invalid input.
     */
    protected static Ptg calcChar(Ptg[] operands) {
        Object o = operands[0].getValue();
        byte code;
        try {
            code = Byte.parseByte(o.toString());
        } catch (NumberFormatException e) {
            return PtgCalculator.getError(); 
        }
        if (code < 1 || code > 255) {
            return PtgCalculator.getError();
        }
        byte[] b = new byte[]{code};
        String result = "";
        try {
            result = new String(b, XLSConstants.DEFAULTENCODING);
        } catch (UnsupportedEncodingException e) {
            Logger.logWarn("calcChar: Unsupported encoding: " + XLSConstants.DEFAULTENCODING + " - " + e.getMessage());
        }
        return new PtgStr(result);
    }

    /**
     * CLEAN function.
     * <p>
     * Removes all non-printable ASCII characters (below 32) from text.
     * <br>
     * <strong>Syntax (Excel):</strong> CLEAN(text).
     *
     * @param operands An array of Ptgs; operands[0] is the string to clean.
     * @return A PtgStr without non-printable characters, or a PtgErr on exceptions.
     */
    protected static Ptg calcClean(Ptg[] operands) {
        StringBuilder retString = new StringBuilder();
        try {
            String s = operands[0].getValue().toString();
            for (int i = 0; i < s.length(); i++) {
                int c = s.charAt(i);
                if (c >= 32) {
                    retString.append((char) c);
                }
            }
        } catch (Exception e) {
            Logger.logWarn("calcClean: Exception occurred: " + e.getMessage());
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        return new PtgStr(retString.toString());
    }

    /**
     * CODE function.
     * <p>
     * Returns a numeric code for the first character in a text string.
     * <br>
     * <strong>Syntax (Excel):</strong> CODE(text).
     *
     * @param operands An array of Ptgs; operands[0] is the string.
     * @return A PtgInt with the code of the first character, or PtgErr on exceptions.
     */
    protected static Ptg calcCode(Ptg[] operands) {
        try {
            String s = operands[0].getValue().toString();
            byte[] b = s.getBytes(XLSConstants.DEFAULTENCODING);
            return new PtgInt(b[0]);
        } catch (Exception e) {
            Logger.logWarn("calcCode: Unable to get code. " + e.getMessage());
            return PtgCalculator.getError();
        }
    }

    /**
     * CONCATENATE function.
     * <p>
     * Joins multiple strings into one.
     * <br>
     * <strong>Syntax (Excel):</strong> CONCATENATE(text1, [text2], ...).
     *
     * @param operands An array of Ptgs representing all text items.
     * @return A PtgStr with concatenated string, or PtgErr on invalid input.
     */
    protected static Ptg calcConcatenate(Ptg[] operands) {
        if (operands == null || operands.length < 1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        Ptg[] allOps = PtgCalculator.getAllComponents(operands);
        StringBuilder sb = new StringBuilder();
        for (Ptg op : allOps) {
            sb.append(op.getValue().toString());
        }
        Ptg result = new PtgStr(sb.toString());
        result.setParentRec(operands[0].getParentRec());
        return result;
    }

    /**
     * DOLLAR function.
     * <p>
     * Converts a number to text in currency format (USD). Optionally rounds to a specified
     * number of decimal places.
     * <br>
     * <strong>Syntax (Excel):</strong> DOLLAR(number, [decimals]).
     *
     * @param operands operands[0] is the number, operands[1] optional decimal place.
     * @return A PtgStr with the formatted string, or PtgErr on invalid input.
     */
    protected static Ptg calcDollar(Ptg[] operands) {
        if (operands == null || operands.length < 1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        int pop = 0;
        if (operands.length > 1) {
            pop = operands[1].getIntVal();
        }
        double d = operands[0].getDoubleVal();
        d = d * Math.pow(10, pop);
        d = Math.round(d);
        d = d / Math.pow(10, pop);
        return new PtgStr("$" + d);
    }

    /**
     * EXACT function.
     * <p>
     * Checks if two text values are identical (case-sensitive).
     * <br>
     * <strong>Syntax (Excel):</strong> EXACT(text1, text2).
     *
     * @param operands operands[0] is the first string, operands[1] is the second string.
     * @return A PtgBool (true if identical, false if not), or PtgErr on invalid input.
     */
    protected static Ptg calcExact(Ptg[] operands) {
        if (operands == null || operands.length != 2) {
            return PtgCalculator.getError();
        }
        String s1 = operands[0].getValue().toString();
        String s2 = operands[1].getValue().toString();
        return new PtgBool(s1.equals(s2));
    }

    /**
     * FIND function.
     * <p>
     * Finds one text value within another (case-sensitive).
     * <br>
     * <strong>Syntax (Excel):</strong> FIND(find_text, within_text, [start_num]).
     *
     * @param operands operands[0] is find_text, operands[1] is within_text, operands[2] optional start position.
     * @return A PtgInt with 1-based index of found substring, or PtgErr if not found.
     */
    protected static Ptg calcFind(Ptg[] operands) {
        if (operands == null || operands.length < 2) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        int start = 0;
        if (operands.length == 3) {
            start = operands[2].getIntVal() - 1;
        }
        Object searchObj = operands[0].getValue();
        Object wholeObj = operands[1].getValue();
        if (searchObj == null || wholeObj == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }

        String searchString = searchObj.toString();
        String wholeString = wholeObj.toString();

        int idx = wholeString.indexOf(searchString, start);
        if (idx == -1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        // Excel’s FIND function returns a 1-based position
        return new PtgInt(idx + 1);
    }

    /**
     * FINDB function (DBCS-aware).
     * <p>
     * Same as FIND, but counts each double-byte character as 2 for DBCS languages.
     * Otherwise, reverts to normal FIND behavior.
     *
     * @param operands Similar to calcFind. 
     * @return A PtgInt with 1-based index of found substring (counting double-byte properly), or PtgErr if not found.
     */
    protected static Ptg calcFindB(Ptg[] operands) {
        if (operands == null || operands.length < 2 || operands[0] == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        com.valkyrlabs.formats.XLS.WorkBook workbook = operands[0].getParentRec().getWorkBook();
        // If not DBCS, just call normal find.
        if (!workbook.defaultLanguageIsDBCS()) {
            return calcFind(operands);
        }

        int startNum = 0;
        if (operands.length == 3) {
            startNum = operands[2].getIntVal();
        }
        byte[] strToFind = getUnicodeBytesFromOp(operands[0]);
        byte[] str = getUnicodeBytesFromOp(operands[1]);

        if (strToFind == null || strToFind.length == 0 || str == null || startNum < 0 || str.length < startNum) {
            return new PtgInt(startNum);
        }

        int foundIndex = -1;
        for (int i = startNum; i < str.length && foundIndex == -1; i++) {
            if (strToFind[0] == str[i]) {
                foundIndex = i;
                for (int j = 0; j < strToFind.length && (i + j) < str.length && foundIndex == i; j++) {
                    if (strToFind[j] != str[i + j]) {
                        foundIndex = -1; // start over
                        break;
                    }
                }
            }
        }

        if (foundIndex == -1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        // Return 1-based index.
        return new PtgInt(foundIndex + 1);
    }

    /**
     * FIXED function.
     * <p>
     * Formats a number as text with a fixed number of decimals, optionally without commas.
     * <br>
     * <strong>Syntax (Excel):</strong> FIXED(number, decimals, [no_commas]).
     *
     * @param operands operands[0] number, operands[1] decimals, operands[2] optional boolean no_commas.
     * @return A PtgStr with the formatted string, or PtgErr if invalid input.
     */
    protected static Ptg calcFixed(Ptg[] operands) {
        if (operands == null || operands.length < 2) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        boolean noCommas = false;
        if (operands.length == 3) {
            if (operands[2].getValue() instanceof Boolean) {
                noCommas = (Boolean) operands[2].getValue();
            }
        }
        double value = operands[0].getDoubleVal();
        if (Double.isNaN(value)) {
            value = 0.0;
        }
        int decimals = operands[1].getIntVal();

        // Round to given decimals.
        double scale = Math.pow(10, decimals);
        value = Math.round(value * scale) / scale;

        // Convert to string with decimal digits.
        String raw = String.valueOf(value);

        // Handle no decimals case: remove trailing ".0".
        if (decimals == 0 && raw.contains(".")) {
            raw = raw.substring(0, raw.indexOf("."));
            return new PtgStr(raw);
        }

        // If decimals > 0, ensure the substring includes them.
        if (!raw.contains(".") && decimals > 0) {
            raw += ".0";
        }
        String[] parts = raw.split("\\.");
        if (parts.length == 2) {
            // Pad with zeros if needed.
            while (parts[1].length() < decimals) {
                parts[1] += "0";
            }
            raw = parts[0] + "." + parts[1];
        }

        if (noCommas || value < 1000) {
            return new PtgStr(raw);
        }

        // Insert commas into the integer portion.
        StringBuilder sb = new StringBuilder();
        int integerLength = parts[0].length();
        int counter = 0;
        for (int i = integerLength - 1; i >= 0; i--) {
            sb.insert(0, parts[0].charAt(i));
            counter++;
            if (counter == 3 && i != 0) {
                sb.insert(0, ',');
                counter = 0;
            }
        }
        sb.append(".").append(parts[1]);
        return new PtgStr(sb.toString());
    }

    /**
     * JIS function.
     * <p>
     * Converts half-width (single-byte) characters to full-width (double-byte) for Japanese systems.
     * <br>
     * <strong>Syntax (Excel):</strong> JIS(text).
     *
     * @param operands operands[0] is the string to convert.
     * @return A PtgStr with converted string, or PtgErr on exceptions.
     */
    protected static Ptg calcJIS(Ptg[] operands) {
        if (operands == null || operands[0] == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }

        com.valkyrlabs.formats.XLS.WorkBook workbook = operands[0].getParentRec().getWorkBook();
        if (workbook.defaultLanguageIsDBCS()) {
            byte[] strBytes = getUnicodeBytesFromOp(operands[0]);
            if (strBytes == null) {
                strBytes = operands[0].getValue().toString().getBytes();
            }
            try {
                return new PtgStr(new String(strBytes, "Shift_JIS"));
            } catch (Exception e) {
                Logger.logWarn("calcJIS: Unable to convert with Shift_JIS - " + e.getMessage());
            }
        }
        return new PtgStr(operands[0].getValue().toString());
    }

    /**
     * LEFT function.
     * <p>
     * Returns the leftmost characters from a text value.
     * <br>
     * <strong>Syntax (Excel):</strong> LEFT(text, [num_chars]).
     *
     * @param operands operands[0] text, operands[1] optional num_chars.
     * @return A PtgStr with left substring, or PtgErr on invalid input.
     */
    protected static Ptg calcLeft(Ptg[] operands) {
        if (operands == null || operands.length < 1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        if (operands[0] instanceof PtgErr) {
            return new PtgErr(PtgErr.ERROR_NA);
        }
        int numChars = 1;
        if (operands.length == 2) {
            if (operands[1] instanceof PtgErr) {
                return new PtgErr(PtgErr.ERROR_VALUE);
            }
            numChars = operands[1].getIntVal();
        }
        Object o = operands[0].getValue();
        if (o == null) {
            return new PtgStr("");
        }
        String str = String.valueOf(o);
        if (str.isEmpty() || numChars <= 0) {
            return new PtgStr("");
        }
        if (numChars > str.length()) {
            return new PtgStr("");
        }
        return new PtgStr(str.substring(0, numChars));
    }

    /**
     * LEFTB function (DBCS-aware).
     * <p>
     * Similar to LEFT but counts each double-byte character as 2 for DBCS languages.
     *
     * @param operands operands[0] text, operands[1] optional num_bytes.
     * @return A PtgStr with left substring (in bytes for DBCS), or fallback to calcLeft.
     */
    protected static Ptg calcLeftB(Ptg[] operands) {
        if (operands == null || operands[0] == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        com.valkyrlabs.formats.XLS.WorkBook workbook = operands[0].getParentRec().getWorkBook();
        if (workbook.defaultLanguageIsDBCS()) {
            int numBytes = 1;
            if (operands.length < 1) {
                return new PtgErr(PtgErr.ERROR_VALUE);
            }
            if (operands.length == 2) {
                if (operands[1] instanceof PtgErr) {
                    return new PtgErr(PtgErr.ERROR_VALUE);
                }
                numBytes = operands[1].getIntVal();
            }
            try {
                byte[] source = getUnicodeBytesFromOp(operands[0]);
                if (source == null) {
                    return new PtgErr(PtgErr.ERROR_VALUE);
                }
                if (numBytes > source.length) {
                    numBytes = source.length;
                } else if (numBytes < 0) {
                    return new PtgErr(PtgErr.ERROR_VALUE);
                }
                byte[] cut = new byte[numBytes];
                System.arraycopy(source, 0, cut, 0, numBytes);
                return new PtgStr(new String(cut, XLSConstants.UNICODEENCODING));
            } catch (Exception e) {
                Logger.logWarn("calcLeftB: Exception " + e.getMessage());
                return new PtgErr(PtgErr.ERROR_VALUE);
            }
        }
        return calcLeft(operands);
    }

    /**
     * LEN function.
     * <p>
     * Returns the number of characters in a text string.
     * <br>
     * <strong>Syntax (Excel):</strong> LEN(text).
     *
     * @param operands operands[0] text.
     * @return A PtgInt with length, or PtgErr if invalid input.
     */
    protected static Ptg calcLen(Ptg[] operands) {
        if (operands == null || operands.length != 1) {
            return PtgCalculator.getError();
        }
        String s = String.valueOf(operands[0].getValue());
        return new PtgInt(s.length());
    }

    /**
     * LENB function (DBCS-aware).
     * <p>
     * Similar to LEN, but counts double-byte characters as 2 for DBCS languages.
     *
     * @param operands operands[0] text.
     * @return A PtgInt with the length in bytes for DBCS, else normal length.
     */
    protected static Ptg calcLenB(Ptg[] operands) {
        if (operands == null || operands.length != 1) {
            return PtgCalculator.getError();
        }
        com.valkyrlabs.formats.XLS.WorkBook workbook = operands[0].getParentRec().getWorkBook();
        if (workbook.defaultLanguageIsDBCS()) {
            byte[] data = getUnicodeBytesFromOp(operands[0]);
            if (data != null) {
                return new PtgInt(data.length);
            }
        }
        String s = String.valueOf(operands[0].getValue());
        return new PtgInt(s.length());
    }

    /**
     * LOWER function.
     * <p>
     * Converts text to lowercase.
     * <br>
     * <strong>Syntax (Excel):</strong> LOWER(text).
     *
     * @param operands operands[0] text.
     * @return A PtgStr with lowered string, or PtgErr if invalid input.
     */
    protected static Ptg calcLower(Ptg[] operands) {
        if (operands == null || operands.length > 1) {
            return PtgCalculator.getError();
        }
        String s = String.valueOf(operands[0].getValue());
        return new PtgStr(s.toLowerCase());
    }

    /**
     * MID function.
     * <p>
     * Returns a specific number of characters from a text string starting at a given position.
     * <br>
     * <strong>Syntax (Excel):</strong> MID(text, start_num, num_chars).
     *
     * @param operands operands[0] text, operands[1] start, operands[2] length.
     * @return A PtgStr with the substring, or PtgErr on invalid input.
     */
    protected static Ptg calcMid(Ptg[] operands) {
        if (operands == null || operands.length < 3) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String s = String.valueOf(operands[0].getValue());
        if (s.isEmpty()) {
            return new PtgStr("");
        }
        if (operands[1] instanceof PtgErr || operands[2] instanceof PtgErr) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        int start = operands[1].getIntVal() - 1;
        int len = operands[2].getIntVal();
        if (len < 0) {
            len = start + len;
        }
        if (start < 0 || start > s.length()) {
            return new PtgStr("");
        }
        // substring from start
        s = s.substring(start);
        if (len > s.length()) {
            return new PtgStr(s);
        }
        return new PtgStr(s.substring(0, len));
    }

    /**
     * PROPER function.
     * <p>
     * Capitalizes the first letter in each word of a text value (title case).
     * <br>
     * <strong>Syntax (Excel):</strong> PROPER(text).
     *
     * @param operands operands[0] text.
     * @return A PtgStr with title-cased text, or PtgErr if invalid.
     */
    protected static Ptg calcProper(Ptg[] operands) {
        if (operands == null || operands[0] == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String s = String.valueOf(operands[0].getValue());
        s = StringTool.proper(s);
        return new PtgStr(s);
    }

    /**
     * REPLACE function.
     * <p>
     * Replaces part of a text string with a different text string given
     * a start index and number of characters to replace.
     * <br>
     * <strong>Syntax (Excel):</strong> REPLACE(old_text, start_num, num_chars, new_text).
     *
     * @param operands [0]: old_text, [1]: start_num, [2]: num_chars, [3]: new_text.
     * @return A PtgStr with replaced text, or PtgErr if invalid.
     */
    protected static Ptg calcReplace(Ptg[] operands) {
        if (operands == null || operands.length < 4) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String original = String.valueOf(operands[0].getValue());
        int start = operands[1].getIntVal();
        int repAmount = operands[2].getIntVal();
        String repStr = String.valueOf(operands[3].getValue());
        if (start < 1 || start > original.length()) {
            // Excel behavior: if start is out of bounds, it can produce unexpected results
            // but let's mimic the original logic carefully.
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        int endIndex = start + repAmount - 1;
        if (endIndex > original.length()) {
            endIndex = original.length();
        }
        String begin = original.substring(0, start - 1);
        String end = original.substring(endIndex);
        return new PtgStr(begin + repStr + end);
    }

    /**
     * REPT function.
     * <p>
     * Repeats text a given number of times.
     * <br>
     * <strong>Syntax (Excel):</strong> REPT(text, number_times).
     *
     * @param operands [0]: text, [1]: number_times
     * @return A PtgStr with repeated text, or PtgErr if invalid.
     */
    protected static Ptg calcRept(Ptg[] operands) {
        if (operands == null || operands.length < 2) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String original = String.valueOf(operands[0].getValue());
        int times = operands[1].getIntVal();
        if (times < 0) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(original);
        }
        return new PtgStr(sb.toString());
    }

    /**
     * RIGHT function.
     * <p>
     * Returns the rightmost characters from a text value.
     * <br>
     * <strong>Syntax (Excel):</strong> RIGHT(text, [num_chars]).
     *
     * @param operands [0]: text, [1]: optional num_chars
     * @return A PtgStr with the rightmost substring, or PtgErr if invalid.
     */
    protected static Ptg calcRight(Ptg[] operands) {
        if (operands == null || operands.length < 1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String original = String.valueOf(operands[0].getValue());
        if (original.isEmpty()) {
            return new PtgStr("");
        }
        int numChars = 1;
        if (operands.length > 1) {
            numChars = operands[1].getIntVal();
        }
        if (numChars < 0) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        if (numChars > original.length()) {
            numChars = original.length();
        }
        return new PtgStr(original.substring(original.length() - numChars));
    }

    /**
     * SEARCH function.
     * <p>
     * Finds one text value within another (not case-sensitive).
     * <br>
     * <strong>Syntax (Excel):</strong> SEARCH(find_text, within_text, [start_num]).
     *
     * @param operands [0]: find_text, [1]: within_text, [2]: optional start_num
     * @return PtgInt with 1-based index, or PtgErr if not found.
     */
    protected static Ptg calcSearch(Ptg[] operands) {
        if (operands == null || operands.length < 2) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        int start = 0;
        if (operands.length == 3) {
            start = operands[2].getIntVal() - 1;
        }
        String search = operands[0].getValue().toString().toLowerCase();
        String whole = operands[1].getValue().toString().toLowerCase();
        if (start < 0 || start >= whole.length()) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }

        String tmp = whole.substring(start);
        int i = tmp.indexOf(search);
        if (i == -1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        // Add start offset, plus 1 for 1-based indexing.
        return new PtgInt(start + i + 1);
    }

    /**
     * SEARCHB function (DBCS-aware).
     * <p>
     * Same as SEARCH, but counts double-byte characters as 2 for DBCS languages.
     * Otherwise, reverts to calcSearch.
     *
     * @param operands [0]: find_text, [1]: within_text, [2]: optional start_num
     * @return PtgInt with position or PtgErr if not found.
     */
    protected static Ptg calcSearchB(Ptg[] operands) {
        if (operands == null || operands.length < 2 || operands[0] == null) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        com.valkyrlabs.formats.XLS.WorkBook workbook = operands[0].getParentRec().getWorkBook();
        if (!workbook.defaultLanguageIsDBCS()) {
            return calcSearch(operands);
        }

        int startNum = 0;
        if (operands.length > 2) {
            startNum = operands[2].getIntVal();
        }
        byte[] strToFind = getUnicodeBytesFromOp(operands[0]);
        byte[] str = getUnicodeBytesFromOp(operands[1]);
        if (strToFind == null || strToFind.length == 0 || str == null || startNum < 0 || startNum >= str.length) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }

        // fallback approach: run normal substring find in the string form, but then count bytes
        String search = operands[0].getValue().toString().toLowerCase();
        String original = operands[1].getValue().toString().toLowerCase();
        if (startNum >= original.length()) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String subOriginal = original.substring(startNum);
        int foundIndex = subOriginal.indexOf(search);
        if (foundIndex == -1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        // Multiply index by 2 because each DBCS char is counted as 2
        foundIndex = foundIndex * 2 + 1;
        return new PtgInt(foundIndex);
    }

    /**
     * SUBSTITUTE function.
     * <p>
     * Substitutes new text for old text in a text string. Optionally only replaces
     * a specific occurrence.
     * <br>
     * <strong>Syntax (Excel):</strong> SUBSTITUTE(text, old_text, new_text, [instance_num]).
     *
     * @param operands [0]: text, [1]: old_text, [2]: new_text, [3]: optional instance_num
     * @return A PtgStr with substituted string, or PtgErr on invalid input.
     */
    protected static Ptg calcSubstitute(Ptg[] operands) {
        if (operands == null || operands.length < 3) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        int whichReplace = 0;
        if (operands.length == 4) {
            whichReplace = operands[3].getIntVal() - 1; // zero-based for internal
        }
        String original = operands[0].getValue().toString();
        String search = operands[1].getValue().toString();
        String replace = operands[2].getValue().toString();
        String finalStr = StringTool.replaceText(original, search, replace, whichReplace, true);
        return new PtgStr(finalStr);
    }

    /**
     * T function.
     * <p>
     * Returns text if the operand is text, otherwise returns an empty string.
     * <br>
     * <strong>Syntax (Excel):</strong> T(value).
     *
     * @param operands [0]: value
     * @return A PtgStr if it is text, else empty string.
     */
    protected static Ptg calcT(Ptg[] operands) {
        if (operands == null || operands.length == 0) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String res = "";
        try {
            res = (String) operands[0].getValue();
        } catch (ClassCastException e) {
            // Not a string, so return empty.
        }
        return new PtgStr(res);
    }

    /**
     * TEXT function.
     * <p>
     * Formats a number and converts it to text based on a given format string.
     * <br>
     * <strong>Syntax (Excel):</strong> TEXT(value, format_text).
     *
     * @param operands [0]: value, [1]: format_text
     * @return A PtgStr of the formatted string, or PtgErr on invalid input.
     */
    protected static Ptg calcText(Ptg[] operands) {
        if (operands == null || operands.length != 2) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        String val;
        try {
            val = String.valueOf(operands[0].getValue());
        } catch (Exception e) {
            val = operands[0].toString();
        }

        String fmt = operands[1].toString();
        Format formatObj = null;

        // Try matching known numeric formats.
        for (String[] numericFormat : FormatConstants.NUMERIC_FORMATS) {
            if (numericFormat[0].equals(fmt)) {
                fmt = numericFormat[2];
                formatObj = new DecimalFormat(fmt);
                break;
            }
        }
        // Try currency formats
        if (formatObj == null) {
            for (String[] currencyFormat : FormatConstants.CURRENCY_FORMATS) {
                if (currencyFormat[0].equals(fmt)) {
                    fmt = currencyFormat[2];
                    formatObj = new DecimalFormat(fmt);
                    break;
                }
            }
        }
        // Attempt to format as numeric
        if (formatObj != null) {
            try {
                float numericVal = (val == null || val.equals("")) ? 0.0f : Float.parseFloat(val);
                return new PtgStr(formatObj.format(numericVal));
            } catch (NumberFormatException e) {
                Logger.logWarn("calcText: Number format exception: " + e.getMessage());
                return new PtgStr(val);
            }
        }
        // Attempt date formats
        for (String[] dateFormat : FormatConstants.DATE_FORMATS) {
            if (dateFormat[0].equals(fmt)) {
                fmt = dateFormat[2];
                try {
                    Date d;
                    try {
                        d = DateConverter.getDateFromNumber(Double.valueOf(val));
                    } catch (NumberFormatException ex) {
                        d = DateConverter.getDate(val);
                        if (d == null) {
                            d = new Date("1/1/1990"); // fallback
                        }
                    }
                    WorkBookHandle.simpledateformat.applyPattern(fmt);
                    return new PtgStr(WorkBookHandle.simpledateformat.format(d));
                } catch (Exception e) {
                    Logger.logErr("calcText: Unable to format date: " + e.getMessage());
                }
            }
        }

        // If we get here, do a general parse attempt:
        try {
            if (Xf.isDatePattern(fmt)) {
                WorkBookHandle.simpledateformat.applyPattern(fmt);
                formatObj = WorkBookHandle.simpledateformat;
            } else {
                formatObj = new DecimalFormat(fmt);
            }
            float numericVal = (val == null || val.equals("")) ? 0.0f : Float.parseFloat(val);
            return new PtgStr(formatObj.format(numericVal));
        } catch (Exception e) {
            Logger.logWarn("calcText: Fallback formatting failed: " + e.getMessage());
            return new PtgStr(val);
        }
    }

    /**
     * TRIM function.
     * <p>
     * Removes leading/trailing spaces, and reduces internal multiple spaces to a single space.
     * <br>
     * <strong>Syntax (Excel):</strong> TRIM(text).
     *
     * @param operands [0]: text
     * @return PtgStr with trimmed text, or PtgErr if invalid.
     */
    protected static Ptg calcTrim(Ptg[] operands) {
        if (operands == null || operands.length < 1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        Object o = operands[0].getValue();
        String res;
        if (o instanceof Double) {
            res = ExcelTools.getNumberAsString((Double) o);
        } else {
            res = String.valueOf(o);
        }
        if (res == null) {
            return new PtgErr(PtgErr.ERROR_NA);
        }
        // remove leading/trailing
        res = res.trim();
        // remove consecutive spaces
        while (res.contains("  ")) {
            res = res.replace("  ", " ");
        }
        return new PtgStr(res);
    }

    /**
     * UPPER function.
     * <p>
     * Converts text to uppercase.
     * <br>
     * <strong>Syntax (Excel):</strong> UPPER(text).
     *
     * @param operands [0]: text
     * @return PtgStr with uppercase text, or PtgErr on invalid input.
     */
    protected static Ptg calcUpper(Ptg[] operands) {
        if (operands == null || operands.length > 1) {
            return PtgCalculator.getError();
        }
        String s = String.valueOf(operands[0].getValue());
        return new PtgStr(s.toUpperCase());
    }

    /**
     * VALUE function.
     * <p>
     * Converts a text argument to a number.
     * <br>
     * <strong>Syntax (Excel):</strong> VALUE(text).
     *
     * @param operands [0]: text
     * @return A PtgNumber with parsed double, or PtgErr if invalid.
     */
    protected static Ptg calcValue(Ptg[] operands) {
        if (operands == null || operands.length < 1) {
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
        try {
            String s = String.valueOf(operands[0].getValue());
            if (s.equals("")) {
                s = "0";
            }
            double d = Double.parseDouble(s);
            return new PtgNumber(d);
        } catch (NumberFormatException e) {
            Logger.logWarn("calcValue: Number format exception: " + e.getMessage());
            return new PtgErr(PtgErr.ERROR_VALUE);
        }
    }

    /**
     * Helper method for all DBCS-related worksheet functions.
     * <p>
     * Extracts the underlying byte[] from a Ptg if possible.
     *
     * @param op The Ptg to inspect.
     * @return The byte array, or null if not available.
     */
    private static byte[] getUnicodeBytesFromOp(Ptg op) {
        byte[] strBytes = null;
        if (op instanceof PtgRef) {
            com.valkyrlabs.formats.XLS.BiffRec rec = ((PtgRef) op).getRefCells()[0];
            if (rec instanceof com.valkyrlabs.formats.XLS.Labelsst) {
                strBytes = ((com.valkyrlabs.formats.XLS.Labelsst) rec).getUnsharedString().readStr();
            } else if (rec instanceof Formula) {
                strBytes = op.getValue().toString().getBytes();
            } else {
                Logger.logWarn("getUnicodeBytesFromOp: Unexpected rec encountered: " + op.getClass());
            }
        } else if (op instanceof PtgStr) {
            // PtgStr record has length - 3 overhead
            PtgStr ptgStr = (PtgStr) op;
            if (ptgStr.record.length > 3) {
                strBytes = new byte[ptgStr.record.length - 3];
                System.arraycopy(ptgStr.record, 3, strBytes, 0, strBytes.length);
            }
        } else {
            Logger.logWarn("getUnicodeBytesFromOp: Unexpected operand type: " + op.getClass());
        }
        return strBytes;
    }
}
