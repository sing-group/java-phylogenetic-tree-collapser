package org.sing_group.treecollapse.cli;

import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.write;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sing_group.treecollapse.core.BranchLengthAveragingTaxonomyCollapsingStrategy;
import org.sing_group.treecollapse.core.TaxonomyCollapsingStrategy;
import org.sing_group.treecollapse.core.TreeCollapser;
import org.sing_group.treecollapse.core.newick.NewickCladogramTree;
import org.sing_group.treecollapse.core.newick.NewickTree;
import org.sing_group.treecollapse.core.taxonomy.TaxonomyFileReader;
import org.sing_group.treecollapse.core.tree.TreeNode;

import es.uvigo.ei.sing.yacli.command.AbstractCommand;
import es.uvigo.ei.sing.yacli.command.option.DefaultValuedStringOption;
import es.uvigo.ei.sing.yacli.command.option.Option;
import es.uvigo.ei.sing.yacli.command.option.StringOption;
import es.uvigo.ei.sing.yacli.command.parameter.Parameters;

public class CollapseTreeCommand extends AbstractCommand {

  private static final String OUTPUT_DESCRIPTION = "output Newick file";
  private static final String OUTPUT_SHORT_NAME = "o";
  private static final String OUTPUT_NAME = "output";
  private static final String OUTPUT_TYPE_PHYLOGRAM = "phylogram";
  private static final String OUTPUT_TYPE_CLADOGRAM = "cladogram";
  private static final String OUTPUT_TYPE_DEFAULT_VALUE = OUTPUT_TYPE_PHYLOGRAM;

  private static final String OUTPUT_TYPE_DESCRIPTION = "output format: phylogram or cladrogram";
  private static final String OUTPUT_TYPE_NAME = "output-type";
  private static final String OUTPUT_TYPE_SHORT_NAME = "OT";

  private static final String TAXONOMY_STOP_TERMS_DESCRIPTION = "taxonomy stop terms file";
  private static final String TAXONOMY_STOP_TERMS_SHORT_NAME = "tts";
  private static final String TAXONOMY_STOP_TERMS_NAME = "taxonomy-stop-terms";

  private static final String TAXONOMY_DESCRIPTION = "taxonomy file";
  private static final String TAXONOMY_SHORT_NAME = "t";
  private static final String TAXONOMY_NAME = "taxonomy";

  private static final String SEQUENCE_MAPPING_DESCRIPTION = "sequence to species mapping file";
  private static final String SEQUENCE_MAPPING_SHORT_NAME = "sm";
  private static final String SEQUENCE_MAPPING_NAME = "sequence-mapping";

  private static final String INPUT_DESCRIPTION = "input Newick file";
  private static final String INPUT_SHORT_NAME = "i";
  private static final String INPUT_NAME = "input";

  @Override
  public String getName() {
    return "collapse-tree";
  }

  @Override
  public String getDescriptiveName() {
    return "Collapses a phylogenetic tree.";
  }

  @Override
  public String getDescription() {
    return "Collapses a phylogenetic tree.";
  }

  @Override
  public void execute(Parameters parameters) throws Exception {
    String input = parameters.getSingleValueString(getOption(INPUT_NAME));
    NewickTree tree = new NewickTree(readAllLines(new File(input).toPath()).get(0));

    writeOutput(
      parameters,
      new NewickTree(new TreeCollapser().collapseTree(tree.getRoot(), getStrategy(parameters)))
    );
  }

  private TaxonomyCollapsingStrategy getStrategy(Parameters parameters) throws IOException {
    String sequenceToSpecieMappingFilePath = parameters.getSingleValueString(getOption(SEQUENCE_MAPPING_NAME));
    Map<String, String> sequenceToSpecieMapping = new TsvFile(sequenceToSpecieMappingFilePath);

    String taxonomyFilePath = parameters.getSingleValueString(getOption(TAXONOMY_NAME));
    TreeNode taxonomy = TaxonomyFileReader.read(new File(taxonomyFilePath));

    String taxonomyStopTermsFilePath = parameters.getSingleValueString(getOption(TAXONOMY_STOP_TERMS_NAME));
    Set<String> taxonomyStopTerms = fileToSet(taxonomyStopTermsFilePath);

    return new BranchLengthAveragingTaxonomyCollapsingStrategy(sequenceToSpecieMapping, taxonomy, taxonomyStopTerms);
  }

  private Set<String> fileToSet(String path) throws IOException {
    return readAllLines(new File(path).toPath()).stream().collect(toSet());
  }

  private void writeOutput(Parameters parameters, NewickTree collapsed) throws IOException {
    String output = parameters.getSingleValueString(getOption(OUTPUT_NAME));

    String outputType = parameters.getSingleValueString(getOption(OUTPUT_TYPE_NAME));
    String collapsedString = "";
    if (outputType.equals(OUTPUT_TYPE_DEFAULT_VALUE)) {
      collapsedString = collapsed.toString();
    } else if (outputType.equals(OUTPUT_TYPE_CLADOGRAM)) {
      collapsedString = new NewickCladogramTree(collapsed).toString();
    }

    write(new File(output).toPath(), collapsedString.getBytes());
  }

  @Override
  protected List<Option<?>> createOptions() {
    return asList(
      getInputNewickPath(),
      getInputSequenceToSpeciesMappingPath(),
      getTaxonomyPath(),
      getTaxonomyStopTermsPath(),
      getOutputType(),
      getOutputPath()
    );
  }

  private Option<?> getInputNewickPath() {
    return new StringOption(INPUT_NAME, INPUT_SHORT_NAME, INPUT_DESCRIPTION, false, true);
  }

  private Option<?> getInputSequenceToSpeciesMappingPath() {
    return new StringOption(
      SEQUENCE_MAPPING_NAME, SEQUENCE_MAPPING_SHORT_NAME, SEQUENCE_MAPPING_DESCRIPTION, false, true
    );
  }

  private Option<?> getTaxonomyPath() {
    return new StringOption(TAXONOMY_NAME, TAXONOMY_SHORT_NAME, TAXONOMY_DESCRIPTION, false, true);
  }

  private Option<?> getTaxonomyStopTermsPath() {
    return new StringOption(
      TAXONOMY_STOP_TERMS_NAME, TAXONOMY_STOP_TERMS_SHORT_NAME, TAXONOMY_STOP_TERMS_DESCRIPTION, false, true
    );
  }

  private Option<?> getOutputType() {
    return new DefaultValuedStringOption(
      OUTPUT_TYPE_NAME, OUTPUT_TYPE_SHORT_NAME, OUTPUT_TYPE_DESCRIPTION, OUTPUT_TYPE_DEFAULT_VALUE
    );
  }

  private Option<?> getOutputPath() {
    return new StringOption(OUTPUT_NAME, OUTPUT_SHORT_NAME, OUTPUT_DESCRIPTION, false, true);
  }
}
