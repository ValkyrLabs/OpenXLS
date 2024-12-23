/*
 * --------- BEGIN COPYRIGHT NOTICE ---------
 * Copyright 2002-2012 Extentech Inc.
 * Copyright 2013 Infoteria America Corp.
 *
 * This file is part of OpenXLS.
 *
 * OpenXLS is free software: you can redistribute it and/or
 * modify
 * it under the terms of the GNU Lesser General Public
 * License as
 * published by the Free Software Foundation, either version
 * 3 of
 * the License, or (at your option) any later version.
 *
 * OpenXLS is distributed in the hope that it will be
 * useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public
 * License along with OpenXLS. If not, see
 * <http://www.gnu.org/licenses/>.
 * ---------- END COPYRIGHT NOTICE ----------
 */
package com.valkyrlabs.formats.XLS.charts;

import com.valkyrlabs.OpenXLS.CellRange;
import com.valkyrlabs.OpenXLS.ChartSeriesHandle;
import com.valkyrlabs.OpenXLS.WorkBookHandle;
import com.valkyrlabs.formats.XLS.FormatConstants;
import com.valkyrlabs.formats.XLS.WorkBook;
import com.valkyrlabs.toolkit.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * non-stacked area chart
 */
public class AreaChart extends ChartType {
    Area area = null;

    public AreaChart(GenericChartObject charttype, ChartFormat cf, WorkBook wb) {
        super(charttype, cf, wb);
        area = (Area) charttype;
    }

    public JSONObject getJSON(ChartSeriesHandle[] series, WorkBookHandle wbh, Double[] minMax) throws JSONException {
        final JSONObject chartObjectJSON = new JSONObject();
        // Type JSON
        chartObjectJSON.put("type", this.getTypeJSON());

        // Series
        double yMax = 0.0, yMin = 0.0;
        int nSeries = 0;
        final JSONArray seriesJSON = new JSONArray();
        final JSONArray seriesCOLORS = new JSONArray();
        try {
            for (int i = 0; i < series.length; i++) {
                final JSONArray seriesvals = CellRange
                        .getValuesAsJSON(series[i].getSeriesRange(), wbh);
                // must trap min and max for axis tick and units
                double sum = 0.0; // for area-type charts, ymax is the sum of
                // all points in same series
                nSeries = Math.max(nSeries, seriesvals.length());
                for (int j = 0; j < seriesvals.length(); j++) {
                    try {
                        sum += seriesvals.getDouble(j);
                        yMax = Math.max(yMax, sum);
                        yMin = Math.min(yMin, seriesvals.getDouble(j));
                    } catch (final NumberFormatException n) {
                    }
                }
                seriesJSON.put(seriesvals);
                seriesCOLORS.put(FormatConstants.SVGCOLORSTRINGS[series[i]
                        .getSeriesColor()]);
            }
            chartObjectJSON.put("Series", seriesJSON);
            chartObjectJSON.put("SeriesFills", seriesCOLORS);
        } catch (final JSONException je) {
            // TODO: Log error
        }
        minMax[0] = yMin;
        minMax[1] = yMax;
        minMax[2] = (double) nSeries;
        return chartObjectJSON;

    }

    /**
     * return the type JSON for this Chart Object
     *
     * @return
     */
    @Override
    public JSONObject getTypeJSON() throws JSONException {
        final JSONObject typeJSON = new JSONObject();
        String dojoType;
        if (!this.isStacked()) {
            dojoType = "Areas";
        } else {
            dojoType = "StackedAreas";
        }
        typeJSON.put("type", dojoType);
        return typeJSON;
    }

