/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.observer;

/**
 * Publish/subscribe interface for the Observer pattern
 *
 * @author Klaus Dorer
 * @param <T> Data type transported in updates
 */
public interface IPublishSubscribe<T>
{
	/**
	 * Add an observer to the list of observers
	 *
	 * @param observer The observer that wants to be added
	 */
	void attach(IObserver<T> observer);

	/**
	 * Removes an observer from the list of observers
	 *
	 * @param observer The observer that wants to be removed
	 * @return true if The observer has been in the list and was removed
	 */
	boolean detach(IObserver<T> observer);

	/**
	 * Removes all observers from the list of observers
	 */
	void detachAll();

	/**
	 * Called to inform observer about a change in state
	 *
	 * @param content the object that contains the changed information
	 */
	void onStateChange(T content);
}