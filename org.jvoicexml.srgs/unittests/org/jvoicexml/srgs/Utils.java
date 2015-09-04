package org.jvoicexml.srgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.jvoicexml.srgs.SrgsSisrGrammar;
import org.jvoicexml.srgs.SrgsSisrXmlGrammarParser;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.xml.sax.InputSource;

public class Utils {
    public static void InitLogger() {
        Logger rootLogger = Logger.getRootLogger();
        if (!rootLogger.getAllAppenders().hasMoreElements()) {
            rootLogger.setLevel(Level.DEBUG);
            rootLogger.addAppender(new ConsoleAppender(new PatternLayout(
                    "%-5p [%t] (%4L): %m%n")));
        }
    }

    public static SrgsSisrGrammar LoadDocument(String filename)
            throws Exception {
        Assert.assertNotNull("filename to load was null", filename);

        final InputStream input = new FileInputStream(filename);
        Assert.assertNotNull("unable to create input stream", input);

        final InputSource source = new InputSource(input);
        Assert.assertNotNull("unable to create input source", source);

        SrgsXmlDocument document = new SrgsXmlDocument(source);
        Assert.assertNotNull("unable to create document", document);

        final SrgsSisrXmlGrammarParser parser = new SrgsSisrXmlGrammarParser();

        File f = new File(filename);
        return parser.parse(document, new URI("file:///"
                + f.getCanonicalPath().replace('\\', '/')));
    }

    public static Object getItemOnNativeObject(Object o, String key) {
        Assert.assertTrue(o instanceof org.mozilla.javascript.NativeObject);

        return ((org.mozilla.javascript.NativeObject) o).get(key);
    }

    public static void dumpScope(final Scriptable scope) {
        dumpScope(scope, " ");
    }

    public static void dumpScope(final Scriptable scope, String pad) {
        java.lang.Object[] ids = scope.getIds();
        for (Object id : ids) {
            Object o = null;
            if (id instanceof String) {
                o = scope.get((String) id, scope);
                Logger.getRootLogger().debug(
                        pad + id + "=" + o + " ("
                                + o.getClass().getCanonicalName() + ")");
            } else {
                o = scope.get((int) id, scope);
                Logger.getRootLogger().debug(
                        pad + id + "=" + o + " ("
                                + o.getClass().getCanonicalName() + ")");
            }

            if (o instanceof Scriptable)
                dumpScope((Scriptable) o, pad + " ");
            else if (o instanceof NativeObject) {
                for (Map.Entry<Object, Object> item : ((NativeObject) o)
                        .entrySet()) {
                    Logger.getRootLogger().debug(
                            pad
                                    + " "
                                    + item.getKey()
                                    + "="
                                    + item.getValue()
                                    + " ("
                                    + item.getValue().getClass()
                                            .getCanonicalName() + ")");
                }
            }
        }
    }

}
