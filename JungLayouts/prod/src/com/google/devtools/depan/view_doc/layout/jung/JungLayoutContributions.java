/*
 * Copyright 2016 The Depan Project Authors
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

package com.google.devtools.depan.view_doc.layout.jung;

import com.google.devtools.depan.view_doc.layout.LayoutGenerator;
import com.google.devtools.depan.view_doc.layout.registry.LayoutGeneratorContributor;

/**
 * Define contributed layout generators based on JUNG algorithms.
 * These contributions are registered for the Layouts extension point.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class JungLayoutContributions {

  private JungLayoutContributions() {
    // Prevent instantiation.
  }

  public static class FRLayout implements LayoutGeneratorContributor {

    @Override
    public String getLabel() {
      return "FR Layout";
    }

    @Override
    public LayoutGenerator getLayoutGenerator() {
      return JungLayoutGenerator.FRLayoutBuilder;
    }
  }

  public static class FR2Layout implements LayoutGeneratorContributor {

    @Override
    public String getLabel() {
      return "FR2 Layout";
    }

    @Override
    public LayoutGenerator getLayoutGenerator() {
      return JungLayoutGenerator.FR2LayoutBuilder;
    }
  }

  public static class ISOMLayout implements LayoutGeneratorContributor {

    @Override
    public String getLabel() {
      return "Isometric Layout";
    }

    @Override
    public LayoutGenerator getLayoutGenerator() {
      return JungLayoutGenerator.ISOMLayoutBuilder;
    }
  }

  public static class KKLayout implements LayoutGeneratorContributor {

    @Override
    public String getLabel() {
      return "KK Layout";
    }

    @Override
    public LayoutGenerator getLayoutGenerator() {
      return JungLayoutGenerator.KKLayoutBuilder;
    }
  }

  public static class SpringLayout implements LayoutGeneratorContributor {

    @Override
    public String getLabel() {
      return "Spring Layout";
    }

    @Override
    public LayoutGenerator getLayoutGenerator() {
      return JungLayoutGenerator.SpringLayoutBuilder;
    }
  }

  public static class Spring2Layout implements LayoutGeneratorContributor {

    @Override
    public String getLabel() {
      return "Spring 2 Layout";
    }

    @Override
    public LayoutGenerator getLayoutGenerator() {
      return JungLayoutGenerator.Spring2LayoutBuilder;
    }
  }
}
