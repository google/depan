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
package com.google.devtools.depan.pushxml;

import com.google.common.collect.Lists;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Simplify SAX document parsing by using a push-down stack of element
 * handlers.  Some utility classes within this type provide simple support
 * for common XML scenarios.
 * 
 * @author <a href="leeca@pnambic.com">Lee Carver</a>
 */
public class PushDownXmlHandler extends DefaultHandler {
  
  public static interface ElementHandler {

    boolean isFor(String name);

    void processText(String text);

    ElementHandler newChild(String name);

    void processAttributes(Attributes attributes);

    void end();
  }

  public static class DefaultElementHandler implements ElementHandler {
    private final String elementName;

    public DefaultElementHandler(String elementName) {
      this.elementName = elementName;
    }

    @Override
    public boolean isFor(String name) {
      return elementName.equals(name);
    }

    @Override
    public ElementHandler newChild(String name) {
      return new DefaultElementHandler(name);
    }

    @Override
    public void processText(String text) {
      // nothing to do
    }

    @Override
    public void processAttributes(Attributes attributes) {
      // nothing to do
    }

    @Override
    public void end() {
      // nothing to do
    }
  }

  public static abstract class NestingElementHandler
      implements ElementHandler {

    @Override
    public ElementHandler newChild(String name) {
      return new DefaultElementHandler(name);
    }

    @Override
    public void processText(String text) {
      // nothing to do
    }

    @Override
    public void processAttributes(Attributes attributes) {
      // Override if the container has important attributes
    }

    @Override
    public void end() {
      // nothing to do
    }
  }

  public static abstract class TerminalElementHandler
      implements ElementHandler {

    @Override
    public ElementHandler newChild(String name) {
      throw new IllegalArgumentException("Unexpected child element " + name);
    }

    @Override
    public void processText(String text) {
      // nothing to do
    }

    @Override
    public void end() {
      // nothing to do
    }
  }

  public static class TextElementHandler extends TerminalElementHandler {
    private final String name;
    private final StringBuilder builder = new StringBuilder();

    public TextElementHandler(String name) {
      super();
      this.name = name;
    }

    @Override
    public boolean isFor(String name) {
      return this.name.equals(name);
    }

    @Override
    public void processText(String text) {
      builder.append(text);
    }

    @Override
    public void processAttributes(Attributes attributes) {
      // typically ignore attributes if data is in text.
      // Derived types can override.
    }

    public String getText() {
      return builder.toString();
    }
  }

  /**
   * Do the right thing for documents.  Users of the {@link PushDownXmlHandler}
   * derive a integration class and need only implement the factory method
   * for the top-level document node.  An instance derived from this type
   * is used to initialize the {@code PushDownXmlHandler}.
   */
  public static abstract class DocumentHandler implements ElementHandler {

    /**
     * Provide an {@link ElementHandler} for the top-level document node.
     * 
     * If the document name is unrecognized, overriding methods should return
     * {@code null} rather then throw an {@code Exception} directly.
     */
    protected abstract ElementHandler newDocumentElement(String name);

    @Override
    public final ElementHandler newChild(String name) {

      ElementHandler result = newDocumentElement(name);
      if (null == result) {
        throw new IllegalArgumentException("Unknown document element " + name);
      }
      return result;
    }

    @Override
    public final boolean isFor(String name) {
      throw new UnsupportedOperationException(
          "A document element does not have a name");
    }

    @Override
    public void processText(String text) {
      throw new UnsupportedOperationException(
          "Document element should never process text");
    }

    @Override
    public void processAttributes(Attributes attributes) {
      throw new UnsupportedOperationException(
          "Document element should never process attributes");
    }

    @Override
    public final void end() {
      throw new UnsupportedOperationException(
          "Attempt to exit document as an element");
    }
  }

  /////////////////////////////////////
  // Stack of element handlers

  private List<ElementHandler> items = Lists.newArrayList();

  private void push(ElementHandler processor) {
    items.add(processor);
  }

  private void pop() {
    if (items.isEmpty()) {
      throw new IllegalStateException(
          "Attempt to pop from empty XML parsing stack");
    }

    items.remove(items.size() - 1);
  }

  private ElementHandler top() {
    if (items.isEmpty()) {
      throw new IllegalStateException(
          "Attempt to read top from empty XML parsing stack");
    }

    return items.get(items.size() - 1);
  }

  /////////////////////////////////////
  // SAX ContentHandler API

  @Override // from sax..DefaultHandler
  public void startElement(
      String uri, String localName, String qName, Attributes attributes)
      throws SAXException {

    ElementHandler element =  top().newChild(qName);
    element.processAttributes(attributes);
    push(element);

    super.startElement(uri, localName, qName, attributes);
  }

  @Override // from sax..DefaultHandler
  public void characters(char[] ch, int start, int length)
      throws SAXException {
    top().processText(new String(ch, start, length));
  }

  @Override // from sax..DefaultHandler
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    ElementHandler element =  top();
    if (element.isFor(qName)) {
      element.end();
    } else {
      throw new IllegalStateException(
          "Unexpected end for element " + localName);
    }

    pop();
    super.endElement(uri, localName, qName);
  }

  /////////////////////////////////////
  // Parsing and other XML helper methods

  /**
   * All nodes and dependencies from the supplied {@code docSource} are
   * installed in the resulting graphs via the {@code builder}.
   */
  public static void parseDocument(
      DocumentHandler docLoader, InputSource docSource)
      throws ParserConfigurationException, SAXException, IOException {
    PushDownXmlHandler loader = new PushDownXmlHandler(docLoader);
    loader.parseDocument(docSource);
  }

  /**
   * Provide an XML {@link InputSource} for the supplied {@code docFile}.
   * 
   * The result should be non-{@code null}, with the public id for the
   * new instance set as the supplied {@code docFile}s path.
   */
  public static InputSource getInputSource(File docFile) throws IOException {
    FileInputStream stream = new FileInputStream(docFile);
    InputSource result = new InputSource(stream);
    result.setPublicId(docFile.getPath());
    return result;
  }

  /////////////////////////////////////
  // Public API

  public PushDownXmlHandler(DocumentHandler docElement) {
    push(docElement);
  }

  public void parseDocument(InputStream source)
      throws ParserConfigurationException, SAXException, IOException {

    buildParser().parse(source, this);
  }

  public void parseDocument(InputSource source)
      throws ParserConfigurationException, SAXException, IOException {

    buildParser().parse(source, this);
  }

  private SAXParser buildParser() throws ParserConfigurationException, SAXException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    return factory.newSAXParser();
  }
}
