/**
 *
 */
package com.valkyrlabs.OpenXLS.util;

import org.slf4j.LoggerFactory;


/**
 * the usual logging stuff
 *
 * @author John McMahon Copyright 2013 ValkyrLabs Inc., all rights reserved.
 *
 */
public class Logger {

    public static final org.slf4j.Logger LOG = LoggerFactory
            .getLogger(Logger.class);


    /**
     * start with the basics
     *
     * @param message
     */
    public static void log(String message) {
        LOG.info(message);
    }

    public static void debug(String string) {
        LOG.debug(string);
    }

    public static void error(String string) {
        LOG.error(string);
    }

    public static void warn(String string) {
        LOG.warn(string);

    }

    public static void error(Exception e) {
        error(e.getMessage());
        e.printStackTrace();
    }

}
