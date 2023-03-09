/* Copyright 2009 Hochschule Offenburg
 * Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber,
 * Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
 * Joachim Schilling, Rajit Shahi
 *
 * This file is part of magmaOffenburg.
 *
 * magmaOffenburg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * magmaOffenburg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with magmaOffenburg. If not, see <http://www.gnu.org/licenses/>.
 */

package magma.tools.benchmark.model;

import hso.autonomy.util.observer.IObserver;
import java.io.File;
import java.util.List;

/**
 *
 * @author kdorer
 */
public interface IModelReadOnly {
	/**
	 * @return the results per team
	 */
	List<ITeamResult> getTeamResults();

	boolean isRunning();

	List<TeamConfiguration> loadConfigFile(File file) throws InvalidConfigFileException;

	void attach(IObserver<IModelReadOnly> observer);

	/**
	 * Removes an observer from the list of observers
	 *
	 * @param observer The observer that wants to be removed
	 * @return true if The observer has been in the list and was removed
	 */
	boolean detach(IObserver<IModelReadOnly> observer);
}