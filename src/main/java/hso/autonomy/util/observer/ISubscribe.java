/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.observer;

/**
 * Basic subscribe interface for all applications which want to allow objects to
 * subscribe themselves to another object and be informed about changes
 *
 * @author Klaus Dorer
 * @param <T> Data type transported in updates
 */
@FunctionalInterface
public interface ISubscribe<T> {
	/**
	 * Adds an observer to the list of observers if not already in the list
	 * @param observer the observer that wants to be informed
	 */
	void attach(IObserver<T> observer);
}