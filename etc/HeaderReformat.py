#Execute:
#find ../ -name *.java -not -path "*/generated-sources/*" -not -path "*/generated-test-sources/*" -exec python HeaderReformat.py {} \;

import re, mmap, sys, warnings

def _warning(
    message,
    category = UserWarning,
    filename = '',
    lineno = -1):
    print(message)
warnings.showwarning = _warning

filename = sys.argv[1]
authors="Barak Naveh"
year="2016"

with open(filename, 'r+') as f:
  data = mmap.mmap(f.fileno(), 0)
  
   
  #try parsing double header
  #p= re.compile('/\*(.*?)\*/\n/\*(.*?)\*/', re.DOTALL)
  p= re.compile('/\*(.*?)\*/\r?\n\n?/\*(.*?)\*/', re.DOTALL)
  p = p.search(data)
  
  if p:
    firstHeader=p.group(1)
    secondHeader=p.group(2)
        
    #check whether there's a copyright statement present
    p=re.compile('\(C\)')
    p=p.search(secondHeader)
    if p: #found copyright statement: extract authors and contributors
      #Extract Copyright information
      #p=re.compile('\(C\)\s*Copyright\s*(\d+)\-?(\d+)?,\s*(by)?\s+(.*?)(\sand\sContributors\.?)?')
      p=re.compile('\(C\)\s*Copyright\s*(\d+)\-?(\d+)?,\s*(by)?\s*(.*)?')
      p=p.search(secondHeader)
      if not p:
	raise Exception('Cannot parse copyright statement in 2nd header: '+filename+"\n"+secondHeader)
      year=p.group(1)
      authors=p.group(4)
      #strip off "and Contributors" and variations thereof
      p=re.compile('(.*)(\sand)')
      p=p.search(authors)
      if p:
	authors=p.group(1)
      authors=re.sub('[.]', '', authors)
      
    else:
      #Search for "Original author block"
      p=re.compile('Original\sAuthor:\s*(.*)')
      p=p.search(secondHeader)
      p2=re.compile('\@author\s*(Original)?(\:)?\s*(.*)')
      p2=p2.search(data)
      if p:
	authors=p.group(1)
      elif p2:
	authors=p2.group(3)
      else:
	warnings.warn('Cannot find original author in 2nd header: '+filename+"; using default author\n")
      
      #Search for "Initial version" to extract year April-2016: Initial version;
      p=re.compile('(.*)?([0-9]{4,4})(.*)?Initial')
      p=p.search(secondHeader)
      if p:
	year=p.group(2)
      else: #try searching for a year in the first header
	p=re.compile('\(C\).*?[0-9]{4,4}-([0-9]{4,4})')
	p=p.search(firstHeader)
	if not p:
	  warnings.warn('Cannot find Initial version in 2nd header: '+filename+"; using default year\n")
	else:
	  year=p.group(1)
  else:
    #try parsing single header:
    p= re.compile('/\*\s*This\sprogram(.*?)\*/', re.DOTALL)
    p = p.search(data)
    if p: #Parse single header
      firstHeader=p.group(1)
      
      #search for author in data
      p=re.compile('\@author\s*(Original)?(\:)?\s*(.*?)')
      p = p.search(data)
      if p:
	author=p.group(3)
      else:
	warnings.warn('Cannot find author in data: '+filename+"; using default author\n")
	
      #search for year in data
      p=re.compile('\@since(.*?)([0-9]{4,4})')
      p=p.search(data)
      if p:
	year=p.group(2)
      else:
	warnings.warn('Cannot find year in data: '+filename+"; using default year\n")
      
    else:
      warnings.warn('Did not find any headers: '+filename+"\n")

print("(C) Copyright "+year+"-2016, by "+authors+", and Contributors."+" "+filename)
    

    
'''    
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
/* ------------------
 * DirectedGraph.java
 * ------------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   Christian Hammer
 *
 * $Id$
 *
 * Changes
 * -------
 * 24-Jul-2003 : Initial revision (BN);
 * 11-Mar-2004 : Made generic (CH);
 * 07-May-2006 : Changed from List<Edge> to Set<Edge> (JVS);
 *
 */
 '''