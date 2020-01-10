/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.demo.talkingheaddemo;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.client.BasicConnectionInformation;
import org.jvoicexml.client.GenericClient;
import org.jvoicexml.event.ErrorEvent;

/**
 * AppController, to execute the program and generates the necessary documents
 * for the voicexml. browser(incl. BML for the avatar talkinghead)
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public final class AvatarControl {
    /**
     * Constant for the sleep method of the thread.
     */
    private static final int MAX_THREAD_SLEEP_TIME = 2000;

    /**
     * List of available sentence objects.
     */
    private final ArrayList<String> objects;

    /**
     * List of available sentence verbs.
     */
    private final ArrayList<String> verbs;

    /**
     * List of allowed object-verb-pairs.
     */
    private final ArrayList<StringPair> meaningful;

    /**
     * Map of different objects to specific verbs.
     */
    private final HashMap<String, ArrayList<String>> objectToVerbs;

    /**
     * Map of different verbs to specific objects.
     */
    private final HashMap<String, ArrayList<String>> verbToObjects;

    /**
     * Lines of the config file.
     */
    private final List<String> lines;

    /**
     * Queue of triggered events.
     */
    private Queue<AvatarEvent> eventQueue;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(final String[] args) {

        /*
         * if (args[0] == null) { throw new
         * InvalidParameterException("Please provide a config file"); }
         */

        List<String> lines;

        try {
            lines = Files.readAllLines(
                    Paths.get(/* args[0] */"./etc/default_room.csv"),
                    Charset.defaultCharset());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        /*
         * Set the Nimbus look and feel If Nimbus (introduced in Java SE 6) is
         * not available, stay with the default look and feel. For details see
         * http
         * ://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(
                    TestEventTriggerGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(
                    TestEventTriggerGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(
                    TestEventTriggerGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(
                    TestEventTriggerGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>

        final AvatarControl ava = new AvatarControl(lines);
        ava.generateDocuments();
        new Thread(new Runnable() {

            @Override
            public void run() {
                ava.start();
            }
        }).start();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TestEventTriggerGUI(ava).setVisible(true);
            }
        });
    }

    /**
     * Constructor, which initizes the AvatarControl framework with the lines of
     * the config file.
     * 
     * @param theLines
     *            of the config file
     */
    private AvatarControl(final List<String> theLines) {
        lines = theLines;
        objects = new ArrayList<>();
        verbs = new ArrayList<>();
        meaningful = new ArrayList<>();
        objectToVerbs = new HashMap<>();
        verbToObjects = new HashMap<>();
        eventQueue = new ArrayDeque<>();
    }

    /**
     * Method, which generates the different jvoicexml documents.
     */
    protected void generateDocuments() {
        // //final String objectsSRGS;
        // final String verbsSRGS;
        // final String actionsSRGS;
        // final String bad_actionsSRGS;
        // // final String object_verbSRGS;
        // // final String verb_objectSRGS;

        for (String line : lines) {
            String[] tok = line.split(";");
            String obj = tok[0]; // expect uniqueness of objects
            objects.add(obj);
            ArrayList<String> verbsForCurObj = new ArrayList<>();
            objectToVerbs.put(obj, verbsForCurObj);
            for (int i = 1; i < tok.length; i++) {
                String toki = tok[i];
                if (!verbs.contains(toki)) {
                    verbs.add(toki);
                }
                meaningful.add(new StringPair(obj, toki));
                verbsForCurObj.add(toki); // expect uniqueness of verbs for each
                                          // line
                ArrayList<String> objForCurVerb = verbToObjects.get(toki);
                if (objForCurVerb == null) {
                    objForCurVerb = new ArrayList<>();
                    verbToObjects.put(toki, objForCurVerb);
                }
                objForCurVerb.add(obj);
            }
        }

        clearGeneratedDirectory();

        writeFile("etc\\voiceXML\\generated\\standard.srgs",
                generateStandardGrammar());

        writeFile("etc\\voiceXML\\generated\\ActionsList.vxml",
                generatePromptActions());

        for (String obj : objectToVerbs.keySet()) {
            writeFile("etc\\voiceXML\\generated\\ObjectAskVerbs_" + obj
                    + ".vxml", generateObjectAskVerbs(obj));
            writeFile("etc\\voiceXML\\generated\\ObjectAskVerbs_" + obj
                    + ".srgs", generateObjectAskVerbsGrammar(obj));
            writeFile("etc\\voiceXML\\generated\\SelectActionMenu_" + obj
                    + ".vxml", generateObjectVerbsList(obj));
        }

        for (String verb : verbToObjects.keySet()) {
            writeFile("etc\\voiceXML\\generated\\VerbAskObjects_" + verb
                    + ".vxml", generateVerbAskObjects(verb));
            writeFile("etc\\voiceXML\\generated\\VerbAskObjects_" + verb
                    + ".srgs", generateVerbAskObjectsGrammar(verb));
            writeFile("etc\\voiceXML\\generated\\SelectActionMenu_" + verb
                    + ".vxml", generateVerbObjectsList(verb));
        }

    }

    /**
     * Generates the directories for the different documents.
     */
    protected void clearGeneratedDirectory() {
        DirectoryStream<Path> paths = null;
        Path path = Paths.get("etc\\voiceXML\\generated");
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            paths = Files.newDirectoryStream(path);
            for (Path f : paths) {
                Files.delete(f);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Generates the supported gramars of the dialogs.
     * 
     * @return the string containing the content of the grammar files.
     */
    protected String generateStandardGrammar() {
        String objectsSRGS;
        String verbsSRGS;
        String actionsSRGS;
        String badActionsSRGS;
        GrammarCreator gc = new GrammarCreator();
        // add objects
        gc.openRule("objects", "out = new Object(); "
                + "out.r = new Object(); " + "out.r.match = new Object(); "
                + "out.r.result = new Object();");
        for (String obj : objects) {
            gc.addItem(obj, "out.r.match=3; out.r.result=\"" + obj + "\";");
        }
        gc.closeRule();
        objectsSRGS = gc.toString();
        gc.reset();
        // add verbs
        gc.openRule("verbs", "out = new Object(); " + "out.r = new Object(); "
                + "out.r.match = new Object(); "
                + "out.r.result = new Object();");
        for (String verb : verbs) {
            gc.addItem(verb, "out.r.match=2; out.r.result=\"" + verb + "\";");
        }
        gc.closeRule();
        verbsSRGS = gc.toString();
        gc.reset();
        // add meaningful
        gc.openRule("actions", "out = new Object();" + "out.r = new Object();"
                + "out.r.match = new Object();"
                + "out.r.result = new Object();");
        for (StringPair action : meaningful) {
            gc.addItem(action.toString(), "out.r.match=1; out.r.result=\""
                    + action.toString() + "\";");
            gc.addItem(action.reverseToString(),
                    "out.r.match=1; out.r.result=\"" + action.toString()
                            + "\";");
        }
        gc.closeRule();
        actionsSRGS = gc.toString();
        gc.reset();
        // add bad actions
        gc.openRule("bad_actions", "out = new Object(); "
                + "out.r = new Object(); " + "out.r.match = new Object(); "
                + "out.r.result = new Object();");
        ArrayList<StringPair> badActions = new ArrayList<>();
        for (String obj : objects) {
            for (String verb : verbs) {
                StringPair pair = new StringPair(obj, verb);
                if (!meaningful.contains(pair)) {
                    badActions.add(pair);
                    gc.addItem(pair.toString(),
                            "out.r.match=0; out.r.result=\"" + pair.toString()
                                    + "\";");
                    gc.addItem(pair.reverseToString(),
                            "out.r.match=0; out.r.result=\"" + pair.toString()
                                    + "\";");
                }
            }
        }
        gc.closeRule();
        badActionsSRGS = gc.toString();
        gc.reset();

        String standardSRGS = readFile("etc\\templates\\object_verb.srgs");
        standardSRGS = String.format(standardSRGS, objectsSRGS + verbsSRGS
                + actionsSRGS + badActionsSRGS);
        return standardSRGS;
    }

    /**
     * Read a complete text file.
     * 
     * @param path
     *            the path to the text file
     * @return the content of the text file
     */
    protected String readFile(final String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Writes a complete string to a file.
     * 
     * @param path
     *            the path of the file
     * @param standardSRGS
     *            the text (in this case jvoice grammar) to write to file
     */
    protected void writeFile(final String path, final String standardSRGS) {
        try {
            Files.write(Paths.get(path), standardSRGS.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Starts the JvoiceXML dialog.
     * 
     * @return true if it is successful
     */
    protected boolean start() {
        // Start JVOICEXML
        final GenericClient client = new GenericClient();
        final File file = new File("etc\\voiceXML\\DialogStart.vxml");
        final URI dialog = file.toURI();

        String simpleEventString = readFile("etc\\templates\\SimpleEvent.vxml");

        queueEvent(new DummyEvent("Haben Sie gut geschlafen?",
                "Das freut mich.", "Oh das tut mir Leid."));

        while (true) {
            while (!eventQueue.isEmpty()) {
                AvatarEvent triggeredEvent = eventQueue.poll();
                writeFile(
                        "etc\\voiceXML\\generated\\temp.vxml",
                        String.format(simpleEventString,
                                triggeredEvent.getOfferText(),
                                triggeredEvent.getAcceptText(),
                                triggeredEvent.getRejectText()));
                call(client, Paths.get("etc\\voiceXML\\generated\\temp.vxml")
                        .toUri());
                try {
                    Thread.sleep(MAX_THREAD_SLEEP_TIME);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(
                            AvatarControl.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }

            call(client, dialog);
        }
    }

    /**
     * Calls a specific dialog.
     * 
     * @param client
     *            the client to call
     * @param dialog
     *            the dialog to load
     * @return true if it is successful
     */
    protected boolean call(final GenericClient client, final URI dialog) {
        interpretDocument(dialog);
        return true;
    }

    /**
     * Interprets a jvoice document.
     * 
     * @param uri
     *            URL to the document
     */
    private void interpretDocument(final URI uri) {
        InitialContext context = null;
        try {
            context = new InitialContext();
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JVoiceXml jvxml;
        try {
            jvxml = (JVoiceXml) context.lookup("JVoiceXml");
        } catch (javax.naming.NamingException ne) {

            return;
        }

        final ConnectionInformation client = new BasicConnectionInformation(
                "desktop", "bml", "jsapi20");
        Session session;
        try {
            final SessionIdentifier id = new UuidSessionIdentifier();
            session = jvxml.createSession(client, id);

            session.call(uri);

            session.waitSessionEnd();

            session.hangup();
        } catch (ErrorEvent e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Generates a jvoicexml promt action.
     * 
     * @return the prompt action
     */
    protected String generatePromptActions() {
        String sysNewLine = String.format("%n");
        StringBuffer b = new StringBuffer();
        for (String obj : objectToVerbs.keySet()) {
            b.append("\t\t Das Objekt ");
            b.append(obj);
            b.append(" kann ich");
            boolean first = true;
            for (String verb : objectToVerbs.get(obj)) {
                if (first) {
                    b.append(" ");
                    first = false;
                } else {
                    b.append(" oder ");
                }
                b.append(verb);
            }
            b.append(sysNewLine);
        }
        return String.format(readFile("etc\\templates\\ActionsList.vxml"),
                b.toString());
    }

    /**
     * Generates the dialog part to ask a verb to an object.
     * 
     * @param obj
     *            the known object
     * @return the dialog part to ask a verb to an object
     */
    protected String generateObjectAskVerbs(final String obj) {
        return String.format(readFile("etc\\templates\\ObjectAskVerbs.vxml"),
                obj, obj, obj);
    }

    /**
     * Generates a grammar for the object ask verb dialog part.
     * 
     * @param obj
     *            the known object
     * @return the grammar of the object ask verb dialog part
     */
    protected String generateObjectAskVerbsGrammar(final String obj) {
        GrammarCreator gc = new GrammarCreator();
        gc.addHeader("ObjectAskVerbs");
        gc.openRule("ObjectAskVerbs", "out = new Object(); "
                + "out.r = new Object(); " + "out.r.match = new Object(); "
                + "out.r.result = new Object();");
        for (String verb : objectToVerbs.get(obj)) {
            gc.addItem(verb, "out.r.match=1; out.r.result=\"" + obj + " "
                    + verb + "\";");
        }
        gc.closeRule();
        gc.addFooter();
        return gc.toString();
    }

    /**
     * Generates the dialog part to choose a verb to an object via list.
     * 
     * @param obj
     *            the known object
     * @return the dialog part to choose a verb from list
     */
    protected String generateObjectVerbsList(final String obj) {
        StringBuffer prompt = new StringBuffer();
        StringBuffer filled = new StringBuffer();
        String sysNewLine = String.format("%n");
        int i = 1;
        for (String verb : objectToVerbs.get(obj)) {
            prompt.append("\t\t\t\tSagen sie ");
            prompt.append(i);
            prompt.append(" für ");
            prompt.append(obj);
            prompt.append(" ");
            prompt.append(verb);
            prompt.append(".");
            prompt.append(sysNewLine);

            filled.append("\t\t\t\t\t<elseif cond=\"action==");
            filled.append(i).append("\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t<prompt>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:gesture " + "xmlns:ns1="
                    + "\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "lexeme=\"idle\" id=\"id_0\" start=\"0\" end=\"100\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:gesture " + "xmlns:ns1="
                    + "\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "lexeme=\"nod\" id=\"id_1\" start=\"id_0:end+1\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:speech " + "xmlns:ns1="
                    + "\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "id=\"id_2\" start=\"id_1:end+1\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\tBefehl ");
            filled.append(obj);
            filled.append(" ");
            filled.append(verb);
            filled.append(" wird ausgeführt.");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:gesture " + "xmlns:ns1="
                    + "\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "lexeme=\"idle\" id=\"id_3\" start=\"id_2:end+1\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t</prompt>");
            filled.append(sysNewLine);
            i++;
        }
        return String.format(readFile("etc\\templates\\SelectActionMenu.vxml"),
                "SelectActionMenu", prompt.toString(), filled.toString());
    }

    /**
     * Generates the dialog part to ask an object to a verb.
     * 
     * @param verb
     *            the known verb
     * @return the dialog part to ask an object to a verb
     */
    protected String generateVerbAskObjects(final String verb) {
        return String.format(readFile("etc\\templates\\VerbAskObjects.vxml"),
                verb, verb, verb);
    }

    /**
     * Generates a grammar for the verb ask object dialog part.
     * 
     * @param verb
     *            the known verb
     * @return the grammar of the verb ask object dialog part
     */
    protected String generateVerbAskObjectsGrammar(final String verb) {
        GrammarCreator gc = new GrammarCreator();
        gc.addHeader("VerbAskObjects");
        gc.openRule("VerbAskObjects", "out = new Object(); "
                + "out.r = new Object(); " + "out.r.match = new Object(); "
                + "out.r.result = new Object();");
        for (String obj : verbToObjects.get(verb)) {
            gc.addItem(obj, "out.r.match=1; out.r.result=\"" + obj + " " + verb
                    + "\";");
        }
        gc.closeRule();
        gc.addFooter();
        return gc.toString();
    }

    /**
     * Generates a list of objects to a given verb for dialog.
     * 
     * @param verb
     *            the known verb
     * @return the list of objects to a list for output
     */
    protected String generateVerbObjectsList(final String verb) {
        StringBuffer prompt = new StringBuffer();
        StringBuffer filled = new StringBuffer();
        String sysNewLine = String.format("%n");
        int i = 1;
        for (String obj : verbToObjects.get(verb)) {
            prompt.append("\t\t\t\tSagen sie ");
            prompt.append(i);
            prompt.append(" für ");
            prompt.append(obj);
            prompt.append(" ");
            prompt.append(verb);
            prompt.append(".");
            prompt.append(sysNewLine);

            filled.append("\t\t\t\t\t<elseif cond=\"action==");
            filled.append(i).append("\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t<prompt>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:gesture "
                    + "xmlns:ns1=\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "lexeme=\"idle\" id=\"id_0\" start=\"0\" end=\"100\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:gesture "
                    + "xmlns:ns1=\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "lexeme=\"nod\" id=\"id_1\" start=\"id_0:end+1\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:speech "
                    + "xmlns:ns1=\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "id=\"id_2\" start=\"id_1:end+1\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\tBefehl ");
            filled.append(obj);
            filled.append(" ");
            filled.append(verb);
            filled.append(" wird ausgef�hrt.");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t\t<ns1:gesture "
                    + "xmlns:ns1=\"http://www.mindmakers.org/projects/bml-1-0/wiki\" "
                    + "lexeme=\"idle\" id=\"id_3\" start=\"id_2:end+1\"/>");
            filled.append(sysNewLine);
            filled.append("\t\t\t\t\t\t</prompt>");
            filled.append(sysNewLine);
            i++;
        }
        return String.format(readFile("etc\\templates\\SelectActionMenu.vxml"),
                "SelectActionMenu", prompt.toString(), filled.toString());
    }

    /**
     * Adds an event to the queue.
     * 
     * @param event
     *            the event to add
     */
    public void queueEvent(final AvatarEvent event) {
        synchronized (this) {
            eventQueue.add(event);
        }
    }

}
