
//
// ThingImpl.java
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
   ThingImpl is the abstract superclass of the VisAD objects that send
   ThingChangedEvents to Actions.<p>
*/
public abstract class ThingImpl
       implements Thing, java.io.Serializable {

  class RemotePair {
    RemoteThingReference ref;
    RemoteThing data;
   
    RemotePair(RemoteThingReference r, RemoteThing d) {
      ref = r;
      data = d;
    }
   
    public boolean equals(Object pair) {
      if (!(pair instanceof RemotePair)) return false;
      return (ref.equals(((RemotePair) pair).ref) &&
              data.equals(((RemotePair) pair).data));
    }
  }

  /** vector of ThingReference that reference this Thing object */
  private transient Vector references = new Vector();

  public ThingImpl() {
  }

  /** add a ThingReference to this ThingImpl;
      must be local ThingReferenceImpl;
      called by ThingReference.setThing;
      would like 'default' visibility here, but must be declared
      'public' because it is defined in the Thing interface */
  public void addReference(ThingReference r) throws VisADException {
    if (!(r instanceof ThingReferenceImpl)) {
      throw new RemoteVisADException("ThingImpl.addReference: must use " +
                                     "ThingReferenceImpl");
    }
    synchronized (this) {
      if (references == null) references = new Vector();
    }
    references.addElement(r);
/* DEBUG
    System.out.println("ThingImpl " + " addReference " +
                       "(" + System.getProperty("os.name") + ")");
*/
  }

  /** method for use by RemoteThingImpl that adapts this ThingImpl */
  void adaptedAddReference(RemoteThingReference r, RemoteThing t)
        throws VisADException {
    synchronized (this) {
      if (references == null) references = new Vector();
    }
    references.addElement(new RemotePair(r, t));
/* DEBUG
    System.out.println("ThingImpl.adaptedAddReference " +
                       "(" + System.getProperty("os.name") + ")");
*/
  }

  /** remove a ThingReference to this ThingImpl;
      must be local ThingReferenceImpl;
      called by ThingReference.setThing;
      would like 'default' visibility here, but must be declared
      'public' because it is defined in the Thing interface */
  public void removeReference(ThingReference r)
         throws VisADException {
    if (!(r instanceof ThingReferenceImpl)) {
      throw new RemoteVisADException("ThingImpl.removeReference: must use " +
                                     "RemoteThing for RemoteThingReference");
    }
    if (references == null || !references.removeElement(r)) {
      throw new ReferenceException("ThingImpl.removeReference: already clear");
    }
  }

  /** method for use by RemoteThingImpl that adapts this ThingImpl */
  void adaptedRemoveReference(RemoteThingReference r, RemoteThing t)
       throws VisADException {
    if (references == null || !references.removeElement(new RemotePair(r, t))) {
      throw new ReferenceException("ThingImpl.removeReference: already clear");
    }
  }

  /** notify local ThingReferenceImpl-s that this ThingImpl has changed; 
      incTick in RemoteThingImpl for RemoteThingReferenceImpl-s;
      would like 'default' visibility here, but must be declared
      'public' because it is defined in the Thing interface */
  public void notifyReferences()
         throws VisADException, RemoteException {
    if (references != null) {
      // lock references for iterating through it
      synchronized (references) {
        Enumeration refs = references.elements();
        while (refs.hasMoreElements()) {
          Object r = refs.nextElement();
          if (r instanceof ThingReferenceImpl) {
            // notify local ThingReferenceImpl
            ((ThingReferenceImpl) r).incTick();
          }
          else { // r instanceof RemotePair
            // RemoteThingReference, so only incTick in
            // local RemoteThingImpl
            RemoteThing d = ((RemotePair) r).data;
            d.incTick();
          }
        }
      }
    }
  }

}

