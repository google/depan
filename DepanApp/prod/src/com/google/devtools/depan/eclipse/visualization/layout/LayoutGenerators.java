/*
 * Copyright 2013 ServiceNow.
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
package com.google.devtools.depan.eclipse.visualization.layout;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @author <a href='mailto:lee.carver@servicenow.com'>Lee Carver</a>
 */
// TODO: Make into an Eclipse/OGSi extension point
public class LayoutGenerators {
  private LayoutGenerators() {
    // Prevent instantiation.
  }

  private static class NamedLayout {
    public final String name;
    public final LayoutGenerator layout;

    public NamedLayout(String name, LayoutGenerator layout) {
      this.name = name;
      this.layout = layout;
    }
  }

  private static List<NamedLayout> layoutRegistry = Lists.newArrayList();

  private static Map<String, LayoutGenerator> layoutLookup =
      Maps.newHashMap();

  static {
    layoutRegistry.add(new NamedLayout("FRLayout",
        JungLayoutGenerator.FRLayoutBuilder));
    layoutRegistry.add(new NamedLayout("FR2Layout",
        JungLayoutGenerator.FR2LayoutBuilder));
    layoutRegistry.add(new NamedLayout("ISOLayout",
        JungLayoutGenerator.ISOMLayoutBuilder));
    layoutRegistry.add(new NamedLayout("KKLayout",
        JungLayoutGenerator.KKLayoutBuilder));
    layoutRegistry.add(new NamedLayout("SpringLayout",
        JungLayoutGenerator.SpringLayoutBuilder));
    layoutRegistry.add(new NamedLayout("Spring2Layout",
        JungLayoutGenerator.Spring2LayoutBuilder));

    // DROPPED: Old adapted-Jung tree layout strategies
    // TODO: Re-add with new LayoutRunner model.

    layoutRegistry.add(new NamedLayout("NewTreeLayout",
        TreeLayoutGenerator.NewTreeLayoutBuilder));
    layoutRegistry.add(new NamedLayout("NewRadialLayout",
        TreeLayoutGenerator.NewRadialLayoutBuilder));

    for (NamedLayout info : layoutRegistry) {
      layoutLookup.put(info.name, info.layout);
    }
  }

  public static List<String> getLayoutNames() {
    List<String> result =
            Lists.newArrayListWithExpectedSize(layoutRegistry.size());
    for (NamedLayout info : layoutRegistry) {
      result.add(info.name);
    }
    return result;
  }

  public static int getLayoutIndex(String name) {
    for (int index = 0; index < layoutRegistry.size(); index++) {
      if (layoutRegistry.get(index).name.equals(name))
        return index;
    }
    return -1;
  }

  public static LayoutGenerator getByName(String name) {
    return layoutLookup.get(name);
  }
}
