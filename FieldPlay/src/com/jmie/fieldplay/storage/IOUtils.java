package com.jmie.fieldplay.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	  private IOUtils() {
	    // Utility class.
	  }

	  public static int copy(InputStream input, OutputStream output) throws InterruptedException,
	      IOException {
	    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	    int count = 0;
	    int n = 0;
	    while (-1 != (n = input.read(buffer))) {
	      output.write(buffer, 0, n);
	      count += n;
	      if (Thread.interrupted()) {
	        throw new InterruptedException();
	      }
	    }
	    return count;
	  }

	}