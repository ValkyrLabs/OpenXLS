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
package com.valkyrlabs.formats.XLS;

import com.valkyrlabs.OpenXLS.FormulaNotFoundException;
import com.valkyrlabs.OpenXLS.SheetNotFoundException;
import com.valkyrlabs.formats.XLS.formulas.Ptg;

/**
 * Ref defines an OpenXLS record or object that represents a range of cell locations.
 *
 * @see Ptg
 * @see Formula
 */
public interface Ref {

    int PTG_LOCATION_POLICY_UNLOCKED = 0;
    int PTG_LOCATION_POLICY_LOCKED = 1;
    int PTG_LOCATION_POLICY_TRACK = 2;


    /**
     * returns whether the Location of the Ptg is locked
     * used during automated BiffRec movement updates
     *
     * @return location policy
     */
    int getLocationPolicy();

    /**
     * lock the Location of the Ptg so that it will not
     * be updated during automated BiffRec movement updates
     *
     * @param b whether to lock the location of this Ptg
     */
    void setLocationPolicy(int b);

    /**
     * When the ptg is a reference to a location this returns that location
     *
     * @return String Location
     */
    String getLocation() throws FormulaNotFoundException;

    /**
     * setLocation moves a ptg that is a reference to a location, such as
     * a ptg range being modified
     *
     * @param String location, such as A1:D4
     */
    void setLocation(String s);

    int[] getIntLocation() throws FormulaNotFoundException;


    /**
     * returns the row/col ints for the ref
     *
     * @return the row col int array
     */
    int[] getRowCol();

    /**
     * returns the String address of this ptg including sheet reference
     *
     * @return the String location of the reference including sheetname
     */
    String getLocationWithSheet();

    /**
     * gets the sheetname for this ref
     *
     * @param sheetname
     */
    String getSheetName() throws SheetNotFoundException;


}