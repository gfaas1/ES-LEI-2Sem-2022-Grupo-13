package org.jgrapht.ext;

/**
 * Used to show problems with importing a graph.
 */
public class ImportException extends Exception {

   public ImportException(String message) {
      super(message);
   }

   public ImportException(String message, Throwable cause) {
      super(message, cause);
   }
}
