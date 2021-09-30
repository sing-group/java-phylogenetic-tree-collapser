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
