// Generated by Together

package visad.meteorology;

import java.util.Vector;
import visad.CoordinateSystem;
import visad.Display;
import visad.DisplayRealType;
import visad.DisplayTupleType;
import visad.Real;
import visad.SI;
import visad.Unit;
import visad.UnitException;
import visad.VisADException;
import visad.java2d.DefaultDisplayRendererJ2D;


public class
HodographDisplayRenderer2D
    extends DefaultDisplayRendererJ2D
{
    /**
     * The default unit of speed.
     */
    public static final Unit			DEFAULT_SPEED_UNIT = 
	CommonUnits.KNOT;

    /**
     * The default maximum speed.
     */
    public static final float			DEFAULT_MAX_SPEED = 50;

    /**
     * The U-component of velocity.
     */
    public static final DisplayRealType		U;

    /**
     * The V-component of velocity.
     */
    public static final DisplayRealType		V;

    /**
     * The display Z axis (dummy -- but necessary).
     */
    public static final DisplayRealType		Z;

    /**
     * The hodograph coordinate-system transform.
     */
    private static final HodographCoordSys	COORD_SYS;

    /**
     * The hodograph vector space.
     */
    private static final DisplayTupleType	DISPLAY_TUPLE_TYPE;


    static
    {
	DisplayRealType		u = null;
	DisplayRealType		v = null;
	DisplayRealType		z = null;
	HodographCoordSys	coordSys = null;
	DisplayTupleType	displayTupleType = null;

	try
	{
	    u = new DisplayRealType("Hodograph2D_U", false, 0.0,
		DEFAULT_SPEED_UNIT);
	    v = new DisplayRealType("Hodograph2D_V", false, 0.0,
		DEFAULT_SPEED_UNIT);
	    z = new DisplayRealType("Hodograph2D_Z", false, 0.0, null);
	    coordSys =
		new HodographCoordSys(DEFAULT_MAX_SPEED, DEFAULT_SPEED_UNIT);
	    displayTupleType = new DisplayTupleType(
		new DisplayRealType[] {u, v, z},
		coordSys);
	}
	catch (Exception e)
	{
	    String	reason = e.getMessage();
	    System.err.println(
		"Couldn't initialize HodographDisplayRenderer2D class" +
		(reason == null ? "" : ": " + reason));
	    e.printStackTrace();
	}

	U = u;
	V = v;
	Z = z;
	COORD_SYS = coordSys;
	DISPLAY_TUPLE_TYPE = displayTupleType;
    }


    /**
     * Constructs from nothing.
     */
    public
    HodographDisplayRenderer2D()
	throws VisADException
    {
    }


    /**
     * Indicates whether or not the given display real type is legal
     * for this display renderer.
     *
     * @param type	The display real type to be vetted.
     * @return		<code>true</code> if and only if <code>type</code>
     *			is a legal display real type for this display
     * 			renderer.
     */
    public boolean
    legalDisplayScalar(DisplayRealType type)
    {
	return type.equals(U) ||
	    type.equals(V) ||
	    type.equals(Z) ||
	    super.legalDisplayScalar(type);
    }


    /**
     * Gets the cursor coordinates.
     *
     * @return			Cursor coordinates in display space.
     *
     * @param cursor		The location of the cursor in display
     *				coordinates.
     */
    protected static final double[][]
    getCursorCoords(double[] cursor)
    {
	return new double[][] {
	    new double[] {cursor[0]}, 
	    new double[] {cursor[1]}, 
	    new double[] {cursor[2]}};
    }


    /**
     * Sets strings in the vector that describes the current location of the
     * cursor.
     */
    public void
    setCursorStringVector()
    {
	Vector		strings = new Vector(2);
	double[]	cursor = getCursor();
	double[][]	coords =
	    COORD_SYS.fromReference(getCursorCoords(cursor));

	strings.add("U = " + coords[0][0] +
	    " (" + WindProfileImpl.U_TYPE.getDefaultUnitString() + ")");

	strings.add("V = " + coords[1][0] +
	    " (" + WindProfileImpl.V_TYPE.getDefaultUnitString() + ")");

	setCursorStringVector(strings);
    }


    /**
     * Provides support for converting between hodograph coordinates
     * and display coordinates.
     */
    public static class
    HodographCoordSys
	extends	CoordinateSystem
    {
	private final double	maxSpeed;


	/*
	 * Constructs from a maximum speed and unit for speed.
	 *
	 * @param maxSpeed		The maximum speed to display.  Will be 
	 *				mapped to a display radis of 1.
	 * @param speedUnit		The unit of speed.
	 * @throws VisADException	Couldn't create necessary VisAD object.
	 * @throws IllegalArgumentException
	 *				<code>maxSpeed <= 0 </code>.
	 */
	public
	HodographCoordSys(double maxSpeed, Unit speedUnit)
	    throws VisADException
	{
	    super(Display.DisplaySpatialCartesianTuple,
		new Unit[] {speedUnit, speedUnit, null});

	    if (maxSpeed <= 0)
		throw new IllegalArgumentException();

	    this.maxSpeed = maxSpeed;
	}


	/**
	 * Converts from display coordinates to hodograph coordinates.
	 *
	 * @param coords		The coordinates.  On input, coords[0]
	 *				and coords[1] contain, respectively,
	 *				the X and Y display coordinates.  On
	 *				output, coords[0] and coords[1]
	 *				contain, respectively, the U and V
	 *				coordinates.
	 * @throws IllegalArgumentException
	 *				Invalid input coordinates: <code>coords
	 *				== null || coords.length < 2 ||
	 *				coords[0].length != coords[1].length
	 *				</code>.
	 */
	public double[][]
	fromReference(double[][] coords)
	{
	    if (coords == null || coords.length < 2 ||
		coords[0].length != coords[1].length)
	    {
		throw new IllegalArgumentException("Invalid input coordinates");
	    }

	    double[]	xs = coords[0];
	    double[]	ys = coords[1];
	    int		npts = xs.length;

	    for (int i = 0; i < npts; ++i)
	    {
		xs[i] *= maxSpeed;	// U
		ys[i] *= maxSpeed;	// V
	    }

	    return coords;
	}


	/**
	 * Converts from hodograph coordinates to display coordinates.
	 *
	 * @param coords		The coordinates.  On input, coords[0]
	 *				and coords[1] contain, respectively,
	 *				the U and V coordinates.
	 *				On ouput, coords[0] and coords[1]
	 *				contain, respectively, the X and Y
	 *				display coordinates.
	 * @throws IllegalArgumentException	Invalid input coordinates:
	 *					<code>coords == null ||
	 *					coords.length < 2 ||
	 *					coords[0].length != 
	 *					coords[1].length</code>.
	 */
	public double[][]
	toReference(double[][] coords)
	{
	    if (coords == null || coords.length < 2 ||
		coords[0].length != coords[1].length)
	    {
		throw new IllegalArgumentException("Invalid input coordinates");
	    }

	    double[]	us = coords[0];
	    double[]	vs = coords[1];
	    int		npts = us.length;

	    for (int i = 0; i < npts; ++i)
	    {
		us[i] /= maxSpeed;	// X
		vs[i] /= maxSpeed;	// Y
	    }

	    return coords;
	}


	/**
	 * Indicates whether this coordinate system equals an object.
	 *
	 * @param object	The object to be compared to this.
	 * @return		<code>true</code> if and only if this
	 *			coordinate system is semantically identical
	 *			to <code>object</code>.
	 */
	public boolean
	equals(Object object)
	{
	    boolean	equals;

	    if (!(object instanceof HodographCoordSys))
		equals = false;
	    else
	    {
		HodographCoordSys	that = (HodographCoordSys)object;
		equals = maxSpeed == that.maxSpeed;
	    }

	    return equals;
	}
    }
}
