//
// HdfeosDomainMap.java
//

/*
VisAD system for interactive analysis and visualization of numerical
data.  Copyright (C) 1996 - 1998 Bill Hibbard, Curtis Rueden, Tom
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

package visad.data.hdfeos;

import java.util.*;
import java.lang.*;
import visad.Set;
import visad.MathType;
import visad.Gridded1DSet;
import visad.RealTupleType;
import visad.VisADException;
import visad.Unit;

public class HdfeosDomainMap extends HdfeosDomain 
{
  private GctpMap gridMap;
  private Set set = null;

  public HdfeosDomainMap( EosStruct struct, 
                          DimensionSet dimSet,
                          GctpMap gridMap  ) 
         throws VisADException
  {
    super(struct, dimSet, gridMap.getVisADCoordinateSystem(),
                          gridMap.getUnits());
    this.gridMap = gridMap;
  }

  public Set getData( ) 
         throws VisADException 
  {
    set = gridMap.getVisADSet(mathtype);
    return set;
  }

  public Set getData( int[] indexes )
         throws VisADException
  {
    if ( set == null ) 
    {
      set = gridMap.getVisADSet(mathtype);
    }
    return set;
  }
}
