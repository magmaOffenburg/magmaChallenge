/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.observer;

/**
 * Interface to guarantee independence of Observer and Subject
 *
 * @param <T> Data type transported in updates
 */
@FunctionalInterface
public interface IObserver<T> {
	/**
	 * Called to notify an observer about a state change
	 * @param content reference to the object containing changed state
	 */
	void update(T content);
}
