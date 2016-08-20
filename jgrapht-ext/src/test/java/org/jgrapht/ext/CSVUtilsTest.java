/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
/* ------------------------------
 * CSVUtilsTest.java
 * ------------------------------
 * (C) Copyright 2016, by Dimitrios Michail and Contributors.
 *
 * Original Author:  Dimitrios Michail
 * Contributors: -
 *
 * Changes
 * -------
 * 20-Aug-2016 : Initial revision (DM);
 *
 */
package org.jgrapht.ext;

import junit.framework.TestCase;

/**
 * .
 * 
 * @author Dimitrios Michail
 */
public class CSVUtilsTest
    extends TestCase
{

    public void testEscape()
    {
        String input1 = "nothing special in here";
        assertEquals(input1, CSVUtils.escapeCSV(input1, ';'));
        assertEquals(input1, CSVUtils.escapeCSV(input1, ','));

        String input2 = "foo;;;";
        assertEquals(input2, CSVUtils.escapeCSV(input2, ','));
        assertEquals("\"foo;;;\"", CSVUtils.escapeCSV(input2, ';'));

        String input3 = "foo\n";
        assertEquals("\"foo\n\"", CSVUtils.escapeCSV(input3, ';'));

        String input4 = "foo\rfoo";
        assertEquals("\"foo\rfoo\"", CSVUtils.escapeCSV(input4, ';'));

        String input5 = "\"foo\"\n\"foo\"";
        assertEquals(
            "\"\"\"foo\"\"\n\"\"foo\"\"\"",
            CSVUtils.escapeCSV(input5, ';'));
    }

    public void testUnescape()
    {
        String input1 = "nothing special in here";
        assertEquals(input1, CSVUtils.unescapeCSV(input1, ';'));
        assertEquals(input1, CSVUtils.unescapeCSV(input1, ','));

        String input2 = "\"foo;;;\"";
        assertEquals("foo;;;", CSVUtils.unescapeCSV(input2, ';'));
        assertEquals("\"foo;;;\"", CSVUtils.unescapeCSV(input2, ','));

        String input3 = "\"foo\n\"";
        assertEquals("foo\n", CSVUtils.unescapeCSV(input3, ';'));

        String input4 = "\"foo\rfoo\"";
        assertEquals("foo\rfoo", CSVUtils.unescapeCSV(input4, ';'));

        String input5 = "\"\"\"foo\"\"\n\"\"foo\"\"\"";
        assertEquals("\"foo\"\n\"foo\"", CSVUtils.unescapeCSV(input5, ';'));
    }

}

// End CSVUtilsTest.java
