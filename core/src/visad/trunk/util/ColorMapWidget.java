/*

@(#) $Id: ColorMapWidget.java,v 1.13 1998-12-21 16:12:20 billh Exp $

VisAD Utility Library: Widgets for use in building applications with
the VisAD interactive analysis and visualization library
Copyright (C) 1998 Nick Rasmussen
VisAD is Copyright (C) 1996 - 1998 Bill Hibbard, Curtis Rueden, Tom
Rink and Dave Glowacki.
 
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 1, or (at your option)
any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License in file NOTICE for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/

package visad.util;

import visad.*;
import java.rmi.RemoteException;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Vector;

import javax.swing.*;

/** 
 * A color widget that allows users to interactively map numeric data to
 * RGB tuples based on the Vis5D color widget
 *
 * @author Nick Rasmussen nick@cae.wisc.edu
 * @version $Revision: 1.13 $, $Date: 1998-12-21 16:12:20 $
 * @since Visad Utility Library v0.7.1
 */
public class LabeledRGBWidget extends Panel implements ActionListener,
                                                       ColorChangeListener,
                                                       ScalarMapListener {

  private final int TABLE_SIZE;
  private final float SCALE;

  private ArrowSlider slider;

  private ColorWidget widget;

  private SliderLabel label;

  private float[][] orig_table;

  ColorControl colorControl;

  /** construct a LabeledRGBWidget linked to the ColorControl
      in map (which must be to Display.RGB), with auto-scaling range */
  public LabeledRGBWidget(ScalarMap smap) throws VisADException,
                                                 RemoteException {
    this(smap, Float.NaN, Float.NaN, null, true);
  }

  /** construct a LabeledRGBWidget linked to the ColorControl
      in map (which must be to Display.RGB), with auto-scaling
      range of values (min, max) */
  public LabeledRGBWidget(ScalarMap smap, float min, float max)
                          throws VisADException, RemoteException {
    this(smap, min, max, null, true);
  }

  /** construct a LabeledRGBWidget linked to the ColorControl
      in map (which must be to Display.RGB), with auto-scaling range
      of values (min, max), and initial color table in format
      float[TABLE_SIZE][3] with values between 0.0f and 1.0f */
  public LabeledRGBWidget(ScalarMap smap, float min, float max,
         float[][] table) throws VisADException, RemoteException {
    this(smap, min, max, table, true);
  }

  /** construct a LabeledRGBWidget linked to the ColorControl
      in map (which must be to Display.RGB), with range of
      values (min, max), initial color table in format
      float[TABLE_SIZE][3] with values between 0.0f and 1.0f, and
      specified auto-scaling min and max behavior */
  public LabeledRGBWidget(ScalarMap smap, float min, float max,
                          float[][] in_table, boolean update)
                          throws VisADException, RemoteException {
    // verify scalar map
    if (!Display.RGB.equals(smap.getDisplayScalar())) {
      throw new DisplayException("LabeledRGBWidget: ScalarMap must " +
                                 "be to Display.RGB");
    }
    colorControl = (ColorControl) smap.getControl();
    String name = smap.getScalar().getName();
    float[][] table = table_reorg(in_table);
 
    // set up user interface
    ColorWidget c = new ColorWidget(new RGBMap(table));
    ArrowSlider s = new ArrowSlider(min, max, (min + max) / 2, name);
    SliderLabel l = new SliderLabel(s);
    widget = c;
    slider = s;
    label = l;
    Button reset = new Button("Reset") {
      public Dimension getMinimumSize() {
        return new Dimension(0, 18);
      }
      public Dimension getPreferredSize() {
        return new Dimension(0, 18);
      }
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 18);
      }
    };
    reset.setActionCommand("reset");
    reset.addActionListener(this);
    Button grey = new Button("Grey Scale") {
      public Dimension getMinimumSize() {
        return new Dimension(0, 18);
      }
      public Dimension getPreferredSize() {
        return new Dimension(0, 18);
      }
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 18);
      }
    };
    grey.setActionCommand("grey");
    grey.addActionListener(this);
    Panel panel = new Panel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(reset);
    panel.add(grey);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(widget);
    add(slider);
    add(label);
    add(panel);

    // enable auto-scaling
    if (update) smap.addScalarMapListener(this);
    else {
      smap.setRange(min, max);
      updateWidget(min, max);
    }

    // set up color table
    ColorMap map = widget.getColorMap();
    TABLE_SIZE = map.getMapResolution(); 
    SCALE = 1.0f / (TABLE_SIZE - 1.0f);

    if (table == null) {
      table = new float[3][TABLE_SIZE];
 
      for (int i=0; i<TABLE_SIZE; i++) {
        float[] t = map.getRGBTuple(SCALE * i);
        table[0][i] = t[0];
        table[1][i] = t[1];
        table[2][i] = t[2];
      }
      colorControl.setTable(table);
      orig_table = copy_table(table);
    }
    else {
      colorControl.setTable(in_table);
      orig_table = copy_table(in_table);
    }
    widget.addColorChangeListener(this);
  }

  private Dimension maxSize = null;

  public Dimension getMaximumSize() {
    if (maxSize != null) return maxSize;
    else return super.getMaximumSize();
  }

  public void setMaximumSize(Dimension size) {
    maxSize = size;
  }

  private void updateWidget(float min, float max) {
    float val = slider.getValue();
    if (val <= min || val >= max) val = (min+max)/2;
    slider.setBounds(min, max, val);
  }

  /** ScalarMapListener method used with delayed auto-scaling */
  public void mapChanged(ScalarMapEvent e) {
    ScalarMap s = e.getScalarMap();
    double[] range = s.getRange();
    updateWidget((float) range[0], (float) range[1]);
  }

  /** ColorChangeListener method */
  public void colorChanged(ColorChangeEvent e) {
    ColorMap map_e = widget.getColorMap();
    float[][] table_e = new float[3][TABLE_SIZE];
    for (int i=0; i<TABLE_SIZE; i++) {
      float[] t = map_e.getRGBTuple(SCALE * i);
      table_e[0][i] = t[0];
      table_e[1][i] = t[1];
      table_e[2][i] = t[2];
    }
    try {
      colorControl.setTable(table_e);
    }
    catch (VisADException f) { }
    catch (RemoteException f) { }
  }

  /** ActionListener method used with resetting color table */
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("reset")) {
      // reset color table to original values
      try {
        float[][] table = copy_table(orig_table);
        colorControl.setTable(table);
        ((RGBMap) widget.getColorMap()).setValues(table_reorg(table));
      }
      catch (VisADException exc) { }
      catch (RemoteException exc) { }
    }
    else if (e.getActionCommand().equals("grey")) {
      // reset color table to grey wedge
      try {
        float[][] table = copy_table(orig_table);
        float a = 1.0f / (table[0].length - 1.0f);
        for (int j=0; j<table[0].length; j++) {
            table[0][j] = j * a;
            table[1][j] = j * a;
            table[2][j] = j * a;
        }
        colorControl.setTable(table);
        ((RGBMap) widget.getColorMap()).setValues(table_reorg(table));
      }
      catch (VisADException exc) { }
      catch (RemoteException exc) { }
    }
  }

  private static float[][] copy_table(float[][] table) {
    if (table == null || table[0] == null) return null;
    int len = table[0].length;
    float[][] new_table = new float[3][len];
    try {
      for (int i=0; i<3; i++) {
        System.arraycopy(table[i], 0, new_table[i], 0, len);
      }
      return new_table;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  private static float[][] table_reorg(float[][] table) {
    if (table == null || table[0] == null) return null;
    try {
      int len = table[0].length;
      float[][] out = new float[len][3];
      for (int i=0; i<len; i++) {
        out[i][0] = table[0][i];
        out[i][1] = table[1][i];
        out[i][2] = table[2][i];
      }
      return out;
    }
    catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  /** Returns the ColorMap that the color widget is currently pointing to */
  public ColorWidget getColorWidget() {
    return widget;
  }
  
}

