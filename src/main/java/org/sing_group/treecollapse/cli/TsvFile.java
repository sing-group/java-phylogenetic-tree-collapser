package org.sing_group.treecollapse.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class TsvFile extends HashMap<String, String> {
  private static final long serialVersionUID = 1L;

  public TsvFile(String path) throws IOException {
    readFile(path);
  }

  private void readFile(String path) throws IOException {
    for (String line : Files.readAllLines(new File(path).toPath())) {
      String[] lineSplit = line.split("\t");
      if (lineSplit.length == 2) {
        this.put(lineSplit[0], lineSplit[1]);
      } else {
        throw new IOException("Invalid line (it mus thave two columns): " + line);
      }
    }
  }
}
