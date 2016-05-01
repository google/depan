/**
 * Original version
 * Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * 
 * Stolen from Mr. Walker's:
 * https://github.com/WadeWalker/name.wadewalker.tutorial
 * name.wadewalker.tutorial.jogleditor.JOGLEditor.createPartControl();
 */
package com.google.devtools.depan.eclipse.visualization;

import com.jogamp.common.util.JarUtil;

import org.eclipse.core.runtime.FileLocator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 
 * Stolen form Copyright (c) 2010-2011 Wade Walker. Free for any use, but credit is appreciated.
 * @author Standard Lee
 *
 */
public class JoglLoader {

  public static void prepareJoglLoader() {
    JarUtil.setResolver( new JarUtil.Resolver() {

      @Override
      public URL resolve( URL url ) {
          try {
              URL urlTest = FileLocator.resolve( url );
              // HACK: required because FileLocator.resolve() doesn't return an
              // escaped URL, which makes conversion to a URI inside JOGL fail.
              // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=145096 for details.
              URI uriResolved = null;
              try {
                  uriResolved = new URI(urlTest.getProtocol(), urlTest.getPath(), null);
              }
              catch( URISyntaxException urisyntaxexception ) {
                  // should never happen, since FileLocator's URLs should at least be syntactically correct
                  urisyntaxexception.printStackTrace();
              }
              URL urlNew = uriResolved.toURL();
              return( urlNew ); 
          }
          catch( IOException ioexception ) {
              return( url );
          }
      }
    });
  }

}
