package de.wannawork.jcalendar;

/*
 * Copyright (c) 2003, Bodo Tasche (http://www.wannawork.de) All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * This Class creates a ComboBox for selecting the Date. If pressed, it shows a
 * Popup that contains a JCalendarPanel.
 * 
 * You can add a ChangeListener to this ComboBox to receive change events.
 * 
 * It is possible to change the Text on the ComboBox using the
 * DateFormat-Parameter.
 * 
 * @author Bodo Tasche, David Freels
 */
public class JCalendarComboBox extends JPanel implements ActionListener,
        AncestorListener, ChangeListener, SwingConstants {

    /**
     * Creates a Calendar using the current Date and current Local settings.
     */
    public JCalendarComboBox() {
        _calendarPanel = new JCalendarPanel();
        createGUI();
    }

    /**
     * Creates a Calendar using the cal-Date and current Locale Settings. It
     * doesn't use the Locale in the Calendar-Object !
     * 
     * @param cal
     *            Calendar to use
     */
    public JCalendarComboBox(Calendar cal) {
        _calendarPanel = new JCalendarPanel(cal);
        createGUI();
    }

    /**
     * Creates a Calendar using the current Date and the given Locale Settings.
     * 
     * @param locale
     *            Locale to use
     */
    public JCalendarComboBox(Locale locale) {
        _calendarPanel = new JCalendarPanel(locale);
        createGUI();
    }

    /**
     * Creates a Calender using the given Date and Locale
     * 
     * @param cal
     *            Calendar to use
     * @param locale
     *            Locale to use
     */
    public JCalendarComboBox(Calendar cal, Locale locale) {
        _calendarPanel = new JCalendarPanel(cal, locale);
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     * 
     * @param cal
     *            Calendar to use
     * @param locale
     *            Locale to use
     * @param dateFormat
     *            DateFormat for the ComboBox
     */
    public JCalendarComboBox(Calendar cal, Locale locale, DateFormat dateFormat) {
        _calendarPanel = new JCalendarPanel(cal, locale, dateFormat);
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     * 
     * @param cal
     *            Calendar to use
     * @param locale
     *            Locale to use
     * @param dateFormat
     *            DateFormat for the ComboBox
     * @param location
     *            Location of the Popup (LEFT, CENTER or RIGHT)
     */
    public JCalendarComboBox(Calendar cal, Locale locale,
            DateFormat dateFormat, int location) {
        _calendarPanel = new JCalendarPanel(cal, locale, dateFormat);
        _popupLocation = location;
        createGUI();
    }

    /**
     * Creates a Calender using the given Calendar, Locale and DateFormat.
     * 
     * @param cal
     *            Calendar to use
     * @param locale
     *            Locale to use
     * @param dateFormat
     *            DateFormat for the ComboBox
     * @param location
     *            Location of the Popup (LEFT, CENTER or RIGHT)
     * @param flat
     *            Flat Buttons for next/last Month/Year
     */
    public JCalendarComboBox(Calendar cal, Locale locale,
            DateFormat dateFormat, int location, boolean flat) {
        _calendarPanel = new JCalendarPanel(cal, locale, dateFormat, flat);
        _popupLocation = location;
        createGUI();
    }

    /**
     * Creates the GUI for the JCalendarComboBox
     */
    private void createGUI() {
        _calendarPanel.setListenerModus(JCalendarPanel.FIRE_DAYCHANGES);
        _selected = (Calendar) _calendarPanel.getCalendar().clone();

        _calendarPanel.addChangeListener(this);
        _calendarPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        setLayout(new BorderLayout());

        _spinner.setModel(new SpinnerDateModel());
        _spinner
                .setEditor(new JSpinner.DateEditor(_spinner,
                        ((SimpleDateFormat) _calendarPanel.getDateFormat())
                                .toPattern()));
        _spinner.getModel().setValue(_selected.getTime());
        _spinner.setBorder(null);
        
        ((JSpinner.DefaultEditor )_spinner.getEditor ()).getTextField().addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
               // System.out.println("Focus lost!!");
                if (!_calendarPanel.getCalendar().getTime().equals(_spinner.getModel().getValue())) {
                    Date date = (Date) _spinner.getModel().getValue();
                    _selected.setTime(date);
                    _calendarPanel.setCalendar(_selected);
                    fireChangeEvent();
                }
            }
        });
        

        _button = new BasicArrowButton(BasicArrowButton.SOUTH);

        Insets insets = new Insets(_button.getMargin().top, 0, _button
                .getMargin().bottom, 0);
        _button.setMargin(insets);
        _button.addActionListener(this);
        _button.setEnabled(true);

        _button.addFocusListener(new FocusAdapter() {

            /**
             * Invoked when a component gains the keyboard focus.
             */
            public void focusGained(FocusEvent e) {
                if (e.getOppositeComponent() != null) {
                    if (e.getOppositeComponent() instanceof JComponent) {
                        JComponent opposite = (JComponent) e
                                .getOppositeComponent();
                        if ((opposite.getTopLevelAncestor() != _calendarWindow)
                                && (!_calendarWindowFocusLost))
                                _calendarWindowFocusLost = false;
                    }
                }
            }
        });

        add(_spinner, BorderLayout.CENTER);
        add(_button, BorderLayout.EAST);
    }

    /**
     * Creates the CalendarWindow-Popup
     */
    private void createCalendarWindow() {
        Window ancestor = (Window) this.getTopLevelAncestor();

        _calendarWindow = new JWindow(ancestor);

        JPanel contentPanel = (JPanel) _calendarWindow.getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(_calendarPanel);

        ((JComponent) ((RootPaneContainer) ancestor).getContentPane())
                .addAncestorListener(this);

        ((JComponent) ((RootPaneContainer) ancestor).getContentPane())
                .addMouseListener(new MouseAdapter() {

                    public void mouseClicked(MouseEvent e) {
                        hideCalendar();
                    }
                });

        _calendarWindow.addWindowListener(new WindowAdapter() {

            public void windowDeactivated(WindowEvent e) {
                hideCalendar();
            }
        });

        _calendarWindow.addWindowFocusListener(new WindowAdapter() {

            public void windowLostFocus(WindowEvent e) {
                if (_button.isSelected()) {
                    _calendarWindowFocusLost = true;
                }

                hideCalendar();
            }
        });

        ancestor.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                hideCalendar();
            }

            public void componentMoved(ComponentEvent e) {
                hideCalendar();
            }

            public void componentShown(ComponentEvent e) {
                hideCalendar();
            }

            public void componentHidden(ComponentEvent e) {
                hideCalendar();
            }
        });
        ComponentListener list;

        _calendarWindow.pack();
    }

    /**
     * Returns the current seleted Date as Calendar
     * 
     * @return current selected Date
     */
    public Calendar getCalendar() {
        return _calendarPanel.getCalendar();
    }

    /**
     * Sets the current selected Date
     * 
     * @param cal
     *            Date to select
     */
    public void setCalendar(Calendar cal) {
        _calendarPanel.setCalendar(cal);
        _spinner.getModel().setValue(_calendarPanel.getCalendar().getTime());
    }

    /**
     * Returns the JCalendarPanel that is shown in the PopUp
     * 
     * @return JCalendarPanel in the PopUp
     */
    public JCalendarPanel getCalendarPanel() {
        return _calendarPanel;
    }

    /**
     * Sets the Popup Location (Left/Right/Center)
     * 
     * @param location
     */
    public void setPopUpLocation(int location) {
        _popupLocation = location;
    }

    /**
     * Returns the Popup Location
     * 
     * @return Location of the Popup
     */
    public int getPopUpLocation() {
        return _popupLocation;
    }

    /**
     * In earlier Versions it sets the vertical Position of the Text in the Button,
     * but it is not longer used!
     * 
     * @deprecated does nothing!!
     * @param value nothing
     */
    public void setVerticalAlignment(int value) {
    }

    /**
     * Sets the horizontal Position of the Text in the Button
     * 
     * @param value
     *            RIGHT, LEFT, CENTER, LEADING, TRAILING for horizontal
     *            Alignment
     */
    public void setHorizontalAlignment(int value) {
        ((JSpinner.DefaultEditor )_spinner.getEditor ()).getTextField().setHorizontalAlignment(value);
    }

    /**
     * Handles the ToggleButton events for hiding / showing the PopUp
     * 
     * @param e
     *            the ActionEvent
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if ((_calendarWindow != null) && (_calendarWindow.isVisible())) {
            hideCalendar();
        } else {
            showCalender();
        }
    }

    /**
     * Hides the Calendar PopUp and fires a ChangeEvent if a change was made
     */
    public void hideCalendar() {
        if (_calendarWindow.isVisible()) {
            _calendarWindow.setVisible(false);

            // TODO!!
            if (!_calendarPanel.getCalendar().getTime().equals(_spinner.getModel().getValue())) {
                _changed = true;
            }

            if (_changed) {

                _spinner.getModel().setValue(
                        _calendarPanel.getCalendar().getTime());
                _selected = (Calendar) _calendarPanel.getCalendar().clone();
                _changed = false;
                fireChangeEvent();
            }
        }
    }

    /**
     * Shows the Calendar PopUp
     */
    public void showCalender() {
        Window ancestor = (Window) this.getTopLevelAncestor();

        if ((_calendarWindow == null)
                || (ancestor != _calendarWindow.getOwner())) {
            createCalendarWindow();
        }

        //Update the datefrom the spinner model
        Date date = (Date) _spinner.getModel().getValue();
        _selected.setTime(date);
        _calendarPanel.setCalendar(_selected);

        Point location = getLocationOnScreen();

        int x;

        if (_popupLocation == RIGHT) {
            x = (int) location.getX() + _button.getSize().width
                    - _calendarWindow.getSize().width;
        } else if (_popupLocation == CENTER) {
            x = (int) location.getX()
                    + ((_button.getSize().width - _calendarWindow.getSize().width) / 2);
        } else {
            x = (int) location.getX();
        }

        int y = (int) location.getY() + _button.getHeight();

        Rectangle screenSize = getDesktopBounds();

        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }

        if (x + _calendarWindow.getWidth() > screenSize.width) {
            x = screenSize.width - _calendarWindow.getWidth();
        }

        if (y + 30 + _calendarWindow.getHeight() > screenSize.height) {
            y = (int) location.getY() - _calendarWindow.getHeight();
        }

        _calendarWindow.setBounds(x, y, _calendarWindow.getWidth(),
                _calendarWindow.getHeight());
        _calendarWindow.setVisible(true);
    }

    /**
     * Gets the screensize. Takes into account multi-screen displays.
     * 
     * @return a union of the bounds of the various screen devices present
     */
    private Rectangle getDesktopBounds() {
        final GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        final GraphicsDevice[] gd = ge.getScreenDevices();
        final Rectangle[] screenDeviceBounds = new Rectangle[gd.length];
        Rectangle desktopBounds = new Rectangle();
        for (int i = 0; i < gd.length; i++) {
            final GraphicsConfiguration gc = gd[i].getDefaultConfiguration();
            screenDeviceBounds[i] = gc.getBounds();
            desktopBounds = desktopBounds.union(screenDeviceBounds[i]);
        }

        return desktopBounds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.AncestorListener#ancestorAdded(javax.swing.event.AncestorEvent)
     */
    public void ancestorAdded(AncestorEvent event) {
        hideCalendar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.AncestorListener#ancestorMoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorMoved(AncestorEvent event) {
        hideCalendar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.AncestorListener#ancestorRemoved(javax.swing.event.AncestorEvent)
     */
    public void ancestorRemoved(AncestorEvent event) {
        hideCalendar();
    }

    /**
     * Listens to ChangeEvents of the JCalendarPanel and rembers if something
     * was changed.
     * 
     * If the Day was changed, the PopUp is closed.
     * 
     * @param e
     *            ChangeEvent
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        _changed = true;
        hideCalendar();
    }

    /**
     * Adds a Changelistener to this JCalendarComboBox.
     * 
     * It will be called everytime the ComboBox is closed and the Date was
     * changed
     * 
     * @param listener
     *            ChangeListener
     */
    public void addChangeListener(ChangeListener listener) {
        _changeListener.add(listener);
    }

    /**
     * Removes a ChangeListener from this JCalendarComboBox
     * 
     * @param listener
     *            listener to remove
     */
    public void removeChangeListener(ChangeListener listener) {
        _changeListener.remove(listener);
    }

    /**
     * Gets all ChangeListeners
     * 
     * @return all ChangeListeners
     */
    public ChangeListener[] getChangeListener() {
        return (ChangeListener[]) _changeListener.toArray();
    }

    /**
     * Fires the ChangeEvent
     */
    protected void fireChangeEvent() {
        if (!_fireingChangeEvent) {
            _fireingChangeEvent = true;
            ChangeEvent event = new ChangeEvent(this);

            for (int i = 0; i < _changeListener.size(); i++) {
                ((ChangeListener) _changeListener.get(i)).stateChanged(event);
            }

            _fireingChangeEvent = false;
        }

    }

    /**
     * Enables/Disables the ComboBox
     * 
     * @param enabled
     *            Enabled ?
     */
    public void setEnabled(boolean enabled) {
        _spinner.setEnabled(enabled);
        _button.setEnabled(enabled);
    }

    /**
     * Is the ComboBox enabled ?
     * 
     * @return enabled ?
     */
    public boolean isEnabled() {
        return _button.isEnabled();
    }

    /**
     * Gets the Popup Location
     * 
     * @return location of the Popup
     */
    public int getPopupLocation() {
        return _popupLocation;
    }

    /**
     * Sets the Location of the Popup (LEFT, CENTER or RIGHT)
     * 
     * @param location
     *            Location of the PopUp
     */
    public void setPopupLocation(int location) {
        _popupLocation = location;
    }

    /**
     * Returns the model used to hold the Date object
     * 
     * @return
     */
    public SpinnerDateModel getModel() {
        return (SpinnerDateModel) _spinner.getModel();
    }

    /**
     * Sets the model used to hold the Date object
     * 
     * @param model
     *            A SpinnerDateModel to be used by the spinner control.
     */
    public void setSpinnerDateModel(SpinnerDateModel model) {
        _spinner.setModel(model);
    }

    /**
     * Where should be the Popup?
     */
    private int _popupLocation = LEFT;

    /**
     * If the Window looses Focus and the ToggleButton get's it, don't popup
     * the Window again...
     */
    private boolean _calendarWindowFocusLost = false;

    /**
     * Current selected Day
     */
    private Calendar _selected;

    /**
     * The ToggleButton for hiding/showing the JCalendarPanel
     */
    private BasicArrowButton _button;

    /**
     * The text field that holds the date
     */
    private JSpinner _spinner = new JSpinner();

    /**
     * The JWindow for the PopUp
     */
    private JWindow _calendarWindow;

    /**
     * The JCalendarPanel inside the PopUp
     */
    private JCalendarPanel _calendarPanel;

    /**
     * The list of ChangeListeners
     */
    private ArrayList _changeListener = new ArrayList();

    /**
     * Currently firing an ChangeEvent?
     */
    private boolean _fireingChangeEvent = false;

    /**
     * Something changed in the JCalendarPanel ?
     */
    private boolean _changed = false;

}