//
// ColorToolPanel.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 2002 Bill Hibbard, Curtis Rueden, Tom
Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
Tommy Jasmin.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Library General Public
License as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Library General Public License for more details.

You should have received a copy of the GNU Library General Public
License along with this library; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA 02111-1307, USA
*/

package visad.bio;

import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import javax.swing.*;
import javax.swing.event.*;
import visad.*;
import visad.browser.Divider;
import visad.util.Util;

/**
 * ColorToolPanel is the tool panel for
 * adjusting viewing parameters.
 */
public class ColorToolPanel extends ToolPanel implements ItemListener {

  // -- CONSTANTS --

  private static final DisplayRealType[] COLOR_TYPES = {
    Display.Red, Display.Green, Display.Blue, Display.RGB
  };


  // -- GUI COMPONENTS --

  /** Label for brightness. */
  private JLabel brightnessLabel;

  /** Slider for level of brightness. */
  private JSlider brightness;

  /** Label for current brightness value. */
  private JLabel brightnessValue;

  /** Label for contrast. */
  private JLabel contrastLabel;

  /** Slider for level of contrast. */
  private JSlider contrast;

  /** Label for current contrast value. */
  private JLabel contrastValue;

  /** Red color map widget. */
  private BioColorWidget red;

  /** Green color map widget. */
  private BioColorWidget green;

  /** Blue color map widget. */
  private BioColorWidget blue;

  /** Toggle for composite coloring. */
  private JCheckBox composite;

  /** Toggle for colorizing image stack based on slice level. */
  private JCheckBox colorize;


  // -- OTHER FIELDS --

  /** Should changes to the color components be ignored? */
  private boolean ignore = false;


  // -- CONSTRUCTOR --

  /** Constructs a tool panel for adjusting viewing parameters. */
  public ColorToolPanel(BioVisAD biovis) {
    super(biovis);

    // brightness label
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    brightnessLabel = new JLabel("Brightness: ");
    brightnessLabel.setForeground(Color.black);
    brightnessLabel.setAlignmentY(JLabel.TOP_ALIGNMENT);
    p.add(brightnessLabel);

    // brightness slider
    brightness = new JSlider(0, BioVisAD.COLOR_DETAIL,
      BioVisAD.NORMAL_BRIGHTNESS);
    brightness.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) { doColorTable(); }
    });
    brightness.setAlignmentY(JSlider.TOP_ALIGNMENT);
    p.add(brightness);

    // current brightness value
    brightnessValue = new JLabel("" + BioVisAD.NORMAL_BRIGHTNESS);
    Dimension colorValueSize =
      new JLabel("" + BioVisAD.COLOR_DETAIL).getPreferredSize();
    brightnessValue.setPreferredSize(colorValueSize);
    brightnessValue.setAlignmentY(JLabel.TOP_ALIGNMENT);
    p.add(brightnessValue);
    controls.add(pad(p));

    // contrast label
    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    contrastLabel = new JLabel("Contrast: ");
    contrastLabel.setForeground(Color.black);
    contrastLabel.setPreferredSize(brightnessLabel.getPreferredSize());
    contrastLabel.setAlignmentY(JLabel.TOP_ALIGNMENT);
    p.add(contrastLabel);

    // contrast slider
    contrast = new JSlider(0, BioVisAD.COLOR_DETAIL,
      BioVisAD.NORMAL_CONTRAST);
    contrast.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) { doColorTable(); }
    });
    contrast.setAlignmentY(JSlider.TOP_ALIGNMENT);
    contrast.setMajorTickSpacing(BioVisAD.COLOR_DETAIL / 4);
    contrast.setMinorTickSpacing(BioVisAD.COLOR_DETAIL / 16);
    contrast.setPaintTicks(true);
    p.add(contrast);

    // current contrast value
    contrastValue = new JLabel("" + BioVisAD.NORMAL_CONTRAST);
    contrastValue.setPreferredSize(colorValueSize);
    contrastValue.setAlignmentY(JLabel.TOP_ALIGNMENT);
    p.add(contrastValue);
    controls.add(pad(p));

    // spacing
    controls.add(Box.createVerticalStrut(5));

    // red color map widget
    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    red = new BioColorWidget(bio, BioColorWidget.RED);
    red.addItemListener(this);
    p.add(red);

    // green color map widget
    green = new BioColorWidget(bio, BioColorWidget.GREEN);
    green.addItemListener(this);
    p.add(green);

    // blue color map widget
    blue = new BioColorWidget(bio, BioColorWidget.BLUE);
    blue.addItemListener(this);
    p.add(blue);
    controls.add(pad(p));

    // composite checkbox
    composite = new JCheckBox("Composite image coloring", false);
    composite.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        boolean b = !composite.isSelected();
        red.setEnabled(b);
        green.setEnabled(b);
        blue.setEnabled(b);
        doColorTable();
      }
    });
    controls.add(pad(composite));

    // colorize across slice level checkbox
    colorize = new JCheckBox("Colorize image stack across slices", false);
    colorize.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) { doColorTable(); }
    });
    colorize.setEnabled(false);
    controls.add(pad(colorize));
  }


  // -- API METHODS --

  /** Enables or disables this tool panel. */
  public void setEnabled(boolean enabled) {
    brightnessLabel.setEnabled(enabled);
    brightness.setEnabled(enabled);
    contrastLabel.setEnabled(enabled);
    contrast.setEnabled(enabled);
  }


  // -- INTERNAL API METHODS --

  /** ItemListener method for handling color mapping changes. */
  public void itemStateChanged(ItemEvent e) { doColorTable(); }

  /** Updates image color table, for brightness and color adjustments. */
  void doColorTable() {
    if (ignore) return;
    int bright = brightness.getValue();
    int cont = contrast.getValue();
    boolean comp = composite.isSelected();
    bio.setImageColors(bright, cont, comp, red.getSelectedItem(),
      green.getSelectedItem(), blue.getSelectedItem());
    brightnessValue.setText("" + bright);
    contrastValue.setText("" + cont);
  }

  /** Chooses most desirable range types for color widgets. */
  void guessTypes() {
    red.removeItemListener(this);
    green.removeItemListener(this);
    blue.removeItemListener(this);
    red.guessType();
    green.guessType();
    blue.guessType();
    red.addItemListener(this);
    green.addItemListener(this);
    blue.addItemListener(this);
  }

  /** Updates color components to match those specified. */
  void setColors(int bright, int cont, RealType r, RealType g, RealType b) {
    ignore = true;
    brightness.setValue(bright);
    contrast.setValue(cont);
    red.setSelectedItem(r);
    green.setSelectedItem(g);
    blue.setSelectedItem(b);
    ignore = false;
    doColorTable();
  }

}