
//
// Thing.java
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

package visad;

import java.util.*;
import java.rmi.*;

/**
   Thing is the top-level interface for objects that send
   ThingChangedEvents to Actions.<p>
*/
public interface Thing {

  /** add a ThingReference to this Thing object */
  public void addReference(ThingReference r)
         throws VisADException, RemoteException;

  /** remove a ThingReference from this Thing object */
  public void removeReference(ThingReference r)
         throws VisADException, RemoteException;
}

