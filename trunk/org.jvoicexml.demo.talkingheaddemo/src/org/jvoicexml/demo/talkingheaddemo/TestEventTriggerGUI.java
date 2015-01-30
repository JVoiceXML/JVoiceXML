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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 * GUI to trigger different events to show and control the behaviour of the
 * avatar.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public class TestEventTriggerGUI extends JFrame {
    /**
     * Serialization unique id (generated by eclipse).
     */
    private static final long serialVersionUID = -1814557685102749791L;

    /**
     * Preferred gap size for panel layout.
     */
    private static final int PREFERRED_GAP_SIZE = 375;

    /**
     * Preferred container gap size for layout definition.
     */
    private static final int PREFERRED_CONTAINER_GAP_SIZE = 12;

    /**
     * The controller class to trigger different events.
     */
    private AvatarControl avatar;

    /**
     * Button to generate an e-mail event.
     */
    private JButton emailButton;

    /**
     * Button to generate a birthday event.
     */
    private JButton birthdayButton;

    /**
     * Button to generate a gadget event.
     */
    private JButton gadgetButton;

    /**
     * Button to generate a children event.
     */
    private JButton childrenButton;

    /**
     * Button to generate a light event.
     */
    private JButton lightButton;

    /**
     * Button to trigger an event.
     */
    private JButton submitEvent;

    /**
     * Button to generate a deadline event.
     */
    private JButton deadlineButton;

    /**
     * Button to generate a leave event.
     */
    private JButton leaveButton;

    /**
     * Textfield to define an accept text.
     */
    private JTextField acceptText;

    /**
     * Placeholder button 1.
     */
    private JButton jButton8;

    /**
     * Placeholder button 2.
     */
    private JButton jButton9;

    /**
     * Label to mark a text field for event text input.
     */
    private JLabel eventTextLabel;

    /**
     * Label to mark a text field for accept event text input.
     */
    private JLabel acceptEventLabel;

    /**
     * Label to mark a text field for reject event text input.
     */
    private JLabel rejectEventLabel;

    /**
     * Panel to set the main components.
     */
    private JPanel mainPanel;

    /**
     * Textfield to define the event text.
     */
    private JTextField offerText;

    /**
     * Textfield to define a reject text.
     */
    private JTextField rejectText;

    /**
     * Layout of the main panel.
     */
    private GroupLayout panelLayout;

    /**
     * Creates new form TestEventTriggerGUI.
     */
    public TestEventTriggerGUI() {
        initComponents();
    }

    /**
     * Creates the form with given controller.
     * 
     * @param ava
     *            the avatar controller
     */
    TestEventTriggerGUI(final AvatarControl ava) {
        avatar = ava;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {
        // Generate gui components
        generateButtons();
        generateTextFields();
        generateLabels();

        // Generate Panel
        mainPanel = new JPanel();
        generatePanelLayout();
        mainPanel.setLayout(panelLayout);

        // Set attributes of the gui
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        generateFormLayout();
        pack();
    }

    /**
     * Generates the buttons for gui.
     */
    private void generateButtons() {
        // Generate button to generate a new e-mail event.
        emailButton = new JButton("E-Mail");
        emailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                fill("Sie haben eine Nachricht von Markus. Soll ich Sie vorlesen?",
                        "Die Nachricht von Markus wird vorgelesen."
                                + "Hallo Alex, wir haben Spontan Lust auf eine Runde"
                                + "Billiard um 15 Uhr. Komm doch auch. Markus",
                        "Ok.");
            }
        });

        // Generate button to generate a new termin event.
        deadlineButton = new JButton("Termin");
        deadlineButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                fill("Markus m�chte in einer Stunde mit Ihnen Billiard "
                        + "spielen. Soll ich zusagen?",
                        "Der Termin wurde zugesagt. Ein "
                                + "Taxi steht in zehn Minuten bereit.", "Ok.");
            }
        });

        //
        leaveButton = new JButton("Verlassen");
        leaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                fill("Ihr Taxi steht vor der Haust�r. Soll ich f�r Sie das "
                        + "Fenster schlie�en und das Licht l�schen?",
                        "Ok. Viel Spa� beim Billiard.",
                        "Viel Spa� beim Billiard.");
            }
        });

        //
        lightButton = new JButton("Licht");
        lightButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                fill("Es ist dunkel. Wollen Sie das Licht einschalten?",
                        "Das Licht wird angeschaltet.", "Ok.");
            }
        });

        //
        childrenButton = new JButton("Kinder");
        childrenButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                fill("Sie haben sich schon l�nger nicht bei Ihren Kindern "
                        + "gemeldet. Soll ich eine Nachricht schicken und sagen, "
                        + "dass Sie sich bald melden?",
                        "Ihre Kinder wurden benachrichtigt.", "Ok.");
            }
        });

        birthdayButton = new JButton("Geburtstag");
        birthdayButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                fill("Markus hat heute Geburtstag. Soll ich ein Taxi rufen?",
                        "Das Taxi ist unterwegs. F�r die Route zu Markus gibt es keine "
                                + "aktuellen Staumeldungen. In Wiesbaden wird es in zwei "
                                + "Stunden regnen.", "Ok.");
            }
        });

        gadgetButton = new JButton("Ger�te");
        gadgetButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                fill("Sie wollen die Wohnung verlassen, das Radio und das Licht im "
                        + "Wohnzimmer sind noch angeschaltet. Soll ich Sie abschalten?",
                        "Die Ger�te werden abgeschaltet. Auf Wiedersehen.",
                        "Auf Wiedersehen.");
            }
        });

        //
        jButton8 = new JButton("jButton8");

        //
        jButton9 = new JButton("jButton9");

        //
        submitEvent = new JButton("Event ausf�hren");
        submitEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                avatar.queueEvent(new DummyEvent(offerText.getText(),
                        acceptText.getText(), rejectText.getText()));
                clear();
            }
        });
    }

    /**
     * Generates the layout of the main panel.
     */
    private void generatePanelLayout() {
        panelLayout = new GroupLayout(mainPanel);
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                panelLayout
                        .createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(
                                panelLayout
                                        .createParallelGroup(
                                                GroupLayout.Alignment.LEADING,
                                                false)
                                        .addComponent(deadlineButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(leaveButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(lightButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(childrenButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(birthdayButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(gadgetButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(jButton8,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(jButton9,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(emailButton,
                                                GroupLayout.Alignment.TRAILING,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE))
                        .addContainerGap()));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING)
                .addGroup(
                        panelLayout
                                .createSequentialGroup()
                                .addContainerGap()
                                .addComponent(emailButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deadlineButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(leaveButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lightButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(childrenButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(birthdayButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gadgetButton)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton8)
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9)
                                .addContainerGap(PREFERRED_CONTAINER_GAP_SIZE,
                                        Short.MAX_VALUE)));
    }

    /**
     * Generates the layout of the form.
     */
    private void generateFormLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(rejectText)
                                                .addComponent(acceptText)
                                                .addComponent(
                                                        offerText,
                                                        GroupLayout.Alignment.TRAILING)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addGroup(
                                                                        layout.createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                                .addComponent(
                                                                                        eventTextLabel)
                                                                                .addComponent(
                                                                                        acceptEventLabel)
                                                                                .addComponent(
                                                                                        rejectEventLabel)
                                                                                .addComponent(
                                                                                        submitEvent))
                                                                .addGap(0,
                                                                        PREFERRED_GAP_SIZE,
                                                                        Short.MAX_VALUE)))
                                .addPreferredGap(
                                        LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mainPanel,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addContainerGap()));
        layout.setVerticalGroup(layout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                        layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        layout.createParallelGroup(
                                                GroupLayout.Alignment.LEADING)
                                                .addComponent(
                                                        mainPanel,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        GroupLayout.DEFAULT_SIZE,
                                                        Short.MAX_VALUE)
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(
                                                                        eventTextLabel)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        offerText,
                                                                        GroupLayout.PREFERRED_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        acceptEventLabel)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        acceptText,
                                                                        GroupLayout.PREFERRED_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        rejectEventLabel)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        rejectText,
                                                                        GroupLayout.PREFERRED_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(
                                                                        submitEvent)
                                                                .addGap(0,
                                                                        0,
                                                                        Short.MAX_VALUE)))
                                .addContainerGap()));
    }

    /**
     * Generates the text fields for the gui.
     */
    private void generateTextFields() {
        offerText = new JTextField();
        acceptText = new JTextField();
        rejectText = new JTextField();
    }

    /**
     * Generates the labels for the gui.
     */
    private void generateLabels() {
        eventTextLabel = new JLabel();
        eventTextLabel.setText("Event und Angebot");

        acceptEventLabel = new JLabel();
        acceptEventLabel.setText("Zustimmung");

        rejectEventLabel = new JLabel();
        rejectEventLabel.setText("Ablehnung");
    }

    /**
     * Sets data to the form of the gui.
     * 
     * @param offer
     *            the offer text
     * @param yes
     *            the accept text
     * @param no
     *            the reject text
     */
    private void fill(final String offer, final String yes, final String no) {
        offerText.setText(offer);
        acceptText.setText(yes);
        rejectText.setText(no);
    }

    /**
     * Clears the formular of the gui.
     */
    private void clear() {
        offerText.setText(null);
        acceptText.setText(null);
        rejectText.setText(null);
    }
}
