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

import com.valkyrlabs.formats.XLS.XLSRecord;
import com.valkyrlabs.toolkit.ByteTools;

/**
 * <b>ObjectLink: Attaches Text to Chart or to Chart Item (0x1027)</b>
 * <p>
 * 4	wLinkObj	2		Object text is linked to (1= chart title, 2= Veritcal (y) axis title, 3= Category (x) axis title, 4= data series points, 7=Series Axis 12= Display Units
 * 6	wLinkVar1	2		0-based series number	(only if wLinkObj=4,  otherwise 0)
 * 8	wLinkVar2	2		0-based category number within the series specified by wLinkVar1.  (only if wLinkObj=4,  otherwise 0).  If attached to entire series rather
 * than a single data point, = 0xFFFF.
 */
public class ObjectLink extends GenericChartObject implements ChartObject {
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_XAXIS = 3;
    public static final int TYPE_YAXIS = 2;
    public static final int TYPE_DATAPOINTS = 4;
    public static final int TYPE_ZAXIS = 7;
    /**
     * An axis-formatting option that determines how numeric units are displayed on a value axis.
     */
    public static final int TYPE_DISPLAYUNITS = 0xC;
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -301929936750246017L;
    private final byte[] PROTOTYPE_BYTES = new byte[]{1, 0, 0, 0, 0, 0};
    short wLinkObj;

    public static XLSRecord getPrototype(int type) {
        ObjectLink o = new ObjectLink();
        o.setOpcode(OBJECTLINK);
        o.setData(o.PROTOTYPE_BYTES);
        o.setType(type);
        return o;
    }

    public void init() {
        super.init();
        byte[] rkdata = this.getData();
        wLinkObj = ByteTools.readShort(rkdata[0], rkdata[1]);
    }

    /**
     * Does this object link refer to the chart title?
     *
     * @return
     */
    boolean isChartTitle() {
        return wLinkObj == TYPE_TITLE;
    }

    /**
     * Does this object link refer to the XAxis Label?
     *
     * @return
     */
    boolean isXAxisLabel() {
        return wLinkObj == TYPE_XAXIS;
    }

    /**
     * Does this object link refer to the YAxis Label?
     *
     * @return
     */
    boolean isYAxisLabel() {
        return wLinkObj == TYPE_YAXIS;
    }

    public int getType() {
        return wLinkObj;
    }

    public void setType(int type) {
        wLinkObj = (short) type;
        this.getData()[0] = (byte) wLinkObj;
    }
}
