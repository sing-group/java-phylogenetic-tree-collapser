package org.sing_group.treecollapse.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.sing_group.treecollapse.core.newick.NewickTreeParserTest;
import org.sing_group.treecollapse.core.taxonomy.TaxonomyFileReaderTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
  BranchLengthAveragingTaxonomyCollapsingStrategyTest.class,
  TaxonomyCollapseTreeTest.class,
  NewickTreeParserTest.class,
  TaxonomyFileReaderTest.class
})
public class CoreTestSuite {

}
