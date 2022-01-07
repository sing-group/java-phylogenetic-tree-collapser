/*-
 * #%L
 * treecollapse
 * %%
 * Copyright (C) 2021 - 2022 Daniel Glez-Peña, Hugo López-Fernández, Cristina Vieira, Jorge Vieira
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
