//
// GeoMapSet.java
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

import java.lang.*;
import java.util.*;


public class GeoMapSet 
{
  Vector mapSet;
  
  public GeoMapSet() 
  {
    mapSet = new Vector(); 
  }

  public void add( GeoMap obj ) 
  {
    mapSet.addElement( obj );
  }

  public int getSize() 
  {
    int size = mapSet.size();
    return size;
  }

  public GeoMap getElement( int ii )  
  {
    if ( mapSet.size() == 0 ) 
    {
      return null;
    }
    else 
    {
      GeoMap obj = (GeoMap) mapSet.elementAt(ii);
      return obj;
    }
  }

  public GeoMap getGeoMap( NamedDimension obj ) 
  {
    String name = obj.getName();
    return getGeoMap( name );
  }

  public GeoMap getGeoMap( String name ) 
  {
    int size = this.getSize();

    if ( size == 0 ) 
    {
      return null;
    }
    else 
    {
      for ( int ii = 0; ii < size; ii++ ) 
      {
         GeoMap obj = (GeoMap) mapSet.elementAt(ii);

         if(( obj.toDim.equals( name ) ) || ( obj.fromDim.equals( name ) )) 
         {
           return obj;
         }
      }
      return null;
    }
  }
}
