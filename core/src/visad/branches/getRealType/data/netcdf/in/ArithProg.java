/*
 * Copyright 1998, University Corporation for Atmospheric Research.
 * All Rights Reserved.
 * See file LICENSE for copying and redistribution conditions.
 *
 * $Id: ArithProg.java,v 1.8 2000-04-26 15:45:16 dglo Exp $
 */


package visad.data.netcdf.in;

import visad.VisADException;


/**
 * The arithmetic progression class is useful for determining if a sequence
 * of numeric values corresponds to an arithmetic progression and, if so,
 * just what that progression is.
 */
class ArithProg {
    /** Number of values accumulated. */
    private int			n = 0;

    /** First value. */
    private double		first = Double.NaN;

    /** Current last value. */
    private double		last = Double.NaN;

    /** Current increment. */
    private double 		increment = Double.NaN;

    /**
     * Whether the sequence is consistent with an arithmetic progression
     * or not.
     */
    private boolean		consistent = true;

    /** The difference threshold. */
    private final double	epsilon;

    /**
     * Construct with a default nearness threshold.  The default value
     * is 2e-9 (twice the C DBL_EPSILON).
     */
    ArithProg() {
	this.epsilon = 2e-9;	// twice the C DBL_EPSILON
    }

    /**
     * Construct with a caller-supplied nearness threshold.
     *
     * @param epsilon	Nearness threshold.
     * @throws IllegalArgumentException
     *			The given nearness threshold is negative.
     */
    ArithProg(double epsilon) {
	if (epsilon < 0) {
	    throw new IllegalArgumentException("epsilon < 0");
	}

	this.epsilon = epsilon;
    }

    /**
     * Accumulate a bunch of double values.  Indicate whether or not the
     * values are consistent with the arithmetic progression so far.
     *
     * @param values	The current values to accumulate.
     * @return		False if the sequence of values is inconsistent with
     *			the arithmetic progression seen so far.
     *			nearness threshold; otherwise, true.
     * @require		isConsistent() is true.
     * @promise		A subsequent getNumber() will return
     *			<code>values.length</code> more than previously.
     * @promise		A subsequent getLast() will return the last value of
     *			the vector providing the function returns true.
     */
/* WLH 12 Sept 98
    boolean accumulate(double[] values) throws VisADException {
	for (int i = 0; (i < values.length) && accumulate(values[i]); ++i) ;

	return consistent;
    }
*/
    boolean accumulate(double[] values) throws VisADException {
        if (!consistent) {
          throw new VisADException("Sequence not arithmetic series");
        }
        for (int i = 0; i < values.length; ++i) {
          if (n == 0) {
              first = values[i];
          } else if (n == 1) {
              increment = values[i] - first;
          } else {
              double      delta = increment == 0
                                      ? values[i] - last
                                      : 1.0 - (values[i] - last) / increment;
              if (Math.abs(delta) <= epsilon) {
                  increment = (values[i] - first) / n;
              } else {
                  consistent = false;
                  increment = Double.NaN;
              }
          }

          last = values[i];
          n++;

          if (!consistent) return false;

        }
        return true;
    }


    /**
     * Accumulate a bunch of float values.  Indicate whether or not the
     * values are consistent with the arithmetic progression so far.
     *
     * @param values	The current values to accumulate.
     * @return		False if the sequence of values is inconsistent with
     *			the arithmetic progression seen so far.
     *			nearness threshold; otherwise, true.
     * @require		isConsistent() is true.
     * @promise		A subsequent getNumber() will return
     *			<code>values.length</code> more than previously.
     * @promise		A subsequent getLast() will return the last value of
     *			the vector providing the function returns true.
     */
/* WLH 12 Sept 98
    boolean accumulate(float[] values) throws VisADException {
	for (int i = 0; i < values.length && accumulate(values[i]); ++i) ;

	return consistent;
    }
*/
    boolean accumulate(float[] values) throws VisADException {
        if (!consistent) {
          throw new VisADException("Sequence not arithmetic series");
        }
        for (int i = 0; i < values.length; ++i) {
          if (n == 0) {
              first = values[i];
          } else if (n == 1) {
              increment = values[i] - first;
          } else {
              double      delta = increment == 0
                                      ? values[i] - last
                                      : 1.0 - (values[i] - last) / increment;
              if (Math.abs(delta) <= epsilon) {
                  increment = (values[i] - first) / n;
              } else {
                  consistent = false;
                  increment = Double.NaN;
              }
          }

          last = values[i];
          n++;

          if (!consistent) return false;

        }
        return true;
    }

    /**
     * Indicate whether or not the sequence so far is consistent with an
     * arithmetic progression.
     *
     * @return	True if and only if the sequence of values seen so far is
     *		consistent with an arithmetic progression.
     */
    boolean isConsistent() {
	return consistent;
    }

    /**
     * Get the number of values.
     *
     * @return	The number of values accumulated so far.
     * @require	isConsistent() is true.
     */
    int getNumber() {
	return n;
    }

    /**
     * Get the first value.
     *
     * @return	The first accumulated value.
     * @require	isConsistent() is true.
     */
    double getFirst() {
	return first;
    }

    /**
     * Set the first value.
     */
    protected void setFirst(double value)
    {
	first = value;
    }

    /**
     * Get the last value.
     *
     * @return	The most recently accumulated value.
     * @require	isConsistent() is true.
     */
    double getLast() {
	return last;
    }

    /**
     * Get the current increment.
     *
     * @return	The computed increment so far.
     * @require	isConsistent() is true.
     */
    double getIncrement() {
	return increment;
    }

    /**
     * Set the increment.
     */
    protected void setIncrement(double value)
    {
	increment = value;
    }

    /**
     * Return the epsilon value.
     */
    public double getEpsilon()
    {
	return epsilon;
    }

    /**
     * Set whether or not the sequence is consistent.
     */
    protected void setConsistent(boolean value)
    {
	consistent = value;
    }

    /**
     * Set the most recently seen value.
     */
    protected void setLast(double value)
    {
	last = value;
    }

    /**
     * Increment the number of values seen.
     */
    protected void incrementNumber()
    {
	n++;
    }
}