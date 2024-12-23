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
package com.valkyrlabs.formats.XLS.charts;

/**
 * Constants required for Chart functionality
 */

public interface ChartConstants {
    // Chart Types (used in Chart Creation)
    int COLCHART = 0;
    int BARCHART = 1;
    int LINECHART = 2;
    int PIECHART = 3;
    int AREACHART = 4; // 20070703 KSC:
    int SCATTERCHART = 5; // ""
    int RADARCHART = 6; // ""
    int SURFACECHART = 7; // ""
    int DOUGHNUTCHART = 8; // ""
    int BUBBLECHART = 9; // ""
    int OFPIECHART = 10;
    int PYRAMIDCHART = 11;    // column-type pyramid
    int CYLINDERCHART = 12;    // column-type cylinder
    int CONECHART = 13;        // column-type cone
    int PYRAMIDBARCHART = 14;// bar-type pyramid
    int CYLINDERBARCHART = 15;    // bar-type cylinder
    int CONEBARCHART = 16;    // bar-type cone
    int RADARAREACHART = 17; // ""
    int STOCKCHART = 18;

    // Bar Shapes
    int SHAPEDEFAULT = 0;
    int SHAPECOLUMN = SHAPEDEFAULT;        // default
    int SHAPECYLINDER = 1;
    int SHAPEPYRAMID = 256;
    int SHAPECONE = 257;
    int SHAPEPYRAMIDTOMAX = 516;
    int SHAPECONETOMAX = 517;

    // Axis Types
    int XAXIS = 0;
    int YAXIS = 1;
    int ZAXIS = 2;
    int XVALAXIS = 3;    // an X axis type but VAL records
}