    /**
     * returns SVG to represent the actual chart object (BAR, LINE, AREA, etc.)
     *
     * @param chartMetrics maps chart coords in pixels x, y, w, h, canvasw, canvash, min, max
     * @param axisMetrics  maps specific axis options such as xAxisReversed, xPattern ...
     * @param categories
     * @param series       arraylist of double[] series values
     * @param seriescolors int[] of series or bar colors color ints
     * @return String svg
     */
    @Override
    public String getSVG(HashMap<String, Double> chartMetrics, HashMap<String, Object> axisMetrics, ChartSeries s) {
        final double x = chartMetrics.get("x");
        final double y = chartMetrics.get("y");
        final double w = chartMetrics.get("w");
        final double h = chartMetrics.get("h");
        final double max = chartMetrics.get("max");
        chartMetrics.get("min");
        final Object[] categories = s.getCategories();
        final ArrayList series = s.getSeriesValues();
        final String[] seriescolors = s.getSeriesBarColors();
        final String[] legends = s.getLegends();
        // x value for each point= w/(ncategories + 1) 1st one is
        // xv*2 then increases from there
        // y value for each point= h/YMAX
        if (series.size() == 0) {
            Logger.logErr("Area.getSVG: error in series");
            return "";
        }
        final StringBuffer svg = new StringBuffer();
        final int[] dls = getDataLabelInts(); // get array of data labels (can
        // be specific per series ...)

        double xfactor = 0, yfactor = 0; //
        if (categories.length > 1) {
            xfactor = w / (categories.length - 1); // w/#categories
        }
        if (max != 0) {
            yfactor = h / max; // h/YMAXSCALE
        }

        // for each series
        final int n = series.size();
        for (int i = n - 1; i >= 0; i--) { // "paint" right to left
            svg.append("<g>\r\n");
            String points = "";
            double x1 = 0;
            double y1 = 0;
            String labels = null;
            final double[] curseries = (double[]) series.get(i);
            for (int j = 0; j < curseries.length; j++) {
                x1 = (x) + j * xfactor;
                final double yval = curseries[j]; // areapoints[j][i]; //
                // current point
                points += ((x) + (j) * xfactor) + ","
                        + ((y + h) - (yval * yfactor));

                if (j == 0) {
                    y1 = ((y + h) - (yval * yfactor)); // end point (==start
                    // point) for path
                    // statement below
                }
                points += " ";
                // DATA LABELS
                final String l = getSVGDataLabels(dls, axisMetrics, curseries[j], 0, i, legends, categories[j]
                        .toString());
                if (l != null) {
                    // if only category label, center over all series; anything
                    // else, position at data point
                    final boolean showCategories = (dls[i]
                            & AttachedLabel.CATEGORYLABEL) == AttachedLabel.CATEGORYLABEL;
                    final boolean showValueLabel = (dls[i]
                            & AttachedLabel.VALUELABEL) == AttachedLabel.VALUELABEL;
                    final boolean showValue = (dls[i]
                            & AttachedLabel.VALUE) == AttachedLabel.VALUE;
                    if (showCategories && !(showValue || showValueLabel)
                            && j == 0) { // only 1 label, centered along
                        // category axis within area
                        final double hh = y1; // (areapoints[areapoints.length/2][i]*yfactor);
                        final double yy = (y + h) - hh + 10;
                        if (labels == null) {
                            labels = "";
                        }
                        labels = "<text x='" + (x + w / 2) + "' y='" + yy
                                + "' vertical-align='middle' "
                                + ChartType.getDataLabelFontSVG()
                                + " style='text-align:middle;'>" + l
                                + "</text>\r\n";
                    } else if (showValue || showValueLabel) { // labels at each
                        // data point
                        if (labels == null) {
                            labels = "";
                        }
                        final double yy = (((y + h)
                                - ((yval - (curseries[j] * .5)) * yfactor)));
                        labels += "<text x='" + x1 + "' y='" + yy
                                + "' style='text-anchor: middle;' "
                                + ChartType.getDataLabelFontSVG()/*
                         * +" fill='"
                         * +getDarkColor
                         * ()+"'
                         */ + ">"
                                + l + "</text>\r\n";
                    }
                }
            }
            // pointsends connects up area to beginning
            final double x0 = x;
            final String pointsend = x1 + "," + (y + h) + " " + x0 + ","
                    + (y + h) + " " + x0 + "," + y1;
            // String clr= getDarkColor();
            /*
             * try { clr=
             * FormatConstants.SVGCOLORSTRINGS[seriescolors[i]]; }
             * catch(ArrayIndexOutOfBoundsException e) {; }
             */
            svg.append("<polyline  id='series_" + (i + 1) + "' " + getScript("")
                    + " fill='" + seriescolors[i] + "' fill-opacity='1' "
                    + getStrokeSVG() + " points='" + points + pointsend
                    + "' fill-rule='evenodd'/>\r\n");

            // Now print data labels, if any
            if (labels != null) {
                svg.append(labels);
            }
            svg.append("</g>\r\n");
        }
        return svg.toString();
    }

    /**
     * gets the chart-type specific ooxml representation: <areaChart>
     *
     * @return
     */
    @Override
    public StringBuffer getOOXML(String catAxisId, String valAxisId, String serAxisId) {
        final StringBuffer cooxml = new StringBuffer();

        // chart type: contains chart options and series data
        cooxml.append("<c:areaChart>");
        cooxml.append("\r\n");
        cooxml.append("<c:grouping val=\"");

        if (this.is100PercentStacked()) {
            cooxml.append("percentStacked");
        } else if (this.isStacked()) {
            cooxml.append("stacked");
            // } else if (this.isClustered())
            // grouping="clustered";
        } else {
            cooxml.append("standard");
        }
        cooxml.append("\"/>");
        cooxml.append("\r\n");
        // vary colors???

        // *** Series Data: ser, cat, val for most chart types
        cooxml.append(this.getParentChart().getChartSeries()
                .getOOXML(this.getChartType(), false, 0));

        // TODO: FINISH
        // chart data labels, if any
        // cooxml.append(getDataLabelsOOXML(cf));
        if (this.cf.getChartLinesRec() != null) {
            cooxml.append(this.cf.getChartLinesRec().getOOXML());
        }

        // axis ids - unsigned int strings
        cooxml.append("<c:axId val=\"" + catAxisId + "\"/>");
        cooxml.append("\r\n");
        cooxml.append("<c:axId val=\"" + valAxisId + "\"/>");
        cooxml.append("\r\n");

        cooxml.append("</c:areaChart>");
        cooxml.append("\r\n");

        return cooxml;
    }

}
