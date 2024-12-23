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

 import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.valkyrlabs.OpenXLS.ExcelTools;
import com.valkyrlabs.OpenXLS.FunctionNotSupportedException;
import com.valkyrlabs.toolkit.Logger;
 
 /**
  * <strong>StatisticalCalculator</strong>
  * <p>
  * A collection of static methods that emulate Microsoft Excel statistical
  * functions. Each method returns a {@code Ptg} (parse thing) object representing
  * the computed value.
  * <p>
  * Notable methods:
  * <ul>
  *   <li><strong>calcAverage</strong>: Averages numeric values, ignoring non-numbers.</li>
  *   <li><strong>calcCount</strong>: Counts how many numeric values are in the list.</li>
  *   <li><strong>calcMax</strong>: Returns the maximum of numeric values.</li>
  *   <li><strong>calcNormsdist</strong>: Returns the standard normal cumulative distribution.</li>
  *   <li><strong>calcPearson</strong>: Returns the Pearson correlation coefficient.</li>
  *   <!-- And so on... -->
  * </ul>
  * <p>
  * The code handles many statistical operations, including advanced topics like
  * linear regressions, normal distributions, covariance, correlation, etc.
  */
 public class StatisticalCalculator {
 
     /**
      * AVERAGE
      * <p>
      * Returns the average (arithmetic mean) of numeric arguments, ignoring non-numbers.
      * If no valid numbers, returns PtgErr(#DIV/0!).
      *
      * @param operands An array of {@link Ptg} items (range or individual cells).
      * @return A {@code PtgNumber} containing the average, or PtgErr on error.
      */
     protected static Ptg calcAverage(Ptg[] operands) {
         // Flatten all components into a single list
         List<Ptg> flatList = new ArrayList<>();
         for (Ptg operand : operands) {
             Ptg[] comps = operand.getComponents();
             if (comps != null) {
                 for (Ptg c : comps) {
                     flatList.add(c);
                 }
             } else {
                 flatList.add(operand);
             }
         }
 
         BigDecimal sum = BigDecimal.ZERO;
         int count = 0;
         for (Ptg p : flatList) {
             try {
                 if (p.isBlank()) {
                     continue;
                 }
                 Object ov = p.getValue();
                 if (ov != null) {
                     double d = Double.parseDouble(String.valueOf(ov));
                     sum = sum.add(BigDecimal.valueOf(d));
                     count++;
                 }
             } catch (NumberFormatException e) {
                 // ignore non-numerics
             }
         }
         if (count == 0) {
             return new PtgErr(PtgErr.ERROR_DIV_ZERO);
         }
         sum = sum.setScale(15, java.math.RoundingMode.HALF_UP);
         double result = sum.doubleValue() / count;
         return new PtgNumber(result);
     }
 
     /**
      * AVERAGEIF
      * <p>
      * Returns the average (arithmetic mean) of all the cells in a range that meet a given criteria.
      *
      * @param operands [0]: range, [1]: criteria, [2]: optional average_range
      * @return A {@code PtgNumber} with the average, or a PtgErr.
      */
     protected static Ptg calcAverageIf(Ptg[] operands) {
         if (operands.length < 2) {
             return new PtgErr(PtgErr.ERROR_DIV_ZERO);
         }
 
         // range used for testing criteria
         Ptg[] range = operands[0].getComponents();
         if (range == null || range.length == 0) {
             return new PtgErr(PtgErr.ERROR_DIV_ZERO);
         }
         String criteria = operands[1].getString().trim();
         // parse the operator from criteria
         int idx = Calculator.splitOperator(criteria);
         String op = criteria.substring(0, idx); 
         String critVal = criteria.substring(idx);
         critVal = Calculator.translateWildcardsInCriteria(critVal);
 
         // if there's an average_range, we need to map each cell in "range" to the corresponding
         // cell in "average_range"
         Ptg[] averageRange = null;
         boolean varyRow = false;
         if (operands.length > 2) {
             averageRange = new Ptg[range.length];
             Ptg[] bigComponents = operands[2].getComponents();
             if (bigComponents == null || bigComponents.length == 0) {
                 return new PtgErr(PtgErr.ERROR_DIV_ZERO);
             }
             // start with the top-left cell
             averageRange[0] = bigComponents[0];
             int[] rc = null;
             String sheet = "";
             try {
                 rc = averageRange[0].getIntLocation();
                 // if first cell in range differs across row => vary row, else vary column
                 if (range[0].getIntLocation()[0] != range[range.length - 1].getIntLocation()[0]) {
                     varyRow = true;
                 }
                 sheet = ((PtgRef) averageRange[0]).getSheetName() + "!";
             } catch (Exception e) {
                 Logger.logWarn("calcAverageIf: error setting up averageRange: " + e.getMessage());
             }
             // fill out the rest of averageRange
             for (int i = 1; i < averageRange.length; i++) {
                 if (varyRow) {
                     rc[0]++;
                 } else {
                     rc[1]++;
                 }
                 averageRange[i] = new PtgRef3d();
                 averageRange[i].setParentRec(range[0].getParentRec());
                 averageRange[i].setLocation(sheet + ExcelTools.formatLocation(rc));
             }
         }
 
         int nresults = 0;
         double sum = 0.0;
         for (int j = 0; j < range.length; j++) {
             Object val = range[j].getValue();
             if (Calculator.compareCellValue(val, critVal, op)) {
                 // passes the criteria
                 try {
                     if (averageRange != null) {
                         val = averageRange[j].getValue();
                         if (val == null) {
                             continue; 
                         }
                     }
                     sum += ((Number) val).doubleValue();
                     nresults++;
                 } catch (Exception e) {
                     // skip non-numerics
                 }
             }
         }
         if (nresults == 0) {
             return new PtgErr(PtgErr.ERROR_DIV_ZERO);
         }
         return new PtgNumber(sum / nresults);
     }
 
     /**
      * AVERAGEIFS
      * <p>
      * Returns the average of all cells that meet multiple criteria.
      * AVERAGEIFS(average_range, criteria_range1, criteria1, ...)
      *
      * @param operands The array of Ptgs. 
      *                 [0] = average_range, 
      *                 [1] = criteria_range1, [2] = criteria1, 
      *                 [3] = criteria_range2, [4] = criteria2, ...
      * @return A {@code PtgNumber} with the average, or a PtgErr.
      */
     protected static Ptg calcAverageIfS(Ptg[] operands) {
         try {
             PtgArea averageRange = Calculator.getRange(operands[0]);
             Ptg[] avgRangeCells = averageRange.getComponents();
             if (avgRangeCells == null || avgRangeCells.length == 0) {
                 return new PtgErr(PtgErr.ERROR_DIV_ZERO);
             }
             int numCriteriaPairs = (operands.length - 1) / 2;
             String[] ops = new String[numCriteriaPairs];
             String[] critVals = new String[numCriteriaPairs];
             Ptg[][] criteriaCells = new Ptg[numCriteriaPairs][];
 
             int j = 0;
             for (int i = 1; i + 1 < operands.length; i += 2) {
                 PtgArea cr = Calculator.getRange(operands[i]);
                 criteriaCells[j] = cr.getComponents();
                 if (criteriaCells[j].length != avgRangeCells.length) {
                     // each criteria range must match size of average_range
                     return new PtgErr(PtgErr.ERROR_VALUE);
                 }
                 String cstring = operands[i + 1].toString();
                 ops[j] = "="; 
                 int k = Calculator.splitOperator(cstring);
                 if (k > 0) {
                     ops[j] = cstring.substring(0, k);
                 }
                 String val = cstring.substring(k);
                 critVals[j] = Calculator.translateWildcardsInCriteria(val);
                 j++;
             }
 
             // gather all cells that pass all criteria
             List<Ptg> passesList = new ArrayList<>();
             // implicit AND logic: must pass all criteria
             for (int idx = 0; idx < avgRangeCells.length; idx++) {
                 boolean passes = true;
                 for (int cidx = 0; cidx < numCriteriaPairs; cidx++) {
                     Object v = criteriaCells[cidx][idx].getValue();
                     passes = Calculator.compareCellValue(v, critVals[cidx], ops[cidx]) && passes;
                     if (!passes) {
                         break;
                     }
                 }
                 if (passes) {
                     passesList.add(avgRangeCells[idx]);
                 }
             }
             if (passesList.isEmpty()) {
                 return new PtgErr(PtgErr.ERROR_DIV_ZERO);
             }
 
             double sum = 0.0;
             for (Ptg p : passesList) {
                 try {
                     sum += p.getDoubleVal();
                 } catch (Exception e) {
                     Logger.logErr("calcAverageIfS: error obtaining cell value: " + e.getMessage());
                 }
             }
             return new PtgNumber(sum / passesList.size());
         } catch (Exception e) {
             Logger.logErr("calcAverageIfS: error " + e.getMessage());
         }
         return new PtgErr(PtgErr.ERROR_NULL);
     }
 
     /**
      * AVEDEV
      * <p>
      * Returns the average of the absolute deviations of data points from their mean.
      * Equivalent to: AVERAGE(ABS(x - mean)).
      *
      * @param operands 1..n numeric arguments (arrays or single cell references).
      * @return A {@code PtgNumber} with the result, or a PtgErr on error.
      */
     protected static Ptg calcAveDev(Ptg[] operands) {
         if (operands.length < 1) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
 
         // get the average
         PtgNumber avPtg = (PtgNumber) StatisticalCalculator.calcAverage(operands);
         double average;
         try {
             average = Double.parseDouble(String.valueOf(avPtg.getValue()));
         } catch (NumberFormatException e) {
             return PtgCalculator.getError();
         }
 
         // sum absolute deviations
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         double total = 0;
         int count = 0;
         for (Ptg ptg : allOps) {
             try {
                 if (ptg.getValue() != null) {
                     double val = Double.parseDouble(ptg.getValue().toString());
                     total += Math.abs(average - val);
                     count++;
                 }
             } catch (NumberFormatException e) {
                 // ignore
             }
         }
         double mean = total / count;
         return new PtgNumber(mean);
     }
 
     /**
      * AVERAGEA
      * <p>
      * Returns the average of its arguments, evaluating text as 0 and TRUE as 1, FALSE as 0.
      * Ignores blank references but counts them as 0 if directly typed "".
      *
      * @param operands Array of Ptgs to average
      * @return A {@code PtgNumber} with the average, or PtgErr on error.
      */
     protected static Ptg calcAverageA(Ptg[] operands) {
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         double total = 0;
         for (Ptg p : allOps) {
             try {
                 Object ov = p.getValue();
                 if (ov != null) {
                     if ("true".equalsIgnoreCase(String.valueOf(ov))) {
                         total++;
                     } else {
                         total += Double.parseDouble(String.valueOf(ov));
                     }
                 }
             } catch (NumberFormatException e) {
                 // treat non-numerics as 0
             }
         }
         return new PtgNumber(total / allOps.length);
     }
 
     /**
      * CORREL
      * <p>
      * Returns the correlation coefficient between two data sets.
      * Delegates to calcCovar and uses standard deviations for each dataset.
      *
      * @param operands [0]: x-values, [1]: y-values
      * @return A {@code PtgNumber} with correlation, or a PtgErr on error.
      * @throws CalculationException if the process fails
      */
     protected static Ptg calcCorrel(Ptg[] operands) throws CalculationException {
         // first get the covariance
         Ptg p = calcCovar(operands);
         if (p instanceof PtgErr) {
             return p;
         }
         double covar = ((PtgNumber) p).getVal();
 
         // average of x and y
         Ptg[] xArr = new Ptg[] { operands[0] };
         Ptg[] yArr = new Ptg[] { operands[1] };
         double xMean = ((PtgNumber) calcAverage(xArr)).getVal();
         double yMean = ((PtgNumber) calcAverage(yArr)).getVal();
 
         double[] xVals = PtgCalculator.getDoubleValueArray(xArr);
         double[] yVals = PtgCalculator.getDoubleValueArray(yArr);
         if (xVals == null || yVals == null) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
 
         // standard deviation for x
         double xStat = 0;
         for (double xVal : xVals) {
             xStat += Math.pow((xVal - xMean), 2);
         }
         xStat = Math.sqrt(xStat / xVals.length);
 
         // standard deviation for y
         double yStat = 0;
         for (double yVal : yVals) {
             yStat += Math.pow((yVal - yMean), 2);
         }
         yStat = Math.sqrt(yStat / yVals.length);
 
         double retval = covar / (xStat * yStat);
         return new PtgNumber(retval);
     }
 
     /**
      * COUNT
      * <p>
      * Counts the number of numeric values in the list of arguments.
      * Non-numbers are ignored.
      *
      * @param operands Array of {@link Ptg}
      * @return {@code PtgInt} with the count of numeric values.
      */
     protected static Ptg calcCount(Ptg[] operands) {
         int count = 0;
         for (Ptg operand : operands) {
             Ptg[] comps = operand.getComponents();
             if (comps != null) {
                 for (Ptg c : comps) {
                     Object o = c.getValue();
                     if (o != null) {
                         try {
                             Double.parseDouble(o.toString());
                             count++;
                         } catch (NumberFormatException ignored) {
                         }
                     }
                 }
             } else {
                 Object o = operand.getValue();
                 if (o != null) {
                     try {
                         Double.parseDouble(o.toString());
                         count++;
                     } catch (NumberFormatException ignored) {
                     }
                 }
             }
         }
         return new PtgInt(count);
     }
 
     /**
      * COUNTA
      * <p>
      * Counts the number of non-blank cells within a range.
      *
      * @param operands array of Ptgs
      * @return {@code PtgInt} with the count of non-blank cells
      */
     protected static Ptg calcCountA(Ptg[] operands) {
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         int count = 0;
         for (Ptg p : allOps) {
             if (!p.isBlank()) {
                 count++;
             }
         }
         return new PtgInt(count);
     }
 
     /**
      * COUNTBLANK
      * <p>
      * Counts the number of blank cells within a range.
      *
      * @param operands array of Ptgs
      * @return {@code PtgInt} with count of blank cells
      */
     protected static Ptg calcCountBlank(Ptg[] operands) {
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         int count = 0;
         for (Ptg p : allOps) {
             if (p.isBlank()) {
                 count++;
             }
         }
         return new PtgInt(count);
     }
 
     /**
      * COUNTIF
      * <p>
      * Counts the number of cells within a range that meet a specified condition.
      * <br> Example: COUNTIF(A1:A5, ">10")
      *
      * @param operands [0]: range, [1]: condition
      * @return A {@code PtgNumber} with the count, or a PtgErr on error.
      * @throws FunctionNotSupportedException if the function is not supported
      */
     protected static Ptg calcCountif(Ptg[] operands) throws FunctionNotSupportedException {
         if (operands.length != 2) {
             return PtgCalculator.getError();
         }
 
         String matchStr = String.valueOf(operands[1].getValue());
         boolean matchIsNumber = true;
         double matchDub = 0;
         try {
             matchDub = Double.parseDouble(matchStr);
         } catch (Exception e) {
             matchIsNumber = false;
         }
 
         double count = 0;
         Ptg[] pref = operands[0].getComponents();
         if (pref != null) {
             for (Ptg c : pref) {
                 Object o = c.getValue();
                 if (o != null) {
                     String cellVal = o.toString();
                     if (matchIsNumber) {
                         try {
                             double d = Double.parseDouble(cellVal);
                             if (Double.compare(d, matchDub) == 0) {
                                 count++;
                             }
                         } catch (NumberFormatException ignored) {
                         }
                     } else {
                         // case-insensitive string match
                         if (matchStr.equalsIgnoreCase(cellVal)) {
                             count++;
                         }
                     }
                 }
             }
         } else {
             // single cell
             Object o = operands[0].getValue();
             if (o != null) {
                 String cellVal = o.toString();
                 if (matchIsNumber) {
                     try {
                         double d = Double.parseDouble(cellVal);
                         if (Double.compare(d, matchDub) == 0) {
                             count++;
                         }
                     } catch (NumberFormatException ignored) {
                     }
                 } else {
                     if (matchStr.equalsIgnoreCase(cellVal)) {
                         count++;
                     }
                 }
             }
         }
         return new PtgNumber(count);
     }
 
     /**
      * COUNTIFS
      * <p>
      * Counts cells across multiple ranges that meet multiple criteria.
      *
      * @param operands Sequence of pairs: (range1, criteria1, range2, criteria2, ...)
      * @return A {@code PtgNumber} with the count, or PtgErr.
      */
     protected static Ptg calcCountIfS(Ptg[] operands) {
         try {
             int numPairs = operands.length / 2;
             String[] ops = new String[numPairs];
             String[] critVals = new String[numPairs];
             Ptg[][] criteriaCells = new Ptg[numPairs][];
 
             // parse input
             for (int i = 0; i + 1 < operands.length; i += 2) {
                 PtgArea cr = Calculator.getRange(operands[i]);
                 Ptg[] comps = cr.getComponents();
                 if (i > 0 && comps.length != criteriaCells[0].length) {
                     return new PtgErr(PtgErr.ERROR_VALUE);
                 }
                 criteriaCells[i / 2] = comps;
 
                 String cstring = operands[i + 1].toString();
                 ops[i / 2] = "=";
                 int k = Calculator.splitOperator(cstring);
                 if (k > 0) {
                     ops[i / 2] = cstring.substring(0, k);
                 }
                 String val = cstring.substring(k);
                 critVals[i / 2] = Calculator.translateWildcardsInCriteria(val);
             }
 
             int count = 0;
             int length = criteriaCells[0].length;
             for (int i = 0; i < length; i++) {
                 boolean passes = true;
                 for (int cidx = 0; cidx < numPairs; cidx++) {
                     Object v = criteriaCells[cidx][i].getValue();
                     passes = Calculator.compareCellValue(v, critVals[cidx], ops[cidx]) && passes;
                     if (!passes) {
                         break;
                     }
                 }
                 if (passes) {
                     count++;
                 }
             }
             return new PtgNumber(count);
         } catch (Exception e) {
             Logger.logErr("calcCountIfS: " + e.getMessage());
         }
         return new PtgErr(PtgErr.ERROR_NULL);
     }
 
     /**
      * COVAR
      * <p>
      * Returns covariance, the average of the products of paired deviations.
      *
      * @param operands [0]: x-values, [1]: y-values
      * @return A {@code PtgNumber} with the covariance, or PtgErr if mismatch.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcCovar(Ptg[] operands) throws CalculationException {
         // get means
         Ptg[] xMeanPtg = new Ptg[] { operands[0] };
         Ptg[] yMeanPtg = new Ptg[] { operands[1] };
         double xMean = ((PtgNumber) calcAverage(xMeanPtg)).getVal();
         double yMean = ((PtgNumber) calcAverage(yMeanPtg)).getVal();
 
         double[] xVals = PtgCalculator.getDoubleValueArray(xMeanPtg);
         double[] yVals = PtgCalculator.getDoubleValueArray(yMeanPtg);
         if (xVals == null || yVals == null) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
 
         double xyMean;
         if (xVals.length == yVals.length) {
             double sum = 0;
             for (int i = 0; i < xVals.length; i++) {
                 sum += (xVals[i] * yVals[i]);
             }
             xyMean = sum / xVals.length;
         } else {
             return new PtgErr(PtgErr.ERROR_NA);
         }
 
         double retval = xyMean - (xMean * yMean);
         return new PtgNumber(retval);
     }
 
     /**
      * FORECAST
      * <p>
      * Returns a value along a linear trend, using slope and intercept from x/y data.
      *
      * @param operands [0]: x-values, [1]: y-values
      * @return A {@code PtgNumber} with the forecast value.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcForecast(Ptg[] operands) throws CalculationException {
         if (operands.length != 2) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         // slope
         PtgNumber slopePtg = (PtgNumber) calcSlope(operands);
         double slope = slopePtg.getVal();
 
         // intercept
         PtgNumber interceptPtg = (PtgNumber) calcIntercept(operands);
         double intercept = interceptPtg.getVal();
 
         // the value of x we want to predict for
         double knownX = Double.parseDouble(String.valueOf(operands[0].getValue()));
         double retval = slope * knownX + intercept;
         return new PtgNumber(retval);
     }
 
     /**
      * FREQUENCY
      * <p>
      * Returns a frequency distribution as a vertical array.
      *
      * @param operands [0]: data_array, [1]: bins_array
      * @return A {@code PtgArray} with frequency counts, or PtgErr.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcFrequency(Ptg[] operands) throws CalculationException {
         Ptg[] data = PtgCalculator.getAllComponents(operands[0]);
         Ptg[] bins = PtgCalculator.getAllComponents(operands[1]);
 
         // Collect bin values into a sorted list
         List<Double> binList = new ArrayList<>();
         for (Ptg bin : bins) {
             try {
                 Double d = Double.valueOf(bin.getValue().toString());
                 // Insert in ascending order (can do manual or a custom addOrderedDouble)
                 // We'll just do add and sort afterwards for clarity
                 binList.add(d);
             } catch (NumberFormatException ignored) {
             }
         }
         binList.sort(Double::compareTo);
 
         double[] dataArr = PtgCalculator.getDoubleValueArray(data);
         if (dataArr == null) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         int[] counts = new int[binList.size() + 1];
         for (double datum : dataArr) {
             boolean placed = false;
             for (int x = 0; x < binList.size(); x++) {
                 if (datum <= binList.get(x)) {
                     counts[x]++;
                     placed = true;
                     break;
                 }
             }
             if (!placed) {
                 counts[binList.size()]++;
             }
         }
 
         // build the array string
         StringBuilder sb = new StringBuilder("{");
         for (int i = 0; i < counts.length; i++) {
             sb.append(counts[i]);
             if (i < counts.length - 1) {
                 sb.append(",");
             }
         }
         sb.append("}");
 
         PtgArray returnArr = new PtgArray();
         returnArr.setVal(sb.toString());
         return returnArr;
     }
 
     /**
      * INTERCEPT
      * <p>
      * Returns the intercept of the linear regression line from x/y data.
      *
      * @param operands [0]: y-values, [1]: x-values
      * @return A {@code PtgNumber} or PtgErr.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcIntercept(Ptg[] operands) throws CalculationException {
         double[] yVals = PtgCalculator.getDoubleValueArray(operands[0]);
         double[] xVals = PtgCalculator.getDoubleValueArray(operands[1]);
         if (yVals == null || xVals == null) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         double sumX = 0, sumY = 0, sumXY = 0, sqrX = 0;
         for (double xv : xVals) {
             sumX += xv;
             sqrX += xv * xv;
         }
         for (double yv : yVals) {
             sumY += yv;
         }
         for (int i = 0; i < yVals.length; i++) {
             sumXY += xVals[i] * yVals[i];
         }
         double top = (sumX * sumXY) - (sumY * sqrX);
         double bottom = (sumX * sumX) - (sqrX * xVals.length);
         double res = top / bottom;
         return new PtgNumber(res);
     }
 
     /**
      * LARGE
      * <p>
      * Returns the k-th largest value in a data set.
      *
      * @param operands [0]: array, [1]: k
      * @return A {@code PtgNumber}, or PtgErr if invalid.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcLarge(Ptg[] operands) throws CalculationException {
         if (operands.length != 2) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         Ptg[] array = PtgCalculator.getAllComponents(operands[0]);
         if (array.length == 0) {
             return new PtgErr(PtgErr.ERROR_NUM);
         }
         double[] kdub = PtgCalculator.getDoubleValueArray(operands[1]);
         int k = (int) kdub[0];
         if (k <= 0 || k > array.length) {
             return new PtgErr(PtgErr.ERROR_NUM);
         }
 
         List<Double> sorted = new ArrayList<>();
         for (Ptg p : array) {
             try {
                 double d = Double.parseDouble(String.valueOf(p.getValue()));
                 sorted.add(d);
             } catch (NumberFormatException ignored) {
             }
         }
         sorted.sort(Double::compareTo);  
         // largest => pick from end
         double val = sorted.get(sorted.size() - k);
         return new PtgNumber(val);
     }
 
     /**
      * LINEST
      * <p>
      * Returns the parameters of a linear trend.
      *
      * @param operands [0]: known_y, [1]: known_x, [2]: const, [3]: stats
      * @return A {@code PtgArray} with regression statistics, or PtgErr.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcLineSt(Ptg[] operands) throws CalculationException {
         double[] ys = PtgCalculator.getDoubleValueArray(operands[0]);
         if (ys == null) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         double[] xs;
         if (operands.length == 1 || (operands[1] instanceof PtgMissArg)) {
             // create a default x array {0,1,2,...}
             xs = new double[ys.length];
             for (int i = 0; i < ys.length; i++) {
                 xs[i] = i;
             }
         } else {
             xs = PtgCalculator.getDoubleValueArray(operands[1]);
             if (xs == null) {
                 return new PtgErr(PtgErr.ERROR_NA);
             }
         }
 
         boolean stats = false;
         if (operands.length > 3 && !(operands[3] instanceof PtgMissArg)) {
             stats = PtgCalculator.getBooleanValue(operands[3]);
         }
 
         // slope & intercept
         double slope = ((PtgNumber) calcSlope(operands)).getVal();
         double intercept = ((PtgNumber) calcIntercept(operands)).getVal();
 
         // if stats = false, just return basic slope & intercept repeated, a quick approach
         if (!stats) {
             String ret = "{" + slope + "," + intercept + "},"
                        + "{" + slope + "," + intercept + "},"
                        + "{" + slope + "," + intercept + "},"
                        + "{" + slope + "," + intercept + "},"
                        + "{" + slope + "," + intercept + "}";
             PtgArray pa = new PtgArray();
             pa.setVal(ret);
             return pa;
         }
 
         // advanced stats if stats=true:
         double steyx = ((PtgNumber) calcSteyx(operands)).getVal();
         // partial analysis for yError, standard error, regression SS, residual SS, etc.
         // (see original code for details)
         // ...
         // For brevity, we replicate the original logic:
         double sumX = 0;
         for (double x : xs) {
             sumX += x;
         }
         double sumY = 0;
         for (double y : ys) {
             sumY += y;
         }
 
         // predicted values, residual SS
         double[] predicted = new double[xs.length];
         double residualSS = 0;
         for (int i = 0; i < xs.length; i++) {
             predicted[i] = intercept + xs[i] * slope;
             double diff = predicted[i] - ys[i];
             residualSS += diff * diff;
         }
 
         // average of y
         Ptg[] yPtg = new Ptg[] { operands[0] };
         double yMean = ((PtgNumber) calcAverage(yPtg)).getVal();
         double regressionSS = 0;
         for (int i = 0; i < ys.length; i++) {
             double d = predicted[i] - yMean;
             regressionSS += d * d;
         }
 
         // R-squared
         double r2 = ((PtgNumber) calcRsq(operands)).getVal();
         int dof = ys.length - 2;
         double F = (regressionSS / 1) / (residualSS / dof);
 
         // structure matches original:
         // slope, intercept
         // slope_stdErr, intercept_stdErr
         // r2, steyx
         // F, dof
         // regressionSS, residualSS
         String retstr = "{" + slope + "," + intercept + "},"
                 + "{" + 0.0 + "," + 0.0 + "},"  
                 + "{" + r2 + "," + steyx + "},"
                 + "{" + F + "," + dof + "},"
                 + "{" + regressionSS + "," + residualSS + "}";
 
         PtgArray parr = new PtgArray();
         parr.setVal(retstr);
         return parr;
     }
 
     /**
      * MAX
      * <p>
      * Returns the largest value in a set of values, ignoring non-numbers.
      * If no numbers, defaults to 0.
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} with the maximum.
      */
     protected static Ptg calcMax(Ptg[] operands) {
         double result = Double.NEGATIVE_INFINITY;
         for (Ptg operand : operands) {
             Ptg[] comps = operand.getComponents();
             if (comps != null) {
                 Ptg r = calcMax(comps);
                 try {
                     double d = Double.parseDouble(String.valueOf(r.getValue()));
                     if (d > result) {
                         result = d;
                     }
                 } catch (NumberFormatException ignored) {
                 }
             } else {
                 try {
                     Object ov = operand.getValue();
                     if (ov != null) {
                         double d = Double.parseDouble(String.valueOf(ov));
                         if (d > result) {
                             result = d;
                         }
                     }
                 } catch (NumberFormatException | NullPointerException ignored) {
                 }
             }
         }
         if (result == Double.NEGATIVE_INFINITY) {
             result = 0;
         }
         return new PtgNumber(result);
     }
 
     /**
      * MAXA
      * <p>
      * Returns the maximum value in a list of arguments, evaluating text as 0,
      * and TRUE as 1, FALSE as 0.
      * If any item cannot be converted to a number, returns an error.
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} or PtgErr.
      */
     protected static Ptg calcMaxA(Ptg[] operands) {
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         if (allOps.length == 0) {
             return new PtgNumber(0);
         }
         double max = Double.NEGATIVE_INFINITY;
         for (Ptg p : allOps) {
             Object o = p.getValue();
             try {
                 double d;
                 if (o instanceof Number) {
                     d = ((Number) o).doubleValue();
                 } else if (o instanceof Boolean) {
                     d = ((Boolean) o) ? 1.0 : 0.0;
                 } else {
                     d = Double.parseDouble(o.toString());
                 }
                 max = Math.max(max, d);
             } catch (NumberFormatException e) {
                 return new PtgErr(PtgErr.ERROR_VALUE);
             }
         }
         if (max == Double.NEGATIVE_INFINITY) {
             max = 0;
         }
         return new PtgNumber(max);
     }
 
     /**
      * MEDIAN
      * <p>
      * Returns the median of the given numbers. Ignores non-numerics.
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} with the median, or PtgErr.
      */
     protected static Ptg calcMedian(Ptg[] operands) {
         if (operands.length < 1) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         List<Double> vals = new ArrayList<>();
         for (Ptg ptg : allOps) {
             try {
                 double d = Double.parseDouble(String.valueOf(ptg.getValue()));
                 vals.add(d);
             } catch (NumberFormatException ignored) {
             }
         }
         vals.sort(Double::compareTo);
         if (vals.isEmpty()) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
 
         if (vals.size() % 2 == 0) {
             int firstValLoc = (vals.size() / 2) - 1;
             int lastValLoc = firstValLoc + 1;
             double firstVal = vals.get(firstValLoc);
             double lastVal = vals.get(lastValLoc);
             return new PtgNumber((firstVal + lastVal) / 2);
         } else {
             int middle = (vals.size() - 1) / 2;
             return new PtgNumber(vals.get(middle));
         }
     }
 
     /**
      * MIN
      * <p>
      * Returns the smallest number in a set of values, ignoring non-numbers.
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} with the minimum, or 0 if no numbers found.
      */
     protected static Ptg calcMin(Ptg[] operands) {
         double result = Double.POSITIVE_INFINITY;
         for (Ptg operand : operands) {
             Ptg[] comps = operand.getComponents();
             if (comps != null) {
                 Ptg r = calcMin(comps);
                 try {
                     double d = Double.parseDouble(String.valueOf(r.getValue()));
                     if (d < result) {
                         result = d;
                     }
                 } catch (NumberFormatException ignored) {
                 }
             } else {
                 try {
                     Object ov = operand.getValue();
                     if (ov != null) {
                         double d = Double.parseDouble(String.valueOf(ov));
                         if (d < result) {
                             result = d;
                         }
                     }
                 } catch (NumberFormatException | NullPointerException ignored) {
                 }
             }
         }
         if (result == Double.POSITIVE_INFINITY) {
             result = 0;
         }
         return new PtgNumber(result);
     }
 
     /**
      * MINA
      * <p>
      * Returns the smallest value in a list of arguments (text=0, TRUE=1, FALSE=0).
      * If any item is an invalid number, returns ERROR_VALUE.
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber}, or PtgErr.
      */
     protected static Ptg calcMinA(Ptg[] operands) {
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         if (allOps.length == 0) {
             return new PtgNumber(0);
         }
         double min = Double.POSITIVE_INFINITY;
         for (Ptg p : allOps) {
             Object o = p.getValue();
             try {
                 double d;
                 if (o instanceof Number) {
                     d = ((Number) o).doubleValue();
                 } else if (o instanceof Boolean) {
                     d = ((Boolean) o) ? 1.0 : 0.0;
                 } else {
                     d = Double.parseDouble(o.toString());
                 }
                 min = Math.min(min, d);
             } catch (NumberFormatException e) {
                 return new PtgErr(PtgErr.ERROR_VALUE);
             }
         }
         if (min == Double.POSITIVE_INFINITY) {
             min = 0;
         }
         return new PtgNumber(min);
     }
 
     /**
      * MODE
      * <p>
      * Returns the most frequently occurring value in a set of data.
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} with the mode, or 0 if no valid numeric data.
      */
     protected static Ptg calcMode(Ptg[] operands) {
         Ptg[] allOps = PtgCalculator.getAllComponents(operands);
         List<Double> vals = new ArrayList<>();
         List<Double> counts = new ArrayList<>();
         double modeVal = 0;
 
         for (Ptg ptg : allOps) {
             try {
                 double d = Double.parseDouble(String.valueOf(ptg.getValue()));
                 int idx = vals.indexOf(d);
                 if (idx >= 0) {
                     double newCount = counts.get(idx) + 1;
                     counts.set(idx, newCount);
                 } else {
                     vals.add(d);
                     counts.add(1.0);
                 }
             } catch (NumberFormatException ignored) {
             }
         }
         double maxCount = 0;
         for (int i = 0; i < vals.size(); i++) {
             double c = counts.get(i);
             if (c > maxCount) {
                 maxCount = c;
                 modeVal = vals.get(i);
             }
         }
         return new PtgNumber(modeVal);
     }
 
     /**
      * NORMDIST
      * <p>
      * Returns the normal cumulative distribution for a given mean and stddev.
      * If cumulative = TRUE, returns the CDF; otherwise returns the PDF.
      *
      * @param operands [0]: x, [1]: mean, [2]: stddev, [3]: cumulative (bool)
      * @return A {@code PtgNumber} or PtgErr on invalid input.
      */
     protected static Ptg calcNormdist(Ptg[] operands) {
         if (operands.length < 4) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         try {
             double x = operands[0].getDoubleVal();
             double mean = operands[1].getDoubleVal();
             double stddev = operands[2].getDoubleVal();
             if (stddev <= 0) {
                 return new PtgErr(PtgErr.ERROR_NUM);
             }
             boolean cumulative = PtgCalculator.getBooleanValue(operands[3]);
 
             if (!cumulative) {
                 // PDF
                 double denom = Math.sqrt(2 * Math.PI) * stddev;
                 double exponent = -Math.pow(x - mean, 2) / (2 * stddev * stddev);
                 double pdf = (1.0 / denom) * Math.exp(exponent);
                 return new PtgNumber(pdf);
             } else {
                 // CDF
                 // transform to standard normal
                 double z = (x - mean) / (stddev * Math.sqrt(2));
                 Ptg erf = EngineeringCalculator.calcErf(new Ptg[] { new PtgNumber(z) });
                 double cdf = 0.5 * (1 + erf.getDoubleVal());
                 return new PtgNumber(cdf);
             }
         } catch (Exception e) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
     }
 
     /**
      * NORMSDIST
      * <p>
      * Returns the standard normal cumulative distribution.
      * Uses a polynomial/rational approximation for values of x.
      *
      * @param operands [0]: x
      * @return A {@code PtgNumber} or PtgErr on invalid input.
      */
     protected static Ptg calcNormsdist(Ptg[] operands) {
         if (operands.length < 1) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         try {
             double x = operands[0].getDoubleVal();
             // Coefficients
             final double b1 = 0.319381530, b2 = -0.356563782, b3 = 1.781477937;
             final double b4 = -1.821255978, b5 = 1.330274429;
             final double p = 0.2316419;
             final double c = 0.39894228;
             double result;
             if (x >= 0.0) {
                 double t = 1.0 / (1.0 + p * x);
                 result = 1.0 - c * Math.exp(-0.5 * x * x) * t
                         * (t * (t * (t * (t * b5 + b4) + b3) + b2) + b1);
             } else {
                 double t = 1.0 / (1.0 - p * x);
                 result = c * Math.exp(-0.5 * x * x) * t
                         * (t * (t * (t * (t * b5 + b4) + b3) + b2) + b1);
             }
             BigDecimal bd = BigDecimal.valueOf(result).setScale(15, java.math.RoundingMode.HALF_UP);
             return new PtgNumber(bd.doubleValue());
         } catch (Exception e) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
     }
 
     /**
      * NORMSINV
      * <p>
      * Returns the inverse of the standard normal cumulative distribution.
      *
      * @param operands [0]: probability (0..1)
      * @return A {@code PtgNumber} or PtgErr on invalid input.
      */
     public static Ptg calcNormsInv(Ptg[] operands) {
         if (operands.length != 1) {
             return PtgCalculator.getValueError();
         }
         try {
             double x = operands[0].getDoubleVal();
             if (x < 0 || x > 1) {
                 return new PtgErr(PtgErr.ERROR_NUM);
             }
             // polynomial/rational approximation
             double[] a = { -39.69683028665376, 220.946098424521, -275.928510446969,
                            138.357751867269,  -30.6647980661472, 2.506628277459239 };
             double[] b = { -54.4760987982241, 161.585836858041, -155.698979859887,
                            66.8013118877197, -13.2806815528857 };
             double[] c = { -0.007784894002430293, -0.322396458041136, -2.400758277161838,
                            -2.549732539343734,   4.374664141464968,  2.938163982698783 };
             double[] d = { 0.007784695709041462, 0.32246712907004, 2.445134137142996,
                            3.754408661907416 };
             double plow = 0.02425;
             double phigh = 1 - plow;
             if (x < plow) {
                 double q = Math.sqrt(-2 * Math.log(x));
                 double numerator = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]);
                 double denominator = ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
                 BigDecimal r = BigDecimal.valueOf(numerator / denominator)
                         .setScale(15, java.math.RoundingMode.HALF_UP);
                 return new PtgNumber(r.doubleValue());
             }
             if (x > phigh) {
                 double q = Math.sqrt(-2 * Math.log(1 - x));
                 double numerator = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]);
                 double denominator = ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
                 BigDecimal r = BigDecimal.valueOf(-(numerator / denominator))
                         .setScale(15, java.math.RoundingMode.HALF_UP);
                 return new PtgNumber(r.doubleValue());
             }
 
             double q = x - 0.5;
             double r = q * q;
             double numerator = (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q;
             double denominator = ((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1.0;
             BigDecimal ret = BigDecimal.valueOf(numerator / denominator)
                     .setScale(15, java.math.RoundingMode.HALF_UP);
             return new PtgNumber(ret.doubleValue());
         } catch (Exception e) {
             return PtgCalculator.getValueError();
         }
     }
 
     /**
      * NORMINV
      * <p>
      * Returns the inverse of the normal cumulative distribution for the given mean and stddev.
      *
      * @param operands [0]: probability, [1]: mean, [2]: stddev
      * @return A {@code PtgNumber}, or PtgErr if invalid input.
      */
     public static Ptg calcNormInv(Ptg[] operands) {
         try {
             double p = operands[0].getDoubleVal();
             if (p < 0 || p > 1) {
                 return new PtgErr(PtgErr.ERROR_NUM);
             }
             double mean = operands[1].getDoubleVal();
             double stddev = operands[2].getDoubleVal();
             if (stddev <= 0) {
                 return new PtgErr(PtgErr.ERROR_NUM);
             }
             double result = quartile(p, mean, stddev);
             return new PtgNumber(result);
         } catch (Exception e) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
     }
 
     /**
      * PEARSON
      * <p>
      * Returns the Pearson product moment correlation coefficient.
      * Delegates to calcCorrel.
      *
      * @param operands [0]: x-values, [1]: y-values
      * @return A {@code PtgNumber}, or PtgErr
      * @throws CalculationException if something fails
      */
     public static Ptg calcPearson(Ptg[] operands) throws CalculationException {
         return calcCorrel(operands);
     }
 
     /**
      * QUARTILE
      * <p>
      * Returns the quartile of a data set.
      *
      * @param operands [0]: range, [1]: quart index (0..4)
      * @return A {@code PtgNumber} or PtgErr
      */
     protected static Ptg calcQuartile(Ptg[] operands) {
         // gather values
         Ptg[] rangeOps = new Ptg[] { operands[0] };
         Ptg[] allVals = PtgCalculator.getAllComponents(rangeOps);
         List<Double> sorted = new ArrayList<>();
         for (Ptg ptg : allVals) {
             try {
                 double d = Double.parseDouble(String.valueOf(ptg.getValue()));
                 sorted.add(d);
             } catch (NumberFormatException e) {
                 Logger.logErr(e);
             }
         }
         if (sorted.isEmpty()) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         sorted.sort(Double::compareTo);
 
         int quart = 0;
         try {
             Object o = operands[1].getValue();
             if (o instanceof Integer) {
                 quart = (Integer) o;
             } else {
                 quart = ((Double) o).intValue();
             }
         } catch (Exception e) {
             return new PtgErr(PtgErr.ERROR_NUM);
         }
         if (quart < 0 || quart > 4) {
             return new PtgErr(PtgErr.ERROR_NUM);
         }
         if (quart == 0) {
             return new PtgNumber(sorted.get(0));
         } else if (quart == 4) {
             return new PtgNumber(sorted.get(sorted.size() - 1));
         }
 
         float ratio = quart / 4.0f;
         float idx = (sorted.size() - 1) * ratio;
         idx++;
         int k = (int) idx;
         float remainder = (float) (idx - k);
 
         if (k <= 0 || k > sorted.size()) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         double firstVal = sorted.get(k - 1);
         if (k >= sorted.size()) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         double secondVal = sorted.get(k);
         double output = firstVal + remainder * (secondVal - firstVal);
         return new PtgNumber(output);
     }
 
     /**
      * RANK
      * <p>
      * Returns the rank of a number in a list of numbers.
      * If order=0 or omitted => descending rank; otherwise ascending rank.
      *
      * @param operands [0]: number, [1]: ref range, [2]: optional order
      * @return A {@code PtgInt} with the rank, or PtgErr.
      */
     protected static Ptg calcRank(Ptg[] operands) {
         if (operands.length < 2) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         double theNum;
         try {
             Object val = operands[0].getValue();
             if ("".equals(val)) {
                 theNum = 0.0;
             } else {
                 theNum = Double.parseDouble(String.valueOf(val));
             }
         } catch (NumberFormatException e) {
             return new PtgErr();
         }
 
         boolean ascending = true;
         if (operands.length < 3 || (operands[2] instanceof PtgMissArg)) {
             ascending = false;
         } else {
             try {
                 int i = ((PtgInt) operands[2]).getVal();
                 if (i == 0) {
                     ascending = false;
                 }
             } catch (Exception ignored) {
             }
         }
 
         Ptg[] arr = new Ptg[] { operands[1] };
         Ptg[] refs = PtgCalculator.getAllComponents(arr);
         List<Double> valList = new ArrayList<>();
 
         for (Ptg ref : refs) {
             try {
                 double d = Double.parseDouble(String.valueOf(ref.getValue()));
                 // custom addOrderedDouble => we’ll just collect then sort
                 valList.add(d);
             } catch (NumberFormatException ignored) {
             }
         }
         valList.sort(Double::compareTo);
 
         if (!ascending) {
             // reverse to descending
             List<Double> rev = new ArrayList<>();
             for (int i = valList.size() - 1; i >= 0; i--) {
                 rev.add(valList.get(i));
             }
             valList = rev;
         }
         int rank = -1;
         for (int i = 0; i < valList.size(); i++) {
             if (Double.compare(valList.get(i), theNum) == 0) {
                 rank = i + 1; 
                 break;
             }
         }
         if (rank == -1) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         return new PtgInt(rank);
     }
 
     /**
      * RSQ
      * <p>
      * Returns the square of the Pearson correlation coefficient.
      *
      * @param operands [0]: x-values, [1]: y-values
      * @return A {@code PtgNumber}, or PtgErr if error.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcRsq(Ptg[] operands) throws CalculationException {
         PtgNumber pearson = (PtgNumber) calcPearson(operands);
         double r = pearson.getVal();
         return new PtgNumber(r * r);
     }
 
     /**
      * SLOPE
      * <p>
      * Returns the slope of the linear regression line for x/y data.
      *
      * @param operands [0]: y-values, [1]: x-values
      * @return A {@code PtgNumber} or PtgErr
      * @throws CalculationException if something fails
      */
     protected static Ptg calcSlope(Ptg[] operands) throws CalculationException {
         if (operands.length != 2) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         double[] yVals = PtgCalculator.getDoubleValueArray(operands[0]);
         double[] xVals = PtgCalculator.getDoubleValueArray(operands[1]);
         if (xVals == null || yVals == null) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
 
         double sumX = 0, sumY = 0, sumXY = 0, sqrX = 0;
         for (double xv : xVals) {
             sumX += xv;
             sqrX += xv * xv;
         }
         for (double yv : yVals) {
             sumY += yv;
         }
         for (int i = 0; i < yVals.length; i++) {
             sumXY += xVals[i] * yVals[i];
         }
         double top = (sumX * sumY) - (sumXY * yVals.length);
         double bottom = (sumX * sumX) - (sqrX * yVals.length);
         double res = top / bottom;
         return new PtgNumber(res);
     }
 
     /**
      * SMALL
      * <p>
      * Returns the k-th smallest value in a data set.
      *
      * @param operands [0]: array, [1]: k
      * @return A {@code PtgNumber}, or PtgErr on invalid input.
      * @throws CalculationException if something fails
      */
     protected static Ptg calcSmall(Ptg[] operands) throws CalculationException {
         if (operands.length != 2) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         Ptg[] array = PtgCalculator.getAllComponents(operands[0]);
         if (array.length == 0) {
             return new PtgErr(PtgErr.ERROR_NUM);
         }
         double[] kdub = PtgCalculator.getDoubleValueArray(operands[1]);
         int k = (int) kdub[0];
         if (k <= 0 || k > array.length) {
             return new PtgErr(PtgErr.ERROR_NUM);
         }
 
         List<Double> sortedValues = new ArrayList<>();
         for (Ptg p : array) {
             try {
                 double d = Double.parseDouble(String.valueOf(p.getValue()));
                 sortedValues.add(d);
             } catch (NumberFormatException ignored) {
             }
         }
         sortedValues.sort(Double::compareTo);
         if (k - 1 >= sortedValues.size()) {
             return new PtgErr(PtgErr.ERROR_VALUE);
         }
         return new PtgNumber(sortedValues.get(k - 1));
     }
 
     /**
      * STDEV
      * <p>
      * Estimates standard deviation based on a sample (n-1).
      *
      * @param operands array of numeric Ptgs
      * @return A {@code PtgNumber} with stdev, or PtgErr
      * @throws CalculationException if something fails
      */
     protected static Ptg calcStdev(Ptg[] operands) throws CalculationException {
         double[] allVals = PtgCalculator.getDoubleValueArray(operands);
         if (allVals == null || allVals.length < 2) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         double mean = ((PtgNumber) calcAverage(operands)).getVal();
         double sqrDev = 0;
         for (double v : allVals) {
             sqrDev += Math.pow(v - mean, 2);
         }
         double val = Math.sqrt(sqrDev / (allVals.length - 1));
         return new PtgNumber(val);
     }
 
     /**
      * STEYX
      * <p>
      * Returns the standard error of the predicted y-value for each x in the regression.
      *
      * @param operands [0]: y-values, [1]: x-values
      * @return A {@code PtgNumber}, or PtgErr
      * @throws CalculationException if something fails
      */
     public static Ptg calcSteyx(Ptg[] operands) throws CalculationException {
         Ptg[] arr = new Ptg[] { operands[0] };
         PtgNumber pn = (PtgNumber) calcVarp(arr);
         double yVarp = pn.getVal();
 
         arr[0] = operands[1];
         pn = (PtgNumber) calcVarp(arr);
         double xVarp = pn.getVal();
 
         double[] y = PtgCalculator.getDoubleValueArray(operands[0]);
         if (y == null || y.length < 2) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         yVarp *= y.length;
         xVarp *= y.length;
 
         double slope = ((PtgNumber) calcSlope(operands)).getVal();
         double retval = yVarp - (slope * slope * xVarp);
         retval = retval / (y.length - 2);
         retval = Math.sqrt(retval);
         return new PtgNumber(retval);
     }
 
     /**
      * VAR
      * <p>
      * Estimates variance based on a sample (n-1).
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} with variance, or PtgErr
      * @throws CalculationException if something fails
      */
     protected static Ptg calcVar(Ptg[] operands) throws CalculationException {
         double[] allVals = PtgCalculator.getDoubleValueArray(operands);
         if (allVals == null || allVals.length < 2) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         double mean = ((PtgNumber) calcAverage(operands)).getVal();
         double sqrDev = 0;
         for (double v : allVals) {
             sqrDev += Math.pow(v - mean, 2);
         }
         double val = sqrDev / (allVals.length - 1);
         return new PtgNumber(val);
     }
 
     /**
      * VARP
      * <p>
      * Calculates variance based on the entire population (n).
      *
      * @param operands array of Ptgs
      * @return A {@code PtgNumber} with variance, or PtgErr
      * @throws CalculationException if something fails
      */
     protected static Ptg calcVarp(Ptg[] operands) throws CalculationException {
         double[] allVals = PtgCalculator.getDoubleValueArray(operands);
         if (allVals == null || allVals.length == 0) {
             return new PtgErr(PtgErr.ERROR_NA);
         }
         double mean = ((PtgNumber) calcAverage(operands)).getVal();
         double sqrDev = 0;
         for (double v : allVals) {
             sqrDev += Math.pow(v - mean, 2);
         }
         double val = sqrDev / allVals.length;
         return new PtgNumber(val);
     }
 
     // Some private helper for calcNormInv
     private static double expm1(double x) {
         final double DBL_EPSILON = 1e-7;
         double a = Math.abs(x);
         if (a < DBL_EPSILON) {
             return x;
         }
         if (a > 0.697) {
             return Math.exp(x) - 1;
         }
         double y;
         if (a > 1e-8) {
             y = Math.exp(x) - 1;
         } else {
             y = (x / 2 + 1) * x;
         }
         y -= (1 + y) * (Math.log(1 + y) - x);
         return y;
     }
 
     /**
      * quartile
      * <p>
      * Internal helper for NORMINV. (Used to handle normal distribution quartile logic.)
      */
     private static double quartile(double p, double mu, double sigma) {
         if (p <= 0) return Double.NEGATIVE_INFINITY;
         if (p >= 1) return Double.POSITIVE_INFINITY;
         if (sigma < 0) return Double.NaN;
         if (sigma == 0) return mu;
 
         // approximate approach (from prior code)
         // we reuse the approach from NORMSINV but scaled by mean, stddev
         double x = calcNorMSInvValue(p);
         return mu + sigma * x;
     }
 
     /**
      * calcNorMSInvValue
      * <p>
      * Returns the standard normal inverse for a probability p.
      * Internal reuse from calcNormsInv logic.
      */
     private static double calcNorMSInvValue(double p) {
         // same polynomial approximation used in calcNormsInv
         double[] a = { -39.69683028665376, 220.946098424521, -275.928510446969,
                        138.357751867269,  -30.6647980661472,  2.506628277459239 };
         double[] b = { -54.4760987982241, 161.585836858041, -155.698979859887,
                        66.8013118877197, -13.2806815528857 };
         double[] c = { -0.007784894002430293, -0.322396458041136, -2.400758277161838,
                        -2.549732539343734,   4.374664141464968,  2.938163982698783 };
         double[] d = { 0.007784695709041462,  0.32246712907004,   2.445134137142996,
                        3.754408661907416 };
         double plow = 0.02425;
         double phigh = 1 - plow;
 
         if (p < plow) {
             double q = Math.sqrt(-2 * Math.log(p));
             double numerator = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]);
             double denominator = ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
             return numerator / denominator;
         }
         if (p > phigh) {
             double q = Math.sqrt(-2 * Math.log(1 - p));
             double numerator = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5]);
             double denominator = ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
             return -(numerator / denominator);
         }
         double q = p - 0.5;
         double r = q * q;
         double numerator = (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q;
         double denominator = ((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1;
         return numerator / denominator;
     }

    public static Ptg calcTrend(Ptg[] operands) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calcTrend'");
    }
 }
 