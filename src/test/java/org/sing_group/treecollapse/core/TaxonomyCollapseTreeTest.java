package org.sing_group.treecollapse.core;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.sing_group.treecollapse.core.TreeBuilder.root;
import static org.sing_group.treecollapse.core.TreeMatcher.equalTo;
import static org.sing_group.treecollapse.core.TreeNodeBuilder.node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.sing_group.treecollapse.core.exception.TaxonomyCollapsingException;
import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TaxonomyCollapseTreeTest {

  public static TreeNode taxonomy() {
    return root("root").withChildren(
      asList(
        node("T1").withChildren(
          asList(
            node("T1.1").withChildren(
              asList(
                node("s1"),
                node("s6")

              )
            ),
            node("T1.2").withChildren(
              asList(
                node("s2"),
                node("s3")
              )
            )
          )
        ),
        node("T2").withChildren(
          asList(
            node("s4"),
            node("s5")
          )
        )
      )
    ).build();
  }

  public static TreeNode flatTaxonomy() {
    return root("root").withChildren(
      asList(
        node("G1").withChildren(
          asList(
            node("s1")

          )
        ),
        node("G2").withChildren(
          asList(
            node("s2"),
            node("s3"),
            node("s4")
          )
        )

      )
    ).build();
  }

  private TreeNode simpleTree() {
    return root("root").withChildren(
      asList(
        node("s1.1"),
        node().withChildren(
          asList(
            node("s2.1"),
            node("s3.1")
          )
        ),
        node("s4.1")
      )
    ).build();
  }

  private TreeNode collapsedSimpleTree() {
    return root("root").withChildren(
      asList(
        node("s1.1"),
        node("s4.1"),
        node("T1.2_(2)")
      )
    ).build();
  }

  private TreeNode collapsedSimpleTreeWithFlatTaxonomy() {
    return root("root").withChildren(
      asList(
        node("s1.1"),
        node("G2_(3)")
      )
    ).build();
  }

  private TreeNode simpleTree2() {
    return root("root").withChildren(
      asList(
        node("s1.1"),
        node().withChildren(
          asList(
            node("s2.1"),
            node("s3.1")
          )
        ),
        node("s6.1")
      )
    ).build();
  }

  private TreeNode collapsedSimpleTree2() {
    return root("root").withChildren(
      asList(
        node("T1.1_(2)"),
        node("T1.2_(2)")
      )
    ).build();
  }

  private TreeNode twoSubtrees() {
    return root("root").withChildren(
      asList(
        node().withChildren(
          asList(
            node("s1.2"),
            node("s3.1")
          )
        ),
        node().withChildren(
          asList(
            node("s2.1"),
            node("s6.1")
          )
        )
      )
    ).build();
  }

  private TreeNode collapsedTwoSubtrees() {
    return root("T1_(4)").build();
  }

  private TreeNode simpleTreeWithRepeatedSpecies() {
    return root("root").withChildren(
      asList(
        node().withChildren(
          asList(
            node("s1.2"),
            node("s3.1")
          )
        ),
        node().withChildren(
          asList(
            node("s1.1"),
            node("s2.1")
          )
        )
      )
    ).build();
  }

  private TreeNode collapsedSimpleTreeWithRepeatedSpecies() {
    return root("root").withChildren(
      asList(
        node("T1_(2)"),
        node("T1_(2)")
      )
    ).build();
  }

  @Test
  public void testSimpleCollapse() {
    baseTest(
      simpleTree(), collapsedSimpleTree(), taxonomy(), new HashSet<String>(asList("T1.2", "T2")),
      getSequenceToSpecieMapping(simpleTree())
    );
  }

  @Test
  public void testSimpleCollapse2() {
    baseTest(
      simpleTree2(), collapsedSimpleTree2(), taxonomy(), new HashSet<String>(asList("T1.2", "T2")),
      getSequenceToSpecieMapping(simpleTree2())
    );
  }

  @Test
  public void testSimpleCollapseWithFlatTaxonomy() {
    baseTest(
      simpleTree(), collapsedSimpleTreeWithFlatTaxonomy(), flatTaxonomy(), new HashSet<String>(asList("G1", "G2")),
      getSequenceToSpecieMapping(simpleTree())
    );
  }

  @Test
  public void testTwoSubtrees() {
    baseTest(
      twoSubtrees(), collapsedTwoSubtrees(), taxonomy(), new HashSet<String>(asList("T1")),
      getSequenceToSpecieMapping(twoSubtrees())
    );
  }

  @Test
  public void testSimpleWithRepeatedSpecies() {
    baseTest(
      simpleTreeWithRepeatedSpecies(), collapsedSimpleTreeWithRepeatedSpecies(), taxonomy(),
      new HashSet<String>(asList("T1")), getSequenceToSpecieMapping(simpleTreeWithRepeatedSpecies())
    );
  }

  private void baseTest(
    TreeNode tree, TreeNode expectedCollapsed, TreeNode taxonomy, Set<String> taxonomyStopTerms,
    Map<String, String> sequenceToSpecieMapping
  ) {
    System.out.println("BEFORE");
    tree.dumpTree(System.out);
    MutableTreeNode collapsed =
      new TreeCollapser().collapseTree(
        tree, new TaxonomyCollapsingStrategy(sequenceToSpecieMapping, taxonomy, taxonomyStopTerms)
      );

    System.out.println("AFTER");
    collapsed.dumpTree(System.out);

    assertThat(collapsed, is(equalTo(expectedCollapsed)));
  }

  @Test(expected = TaxonomyCollapsingException.class)
  public void testSequenceWithoutMapping() {
    Map<String, String> sequenceToSpecieMapping = getSequenceToSpecieMapping(simpleTree());
    sequenceToSpecieMapping.remove("s2.1");

    baseTest(
      simpleTree(), collapsedSimpleTree(), taxonomy(), new HashSet<String>(asList("T1.2", "T2")),
      sequenceToSpecieMapping
    );
  }

  public static Map<String, String> getSequenceToSpecieMapping(TreeNode root) {
    Map<String, String> sequenceToSpecieMapping = new HashMap<String, String>();

    for (String leafNodeName : getTreeLeafNodeNames(root)) {
      sequenceToSpecieMapping.put(leafNodeName, leafNodeName.split("\\.")[0]);
    }
    return sequenceToSpecieMapping;
  }

  private static List<String> getTreeLeafNodeNames(TreeNode root) {
    List<String> leafNodeNames = new LinkedList<>();

    if (root.isLeaf()) {
      return Arrays.asList(root.getName());
    } else {
      for (TreeNode child : root.getChildren()) {
        leafNodeNames.addAll(getTreeLeafNodeNames(child));
      }
      return leafNodeNames;
    }
  }
}
