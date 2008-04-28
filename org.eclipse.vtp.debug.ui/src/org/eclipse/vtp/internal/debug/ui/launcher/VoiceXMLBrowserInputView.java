package org.eclipse.vtp.internal.debug.ui.launcher;
/*******************************************************************************
 * Copyright (c) 2005,2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.vtp.launching.IVoiceXMLBrowser;
import org.eclipse.vtp.launching.IVoiceXMLBrowserConstants;
import org.eclipse.vtp.launching.VoiceXMLBrowserInput;
import org.eclipse.vtp.launching.VoiceXMLBrowserProcess;
import org.eclipse.vtp.launching.VoiceXMLLogMessage;

/**
 * Input view sending events to a VoiceXML browser such as DTMF keypresses. It also presents log
 *  data in a table. Clients should not extend this class directly, but may use it as an example of how
 *  to send and listen for events.
 *  
 *  @author Brent D. Metz
 */
public class VoiceXMLBrowserInputView extends ViewPart {
	protected Group dtmfComposite = null;
	
	/**
	 * Vector of VoiceXMLBrowserProcess objects representing the currently available browsers.
	 */
	protected Vector activeBrowsers = new Vector();
	
	/**
	 * The browser to which this view is currently associated. All input events go to this browser and all log data from this
	 * 
	 */
	protected VoiceXMLBrowserProcess activeBrowser = null;
	protected Button one,two,three,four,five,six,seven,eight,nine,star,pound,zero,say;
	protected Text input;
	protected Action terminateAction=null;
	
	protected Composite userInputComposite = null;
	protected Group txtComposite = null;
	protected Group logComposite = null;
	protected Table logTable = null;
	
	protected SimpleDateFormat formatter = null;
	
	private void fireDTMF(String key) {
		if (activeBrowser == null) {
			return;
		}
		VoiceXMLBrowserInput input = new VoiceXMLBrowserInput();
		input.setInputType(VoiceXMLBrowserInput.TYPE_DTMF);
		input.setInput(key);

		activeBrowser.getVoiceXMLBrowser().sendInput(input);
	}

	private void fireTEXT(String text) {
		if (activeBrowser == null) {
			return;
		}
		VoiceXMLBrowserInput input = new VoiceXMLBrowserInput();
		input.setInputType(VoiceXMLBrowserInput.TYPE_VOICE);
		input.setInput(text);

		activeBrowser.getVoiceXMLBrowser().sendInput(input);
	}

	/**
	 * Updates the drop down menu on the Input View.
	 *
	 */
	private void updateViewMenu() {
		terminateAction.setEnabled(activeBrowser != null);
		getViewSite().getActionBars().updateActionBars();
		
		IMenuManager imm = getViewSite().getActionBars().getMenuManager();
		imm.removeAll();
		
		if (activeBrowsers.size() == 0) {
			Action nothing = new Action("No browsers available.") {
				public void run() {
				}
			};
			nothing.setEnabled(true);
			imm.add(nothing);
		}
		
		for (int i = 0 ; i < activeBrowsers.size() ; i++) {
			final VoiceXMLBrowserProcess p = (VoiceXMLBrowserProcess)activeBrowsers.get(i);
			Action a = new Action(p.getLabel()) {
				public void run() {
					if (activeBrowser==p) {
						return;
					}
					activeBrowser=p;
					clearLogTable();
					updateUI();
				}
			};
			a.setChecked(p==activeBrowser);
			imm.add(a);
		}
		getViewSite().getActionBars().updateActionBars();
	}
	
