package org.sing_group.treecollapse.core;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.sing_group.treecollapse.core.tree.MutableTreeNode;
import org.sing_group.treecollapse.core.tree.TreeNode;

public class TreeBuilder {
  public static TreeNodeBuilder root(String name) {
    return TreeNodeBuilder.node(name);
  }
}

class TreeNodeBuilder {
  private String name;
  private List<TreeNodeBuilder> children = new LinkedList<>();
  
  public TreeNodeBuilder withChildren(List<TreeNodeBuilder> children) {
    this.children = children;
    
    return this;
  }
  
  public TreeNode build() {
    return new MutableTreeNode(new TreeNode() {

      @Override
      public List<TreeNode> getChildren() {
        return children.stream().map((e)-> e.build()).collect(Collectors.toList());
      }

      @Override
      public String getName() {
        return name;
      }
      
    });
  }
  
  
  public static TreeNodeBuilder node(String name) {
    TreeNodeBuilder builder = new TreeNodeBuilder();
    builder.name = name;
    return builder;  
  }
  
  public static TreeNodeBuilder node() {
    TreeNodeBuilder builder = new TreeNodeBuilder();
    builder.name = null;
    return builder;  
  }
  
}
