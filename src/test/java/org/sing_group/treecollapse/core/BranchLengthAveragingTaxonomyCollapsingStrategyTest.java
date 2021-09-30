package org.sing_group.treecollapse.core;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sing_group.treecollapse.core.newick.NewickTree;
import org.sing_group.treecollapse.core.tree.MutableTreeNode;

public class BranchLengthAveragingTaxonomyCollapsingStrategyTest {

  @Test
  public void mergeTwoNodesTest() {
    testCollapse("(s1.1:4.0,(s2.1:2.0,s3.1:1.0,s5.1:1.0):4.0,s4.1:2.0)root;", "(s1.1:4.0,(s5.1:1.0,T1.2_(2):1.5):4.0,s4.1:2.0)root;");
  }

  @Test
  public void mergeTwoNodesAndCollapseTest() {
    testCollapse("(s1.1:4.0,(s2.1:2.0,s3.1:1.0):4.0,s4.1:2.0)root;", "(s1.1:4.0,s4.1:2.0,T1.2_(2):5.5)root;");
  }


  private void testCollapse(String newickTreeString, String expectedCollapsedNewickTreeString) {
    String collapsedNewickTreeString = collapse(newickTreeString);

    Assert.assertEquals(expectedCollapsedNewickTreeString, collapsedNewickTreeString);
  }
  
  private String collapse(String newickTreeString) {
    NewickTree newickTree = new NewickTree(newickTreeString);

    TaxonomyCollapseTreeTest test = new TaxonomyCollapseTreeTest();
    MutableTreeNode root = newickTree.getRoot();
    
    Map<String, String> sequenceToSpecieMapping = test.getSequenceToSpecieMapping(root);
    
    MutableTreeNode collapsed =
      new TreeCollapser().collapseTree(
        root,
        new BranchLengthAveragingTaxonomyCollapsingStrategy(
          sequenceToSpecieMapping, test.taxonomy(), new HashSet<String>(asList("T1.2", "T2"))
        )
      );
    
    String collapsedNewickTreeString = new NewickTree(collapsed).toString();
    return collapsedNewickTreeString;
  }
}
