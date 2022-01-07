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

import static java.util.Arrays.asList;

import java.util.List;

import es.uvigo.ei.sing.yacli.CLIApplication;
import es.uvigo.ei.sing.yacli.command.Command;

public class TreeCollapserCli extends CLIApplication {

  @Override
  protected List<Command> buildCommands() {
    return asList(new CollapseTreeCommand());
  }

  @Override
  protected String getApplicationName() {
    return "Phylogenetic Tree Collapser";
  }

  @Override
  protected String getApplicationVersion() {
    return "0.0.1-SNAPSHOT";
  }

  @Override
  protected String getApplicationCommand() {
    return "ptc";
  }

  public static void main(String[] args) {
    new TreeCollapserCli().run(args);
  }
}
