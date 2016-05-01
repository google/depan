package com.google.devtools.depan.graph_doc.model;

/**
 * Placeholder for name of dependency analysis.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class DependencyModel {
  private final String analyzer;

  public DependencyModel(String analyzer) {
    this.analyzer = analyzer;
  }

  public String getAnalyzer() {
    return analyzer;
  }
}
