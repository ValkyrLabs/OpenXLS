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
package com.valkyrlabs.OpenXLS;

/**
 * WorkBookInstantiationException is thrown when a workbook cannot be parsed for
 * a particular reason.
 * 
 * Error codes can be retrieved with getErrorCode, which map to the static error
 * ints
 */
public class OpenXLSWorkBookException extends com.valkyrlabs.OpenXLS.WorkBookException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5313787084750169461L;
    public static final int DOUBLE_STREAM_FILE = 0;
    public static final int NOT_BIFF8_FILE = 1;
    public static final int LICENSING_FAILED = 2;
    public static final int UNSPECIFIED_INIT_ERROR = 3;
    public static final int RUNTIME_ERROR = 4;
    public static final int SMALLBLOCK_FILE = 5;
    public static final int WRITING_ERROR = 6;
    public static final int DECRYPTION_ERROR = 7;
    public static final int DECRYPTION_INCORRECT_PASSWORD = 8;
    public static final int ENCRYPTION_ERROR = 9;
    public static final int DECRYPTION_INCORRECT_FORMAT = 10;
    public static final int ILLEGAL_INIT_ERROR = 11;
    public static final int READ_ONLY_EXCEPTION = 12;
    public static final int SHEETPROTECT_INCORRECT_PASSWORD = 13;

    public OpenXLSWorkBookException(String n, int x) {
        super(n, x);
    }

    public OpenXLSWorkBookException(String string, int x,
            Exception e) {
        super(string, x, e);
    }

}
