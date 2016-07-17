/*
 * Copyright 2007 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.depan.java.bytecode.eclipse;

import com.google.devtools.depan.filesystem.graph.FileSystemElement;
import com.google.devtools.depan.java.graph.FieldElement;
import com.google.devtools.depan.java.graph.InterfaceElement;
import com.google.devtools.depan.java.graph.JavaElementDispatcher;
import com.google.devtools.depan.java.graph.MethodElement;
import com.google.devtools.depan.java.graph.PackageElement;
import com.google.devtools.depan.java.graph.TypeElement;
import com.google.devtools.depan.model.Element;
import com.google.devtools.depan.model.ElementTransformer;
import com.google.devtools.depan.model.GraphNode;
import com.google.devtools.depan.model.builder.chain.ElementFilter;

import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * Default implementation for the ElementFilter interface. Filter only
 * TypeElement, InterfaceElement and PackageElement, based on their fully
 * qualified name: a simple startWith-test make the job.
 *
 * Others elements automatically pass the filter.
 *
 * A base package name is provided to the constructor. Test are made upon this
 * package name. E.g. with the package com.google.devtools, all the TypeElement,
 * InterfaceElement and PackageElement starting with com.google.devtools pass
 * the filter: com.google.devtools.depan,
 * com.google.devtools.depan.model.DefaultElementFilter...
 *
 * @author ycoppel@google.com (Yohann Coppel)
 *
 */
public class DefaultElementFilter extends JavaElementDispatcher<Boolean>
    implements ElementTransformer<Boolean>, ElementFilter {

  /**
   * Whitelist of package name for used to filter TypeElement, InterfaceElement
   * and PackageElement. Only objects in or under the whitelist package names
   * pass this filter
   */
  private final Collection<String> packageWhitelist;

  /**
   * Construct a new DefaultElementFilter based on the given whitelist of
   * package names. Inclusion in the filter is determined by whether package
   * names for Java elements startswith() any whitelisted name.
   * <p>
   * In order to select all elements, the whitelist should contain an entry that
   * is just an empty string. If used, this should be the only entry in the
   * whitelist.
   *
   * @param packageWhitelist white list of package names to use for selecting
   * Java elements for addition to the dependency graphs.
   */
  public DefaultElementFilter(Collection<String> packageWhitelist) {
    this.packageWhitelist = packageWhitelist;
  }

  /**
   * Determine if the named resource is included in any of the whitelisted
   * packages.
   *
   * @param resourceName containing package name for Java element to check
   * @return true if package name is the beginning of any whitelisted package
   */
  private boolean passPackageNameFilter(String resourceName) {
    for (String context : packageWhitelist) {
      if (resourceName.startsWith(context)) {
        return true;
      }
    }
    // resourceName is not on any whitelisted package
    return false;
  }

  @Override
  public Boolean match(TypeElement e) {
    return passPackageNameFilter(e.getFullyQualifiedName());
  }

  @Override
  public Boolean match(MethodElement e) {
    return match(e.getClassElement());
  }

  @Override
  public Boolean match(FieldElement e) {
    return match(e.getContainerClass()) && match(e.getType());
  }

  @Override
  public Boolean match(InterfaceElement e) {
    return passPackageNameFilter(e.getFullyQualifiedName());
  }

  @Override
  public Boolean match(PackageElement e) {
    return passPackageNameFilter(e.getPackageName());
  }

  @Override
  public boolean passFilter(GraphNode node) {
    return transform(node);
  }

  @Override
  public Boolean transform(Element element) {
    if (element instanceof FileSystemElement) {
      return true;
    }
    return match(element);
  }

  /**
   * For now, split a filter input line into a filter whitelist.  In the future
   * a better UI would be appropriate.
   *
   * Split the input line on spaces, and build up the whitelist from the split()
   * results.  If the generated whitelist is empty, add on empty string to the
   * whitelist so that it matches all packages or directories.
   *
   * @param formFilter user input with possibly multiple patterns
   *     for a whitelist
   * @return Collection of Strings suitable for a whitelist.
   */
  private static Collection<String> splitFilter(String formFilter) {
    Collection<String> result = Lists.newArrayList();
    for (String filter : formFilter.split("\\p{Space}+")) {
      if ((filter != null) && (!filter.isEmpty())) {
        result.add(filter);
      }
    }

    // If the constructed filter wound up empty, make it accept everything
    if (result.size() <= 0) {
      result.add("");
    }
    return result;
  }

  public static DefaultElementFilter build(String packageFilter) {
    Collection<String> packageWhitelist = splitFilter(packageFilter);
    return new DefaultElementFilter(packageWhitelist);
    
  }
}
