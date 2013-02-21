/*
 * JVoiceXML VTP Plugin
 *
 * Copyright (C) 2006 Dirk Schnelle-Walka
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jvoicexml.eclipse.debug.ui.launching.jvoicexml;

import java.io.File;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jvoicexml.eclipse.debug.ui.BrowserConfigurationUIListener;
import org.jvoicexml.eclipse.debug.ui.IBrowserConfigurationUI;

/**
 * User interface for the JVoiceXmlBrowser.
 *
 * @author Dirk Schnelle-Walka
 * @author Aurelian Maga
 * 
 * @see org.jvoicexml.eclipse.debug.ui.launching.jvoicexml.JVoiceXmlBrowser
 */
public final class JVoiceXmlBrowserUI
    implements IBrowserConfigurationUI {
    /** Default value for the policy. */
    private static final String DEFAULT_POLICY;

    /** Default value for the initial context factory.*/
    private static final String DEFAULT_CONTEXT_FACTORY =
        "com.sun.jndi.rmi.registry.RegistryContextFactory";

    /** Default value for the provider URL. */
    private static final String DEFAULT_PROVIDER_URL = "rmi://localhost:1099";

    /** Default value for the logging port. */
    private static final int DEFAULT_PORT = 4242;
    
    /** Default port for text client */
    private static final int DEFAULT_TEXT_PORT = 4243;

    /* Default value for the logging level. */
    private static final String DEFAULT_LEVEL;

    /** Location of the security policy. */
    private Text policy;

    /** Browse for a policy. */
    private Button findPolicy;

    /** RMI provider URL. */
    private Text providerUrl;

    /** Port number of the log4j logger. */
    private Text port;

    /** Port number of the text client. */
    private Text textPort;
    
    /** Debug level. */
    private Combo level;

    static {
        final JVoiceXmlPlugin plugin = JVoiceXmlPlugin.getDefault();
        final String policy = plugin.getFile("jvoicexml.policy");
        if (policy == null) {
            DEFAULT_POLICY = "jvoicexml.policy";
        } else {
            DEFAULT_POLICY = policy;
        }

        DEFAULT_LEVEL = Level.INFO.toString();
    }

    /**
     * Constructs a new object.
     */
    public JVoiceXmlBrowserUI() {
    }

    /**
     * {@inheritDoc}
     */
    public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
        getAttribute(configuration, JVoiceXmlPluginConstants.JNDI_POLICY,
                     DEFAULT_POLICY, policy);
        getAttribute(configuration, JVoiceXmlPluginConstants.JNDI_PROVIDER_URL,
                     DEFAULT_PROVIDER_URL, providerUrl);
        getAttribute(configuration, JVoiceXmlPluginConstants.LOGGING_PORT,
                     DEFAULT_PORT, port);
        getAttribute(configuration, JVoiceXmlPluginConstants.LOGGING_LEVEL,
                     DEFAULT_LEVEL, level);
        getAttribute(configuration, JVoiceXmlPluginConstants.TEXT_PORT,
                DEFAULT_TEXT_PORT, textPort);        
    }

    /**
     * {@inheritDoc}
     */
    public void performApply(final ILaunchConfigurationWorkingCopy
                             configuration) {
        setAttribute(configuration, JVoiceXmlPluginConstants.JNDI_POLICY,
                     policy);
        configuration
            .setAttribute(JVoiceXmlPluginConstants.JNDI_CONTEXT_FACTORY,
                          DEFAULT_CONTEXT_FACTORY);
        setAttribute(configuration, JVoiceXmlPluginConstants.JNDI_PROVIDER_URL,
                     providerUrl);
        setIntAttribute(configuration, JVoiceXmlPluginConstants.LOGGING_PORT,
                        port);
        setAttribute(configuration, JVoiceXmlPluginConstants.LOGGING_LEVEL,
                     level);
        setIntAttribute(configuration, JVoiceXmlPluginConstants.TEXT_PORT,
                textPort);
    }

    /**
     * Convenience method to set a configuration value from a text.
     *
     * @param configuration
     *        The configuration.
     * @param attribute
     *        Name of the attribute,
     * @param text
     *        The text.
     */
    private void setAttribute(
        final ILaunchConfigurationWorkingCopy configuration,
        final String attribute, final Text text) {
        if ( (configuration == null) || (text == null)) {
            return;
        }

        final String value = text.getText();
        configuration.setAttribute(attribute, value);
    }

    /**
     * Convenience method to set a configuration value from a combo box.
     *
     * @param configuration
     *        The configuration.
     * @param attribute
     *        Name of the attribute,
     * @param combo
     *        The combo box containing the current value.
     */
    private void setAttribute(
        final ILaunchConfigurationWorkingCopy configuration,
        final String attribute, final Combo combo) {
        if ( (configuration == null) || (combo == null)) {
            return;
        }

        final String value = combo.getText();
        configuration.setAttribute(attribute, value);
    }

    /**
     * Convenience method to set an integer configuration value from a text.
     *
     * @param configuration
     *        The configuration.
     * @param attribute
     *        Name of the attribute,
     * @param text
     *        The text containg the current value..
     */
    private void setIntAttribute(
        final ILaunchConfigurationWorkingCopy configuration,
        final String attribute, final Text text) {
        if ( (configuration == null) || (text == null)) {
            return;
        }

        final String value = text.getText();
        try {
            final Integer intValue = Integer.parseInt(value);
            configuration.setAttribute(attribute, intValue);
        } catch (NumberFormatException ignore) {
        }
    }

    /**
     * Convenience method to set a text value from a configuration
     *
     * @param configuration
     *        The configuration.
     * @param attribute
     *        Name of the attribute.
     * @param def
     *        Default value.
     * @param text
     *        The text to use for display.
     */
    private void getAttribute(final ILaunchConfiguration configuration,
                              final String attribute, final String def,
                              final Text text) {
        if ( (configuration == null) || (text == null)) {
            return;
        }

        try {
            final Object value = configuration.getAttribute(attribute, def);
            if (value == null) {
                return;
            }

            String str = value.toString();
            if (str.length() == 0) {
                str = def;
            }
            
            text.setText(str);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to set a combo box value from a configuration.
     *
     * @param configuration
     *        The configuration.
     * @param attribute
     *        Name of the attribute.
     * @param def
     *        Default value.
     * @param combo
     *        The combo box to use for display.
     */
    private void getAttribute(final ILaunchConfiguration configuration,
                              final String attribute, final String def,
                              final Combo combo) {
        if ( (configuration == null) || (combo == null)) {
            return;
        }

        try {
            final Object value = configuration.getAttribute(attribute, def);
            if (value == null) {
                return;
            }

            String str = value.toString();
            if (str.length() == 0) {
                str = def;
            }
            
            combo.setText(str);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convenience method to set a text value from a configuration.
     *
     * @param configuration
     *        The configuration.
     * @param attribute
     *        Name of the attribute.
     * @param def
     *        Default value.
     * @param text
     *        The text to use for display.
     */
    private void getAttribute(final ILaunchConfiguration configuration,
                              final String attribute, final int def,
                              final Text text) {
        if ( (configuration == null) || (text == null)) {
            return;
        }

        try {
            final Object value = configuration.getAttribute(attribute, def);
            if (value == null) {
                return;
            }

            text.setText(value.toString());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void initializeFrom(final ILaunchConfiguration configuration) {
        getAttribute(configuration, JVoiceXmlPluginConstants.JNDI_POLICY,
                     DEFAULT_POLICY, policy);
        getAttribute(configuration, JVoiceXmlPluginConstants.JNDI_PROVIDER_URL,
                     DEFAULT_PROVIDER_URL, providerUrl);
        getAttribute(configuration, JVoiceXmlPluginConstants.LOGGING_PORT,
                     DEFAULT_PORT, port);
        getAttribute(configuration, JVoiceXmlPluginConstants.LOGGING_LEVEL,
                     DEFAULT_LEVEL, level);
        getAttribute(configuration, JVoiceXmlPluginConstants.TEXT_PORT,
                DEFAULT_TEXT_PORT, textPort);
    }

    /**
     * {@inheritDoc}
     */
    public void drawConfigurationUI(final Composite ui,
                                    final BrowserConfigurationUIListener
                                    listener) {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        final FormLayout uilayout = new FormLayout();
        ui.setLayout(uilayout);

        final GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;

        final Group logging = new Group(ui, SWT.PUSH);
        logging.setLayout(layout);
        logging.setText("Logging");
        final FormData loggingData = new FormData();
        loggingData.top = new FormAttachment(0, 5);
        loggingData.left = new FormAttachment(0, 5);
        loggingData.right = new FormAttachment(100, 0);
        logging.setLayoutData(loggingData);

        final Label portLabel = new Label(logging, SWT.PUSH);
        portLabel.setText("Logger port:");

        port = new Text(logging, SWT.BORDER);
        port.setToolTipText("Port of the log4j socket appender of JVoiceXml");
        data.grabExcessHorizontalSpace = true;
        port.setLayoutData(data);
        port.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                listener.contentsChanged();
            }
        });

        final Label levelLabel = new Label(logging, SWT.NONE);
        levelLabel.setText("Logger level:");

        level = new Combo(logging, SWT.BORDER);
        level.setToolTipText("Minimal level for the logging output");
        level.add(Level.TRACE.toString());
        level.add(Level.DEBUG.toString());
        level.add(Level.INFO.toString());
        level.add(Level.WARN.toString());
        level.add(Level.ERROR.toString());
        level.add(Level.FATAL.toString());

        data.grabExcessHorizontalSpace = true;
        level.setLayoutData(data);
        level.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                listener.contentsChanged();
            }
        });

        final Group jndi = new Group(ui, SWT.NONE);
        final GridLayout jndiLayout = new GridLayout();
        jndiLayout.numColumns = 3;
        jndi.setLayout(jndiLayout);
        jndi.setText("JNDI");
        final FormData jndiData = new FormData();
        jndiData.top = new FormAttachment(logging, 5);
        jndiData.left = new FormAttachment(0, 5);
        jndiData.right = new FormAttachment(100, -5);

        jndi.setLayoutData(jndiData);

        final Label policyLabel = new Label(jndi, SWT.NONE);
        policyLabel.setText("Security policy:");

        policy = new Text(jndi, SWT.BORDER);
        policy
            .setToolTipText("Full path to the location of the security policy");
        data.grabExcessHorizontalSpace = true;
        policy.setLayoutData(data);
        policy.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                listener.contentsChanged();
            }
        });
        findPolicy = new Button(jndi, SWT.RIGHT);
        findPolicy.setText("...");
        findPolicy.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
                FileDialog dialog = new FileDialog(ui.getShell());
                String file = dialog.open();
                if (file != null) {
                    policy.setText(file);
                }
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
            }
        });
        final Label providerUrlLabel = new Label(jndi, SWT.NONE);
        providerUrlLabel.setText("RMI provider URL:");

        providerUrl = new Text(jndi, SWT.BORDER);
        providerUrl
            .setToolTipText(
                "URL of the RMI provider. Typically rmi://localhost:1099");
        data.grabExcessHorizontalSpace = true;
        providerUrl.setLayoutData(data);
        providerUrl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                listener.contentsChanged();
            }
        });

        final Group textClientGroup = new Group(ui,SWT.PUSH);
        textClientGroup.setLayout(layout);
        textClientGroup.setText("Text Client");
        
        final FormData textClientGroupData = new FormData();
        textClientGroupData.top = new FormAttachment(jndi, 5);
        textClientGroupData.left = new FormAttachment(0, 5);
        textClientGroupData.right = new FormAttachment(100, -5);

        textClientGroup.setLayoutData(textClientGroupData);
        
        final Label textClientPortLabel = new Label(textClientGroup,SWT.NONE);
        textClientPortLabel.setText("Text client port:");
        
        textPort = new Text(textClientGroup,SWT.BORDER);
        data.grabExcessHorizontalSpace = true;
        textPort
        .setToolTipText("TextRemoteClient port number");
        textPort.setLayoutData(data);
        textPort.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                listener.contentsChanged();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValid(final ILaunchConfiguration configuration) {
        final String enteredPolicy = policy.getText().trim();
        if (enteredPolicy.length() == 0) {
            System.out.println("No policy given!");
            return false;
        }

        if (!existsFile(enteredPolicy)) {
            System.out.println("Policy does not point to an existing file!");
            return false;
        }

        final String enteredProviderUrl = providerUrl.getText().trim();
        if (enteredProviderUrl.length() == 0) {
            System.out.println("No provider url given!");
            return false;
        }

        final String enteredPort = port.getText().trim();
        if (enteredPort.length() == 0) {
            System.out.println("No port name given!");
            return false;
        }

        try {
            Integer.parseInt(enteredPort);
        } catch (NumberFormatException nfe) {
            System.out.println("Port number must be a number!");
            return false;
        }

        final String enteredTextPort = textPort.getText().trim();
        try {
        	Integer.parseInt(enteredTextPort);
        } catch(NumberFormatException nfe){
            System.out.println("TextClient Port number must be a number!");
            return false;
        }
        return true;
    }

    /**
     * Checks, if the given file exists.
     *
     * @param name
     *        Name of the file.
     * @return <code>true</code> if the file exists.
     */
    private boolean existsFile(final String name) {
        final File file = new File(name);

        return file.exists();

    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    }
}
