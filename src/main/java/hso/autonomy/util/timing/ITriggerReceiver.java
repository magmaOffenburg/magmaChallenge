/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.timing;

/**
 * Instance that is able to receive an alarm from alarm timer
 * @author kdorer
 */
@FunctionalInterface
public interface ITriggerReceiver {
	/**
	 * Called when a set alarm gets active
	 * @param name the name of the Alarm
	 * @return true, if the alarm was no false alarm and should be finished
	 */
	boolean trigger(String name);
}
