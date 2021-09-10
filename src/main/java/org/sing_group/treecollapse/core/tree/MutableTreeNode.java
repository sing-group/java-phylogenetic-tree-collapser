package org.sing_group.treecollapse.core.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MutableTreeNode implements TreeNode {

  private String name = null;
  private List<MutableTreeNode> children = new LinkedList<>();
  private Map<String, Object> attributes = new HashMap<>();
  
  private MutableTreeNode parent = null;
  
  public MutableTreeNode(TreeNode original) {
    this.name = original.getName();
    original.getChildren().forEach((child) -> {
      MutableTreeNode mChild = new MutableTreeNode(child); 
      this.children.add(mChild);
      mChild.parent = this;
      
    });
  }
  
  public MutableTreeNode(String name) {
    this.name = name;
  }  
  
  public void addChild(MutableTreeNode node) {
    this.children.add(node);
    node.parent = this;
  }
  
  public void removeChild(MutableTreeNode node) {
    this.children.remove(node);
    node.parent = null;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  @Override
  public List<MutableTreeNode> getChildren() {
    return this.children;
  }

  @Override
  public String getName() {
    return this.name;
  }
  
  public void setAttribute(String key, Object value) {
    this.attributes.put(key, value);
  }
  
  public void removeAttribute(String key) {
    this.attributes.remove(key);
  }
  
  public Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(this.attributes);
  }
  
  @SuppressWarnings("unchecked")
  public <T> T getAttribute(String key) {
    return (T) this.getAttributes().get(key);
  }
  
  public MutableTreeNode getParent() {
    return parent;
  }

  @Override
  public String toString() {
    return this.getName() + " " + this.getAttributes();
  }

  
}