	/**
	 * Conveniance function to clear the log table on the display thread.
	 *
	 */
	private void clearLogTable() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (logTable != null) {
					logTable.removeAll();
				}
			}
		});
	}
	
	/**
	 * Enables/disables UI based upon the currently selected browser.
	 *
	 */
	private void updateUI() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateViewMenu();
				
				boolean useDTMF = false;
				boolean useTEXT = false;
				
				if (activeBrowser != null) {
					if (activeBrowser.getVoiceXMLBrowser().hasCapability(IVoiceXMLBrowser.CAPABILITY_DTMF)) {
						useDTMF = true;
					}
					if (activeBrowser.getVoiceXMLBrowser().hasCapability(IVoiceXMLBrowser.CAPABILITY_INTERACTIVE)) {
						useTEXT = true;
					}
					
				}
				
				one.setEnabled(useDTMF);
				two.setEnabled(useDTMF);
				three.setEnabled(useDTMF);
				four.setEnabled(useDTMF);
				five.setEnabled(useDTMF);
				six.setEnabled(useDTMF);
				seven.setEnabled(useDTMF);
				eight.setEnabled(useDTMF);
				nine.setEnabled(useDTMF);
				star.setEnabled(useDTMF);
				zero.setEnabled(useDTMF);
				pound.setEnabled(useDTMF);
				
				input.setEnabled(useTEXT);
				say.setEnabled(useTEXT);
			}
		});
		
	}
	

	public void createPartControl(Composite parent) {
		
		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		
		GridLayout gl = new GridLayout();
		gl.numColumns=2;
		parent.setLayout(gl);
		
		userInputComposite = new Composite(parent,SWT.NONE);
		gl = new GridLayout();
		gl.numColumns=1;
		userInputComposite.setLayout(gl);
		GridData gd =new GridData();
		gd.verticalAlignment=GridData.BEGINNING;
		userInputComposite.setLayoutData(gd);
		
		
		dtmfComposite=new Group(userInputComposite, SWT.NONE);
		dtmfComposite.setText("DTMF");
		gd =new GridData();
		gd.verticalAlignment=GridData.BEGINNING;
		dtmfComposite.setLayoutData(gd);
		
		IToolBarManager itm = getViewSite().getActionBars().getToolBarManager();
		terminateAction = new Action() {
			public void run() {
				if (activeBrowser != null) {
					if (activeBrowser.getVoiceXMLBrowser() != null) {
						activeBrowser.getVoiceXMLBrowser().stop();
					}
					activeBrowser = null;
				}
			}
		};
		terminateAction.setText("Terminate");
		terminateAction.setToolTipText("Terminates the current browser.");
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL("platform:/plugin/org.eclipse.debug.ui/icons/full/elcl16/terminate_co.gif"));
			terminateAction.setImageDescriptor(id);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		itm.add(terminateAction);
		getViewSite().getActionBars().updateActionBars();
		
		KeyListener kl = new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				switch (e.character) {
					case '1': fireDTMF("1"); break;
					case '2': fireDTMF("2"); break;
					case '3': fireDTMF("3"); break;
					case '4': fireDTMF("4"); break;
					case '5': fireDTMF("5"); break;
					case '6': fireDTMF("6"); break;
					case '7': fireDTMF("7"); break;
					case '8': fireDTMF("8"); break;
					case '9': fireDTMF("9"); break;
					case '0': fireDTMF("0"); break;
					case '*': fireDTMF("*"); break;
					case '#': fireDTMF("#"); break;
				}
			}
		};
		dtmfComposite.addKeyListener(kl);
		
		gl = new GridLayout();
		gl.numColumns=3;
		gl.makeColumnsEqualWidth=true;
		dtmfComposite.setLayout(gl);
		
		one = new Button(dtmfComposite, SWT.PUSH);
		one.setText("1");
		one.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("1");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		one.setLayoutData(gd);
		one.addKeyListener(kl);
		
		two = new Button(dtmfComposite, SWT.PUSH);
		two.setText("2");
		two.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("2");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		two.setLayoutData(gd);
		two.addKeyListener(kl);
		
		three = new Button(dtmfComposite, SWT.PUSH);
		three.setText("3");
		three.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("3");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		three.setLayoutData(gd);
		three.addKeyListener(kl);
		
		four = new Button(dtmfComposite, SWT.PUSH);
		four.setText("4");
		four.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("4");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		four.setLayoutData(gd);
		four.addKeyListener(kl);
		
		five = new Button(dtmfComposite, SWT.PUSH);
		five.setText("5");
		five.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("5");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		five.setLayoutData(gd);
		five.addKeyListener(kl);
		
		six = new Button(dtmfComposite, SWT.PUSH);
		six.setText("6");
		six.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("6");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		six.setLayoutData(gd);
		six.addKeyListener(kl);
		
		seven = new Button(dtmfComposite, SWT.PUSH);
		seven.setText("7");
		seven.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("7");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		seven.setLayoutData(gd);
		seven.addKeyListener(kl);
		
		eight = new Button(dtmfComposite, SWT.PUSH);
		eight.setText("8");
		eight.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("8");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		eight.setLayoutData(gd);
		eight.addKeyListener(kl);
		
		nine = new Button(dtmfComposite, SWT.PUSH);
		nine.setText("9");
		nine.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("9");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		nine.setLayoutData(gd);
		nine.addKeyListener(kl);
		
		star = new Button(dtmfComposite, SWT.PUSH);
		star.setText("*");
		star.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("*");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		star.setLayoutData(gd);
		star.addKeyListener(kl);
		
		zero = new Button(dtmfComposite, SWT.PUSH);
		zero.setText("0");
		zero.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("0");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		zero.setLayoutData(gd);
		zero.addKeyListener(kl);
		
		pound = new Button(dtmfComposite, SWT.PUSH);
		pound.setText("#");
		pound.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireDTMF("#");
			}
		});
		gd = new GridData();
		gd.horizontalAlignment=GridData.FILL;
		pound.setLayoutData(gd);
		pound.addKeyListener(kl);
		
		txtComposite = new Group(userInputComposite, SWT.NONE);
		txtComposite.setText("Text");
		gl = new GridLayout();
		gl.numColumns = 1;
		txtComposite.setLayout(gl);
		gd = new GridData();
		txtComposite.setLayoutData(gd);
		
		input = new Text(txtComposite,SWT.SINGLE|SWT.BORDER);
		input.setText("");
		gd = new GridData();
		input.setLayoutData(gd);
		
		say = new Button(txtComposite,SWT.PUSH);
		say.setText("say");
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		say.setLayoutData(gd);
		
		say.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				fireTEXT(input.getText());
				input.setText("");
			}
		});

		
		logComposite = new Group(parent, SWT.NONE);
		logComposite.setText("Log");
		gl = new GridLayout();
		logComposite.setLayout(gl);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace=true;
		gd.grabExcessVerticalSpace=true;
		gd.verticalAlignment=GridData.FILL;
		logComposite.setLayoutData(gd);
		
		logTable = new Table(logComposite, SWT.SINGLE|SWT.FULL_SELECTION);
		logTable.setLinesVisible(true);
		logTable.setHeaderVisible(true);
		gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace=true;
		gd.grabExcessVerticalSpace=true;
		logTable.setLayoutData(gd);
		
		logTable.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				if (1==1) {
					return;
				}
				super.mouseDoubleClick(e);
				int idx = logTable.getSelectionIndex();
				if (idx == -1) {
					return;
				}
				TableItem ti = logTable.getItem(idx);
				String text = ti.getText(1);
				if (text == null || text.trim().length() == 0) {
					return;
				}
				StringTokenizer st = new StringTokenizer(text);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					try {
						URL u = new URL(token);
						String path = u.getPath();
						IResource res = ResourcesPlugin.getWorkspace().getRoot().getProject("foo").getFolder("."); // TODO finish
						if (res != null && res instanceof IFile) {
							IFile file = (IFile)res;
							FileEditorInput fei = new FileEditorInput(file);
                            IEditorDescriptor id = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());

                            getSite().getWorkbenchWindow().getActivePage().openEditor(fei, id.getId());
						}
					} catch (Exception ignoredException) {
						// Eat exception
					}
				}
				
			}
		});
		
		TableColumn tc1 = new TableColumn(logTable, SWT.NONE);
		tc1.setText("Time");
		tc1.setWidth(100);

		TableColumn tc2 = new TableColumn(logTable, SWT.NONE);
		tc2.setText("Message");
		tc2.setWidth(700);
		
		
		
		try {
			ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
			if (launches != null) {
				for (int i = 0 ; i < launches.length;i++) {
					IProcess[] t = launches[i].getProcesses();
					if (t != null && t.length > 0 && t[0] instanceof VoiceXMLBrowserProcess) {
						activeBrowsers.add(t[0]);
						if (activeBrowser == null) {
							activeBrowser = (VoiceXMLBrowserProcess)t[0];
							clearLogTable();
						}
					}
				}
			}
			updateUI();
			
			DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
				public void handleDebugEvents(DebugEvent[] events) {
					if (events==null) {
						return;
					}
					for (int i =0 ; i < events.length;i++) {
						if (events[i]==null) {
							continue;
						}
						if (events[i].getSource() instanceof IVoiceXMLBrowser) {
							IVoiceXMLBrowser browser = (IVoiceXMLBrowser)events[i].getSource();
							VoiceXMLBrowserProcess p = browser.getProcess();
							if (p == null || p != activeBrowser) {
								return;
							}

							if (events[i].getDetail() == IVoiceXMLBrowserConstants.EVENT_LOG_MESSAGE) {
								VoiceXMLLogMessage message = (VoiceXMLLogMessage)events[i].getData();
								if (message == null) {
									return;
								}
								String date = "";
								if (message.getDate() != null) {
									date = formatter.format(message.getDate());
								}
								final String fDate = date;
								final String fMessage = message.getMessage();
								
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {
										if (logTable != null) {
											TableItem ti = new TableItem(logTable, SWT.NONE);
											ti.setText(0, fDate);
											ti.setText(1, fMessage);
											logTable.showItem(ti);
										}
									}
								});							
							}
						}
						if (events[i].getKind() == DebugEvent.TERMINATE && events[i].getSource() instanceof VoiceXMLBrowserProcess) {
							VoiceXMLBrowserProcess p = (VoiceXMLBrowserProcess)events[i].getSource();
							
							activeBrowsers.remove(p);
							if (activeBrowser != null && p.equals(activeBrowser)) {
								activeBrowser=null;
							}
							updateUI();
						}
					}
				}
			});
			
			/**
			 * When a launch is added, updated, or removed, a browser has to be chosen if none is already
			 *   selected and the UI has to be refreshed to reflect the input view's current state. 
			 */
			DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new ILaunchListener() {
				public void launchRemoved(ILaunch launch) {
					IProcess[] t = launch.getProcesses();
					if (t != null && t.length > 0 && t[0] instanceof VoiceXMLBrowserProcess) {
						activeBrowsers.remove(t[0]);
						if (activeBrowser != null && t[0].equals(activeBrowser)) {
							activeBrowser=null;
							if (activeBrowsers.size() > 0) {
								activeBrowser=(VoiceXMLBrowserProcess)activeBrowsers.get(0);
								clearLogTable();
							}
						}
					}
					updateUI();
				}

				public void launchAdded(ILaunch launch) {
					IProcess[] t = launch.getProcesses();
					if (t != null && t.length > 0 && t[0] instanceof VoiceXMLBrowserProcess) {
						activeBrowsers.add(t[0]);
						if (activeBrowser==null) {
							activeBrowser=(VoiceXMLBrowserProcess)t[0];
							clearLogTable();
						}
					}
					updateUI();
				}

				public void launchChanged(ILaunch launch) {
					IProcess[] t = launch.getProcesses();
					if (t != null && t.length > 0 && t[0] instanceof VoiceXMLBrowserProcess) {
						activeBrowsers.add(t[0]);
						if (activeBrowser==null) {
							activeBrowser=(VoiceXMLBrowserProcess)t[0];
							clearLogTable();
						}
					}
					updateUI();
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void setFocus() {
		if (dtmfComposite != null) {
			dtmfComposite.setFocus();
		}
	}

}
