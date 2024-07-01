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

package magma.tools.benchmark.model.proxy;

import hso.autonomy.util.symboltreeparser.SymbolTreeParser;
import java.net.Socket;
import java.util.List;
import magma.tools.SAProxy.impl.AgentProxy;
import magma.tools.benchmark.model.bench.RunInformation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Special proxy for benchmarking agents
 * @author kdorer
 */
public class BenchmarkAgentProxy extends AgentProxy
{
	/** counts the number of cycles both legs do not have force */
	private int bothLegsOffGround;

	/** counts the number of cycles one leg do not have force */
	private int oneLegOffGround;

	/** counts the number of cycles both legs have force */
	private int noLegOffGround;

	/** counts the number of cycles in which at least one leg has force */
	private int legOnGround;

	private boolean startCount;

	private boolean stopCount;

	private boolean beaming;

	private final boolean allowPlayerBeaming;

	private final boolean isGazebo;

	private RunInformation runInfo;

	private SymbolTreeParser parser = new SymbolTreeParser();

	private Vector3D groundTruthPosition = Vector3D.ZERO;

	private float groundTruthOrientation;

	private float time;

	public BenchmarkAgentProxy(Socket clientSocket, String ssHost, int ssPort, boolean showMessages,
			boolean allowPlayerBeaming, boolean isGazebo)
	{
		super(clientSocket, ssHost, ssPort, showMessages);
		this.allowPlayerBeaming = allowPlayerBeaming;
		this.isGazebo = isGazebo;
		beaming = true;
		runInfo = new RunInformation();
	}

	public void startCount()
	{
		if (startCount) {
			return;
		}
		bothLegsOffGround = 0;
		oneLegOffGround = 0;
		noLegOffGround = 0;
		legOnGround = 0;
		startCount = true;
		stopCount = false;
	}

	public void stopCount()
	{
		stopCount = true;
	}

	public void noBeaming()
	{
		beaming = false;
	}

	/**
	 * Called before a message from the server was forwarded to the client
	 * @param msg the message received from the server
	 */
	@Override
	protected byte[] onNewServerMessage(byte[] msg)
	{
		if (isGazebo) {
			msg = extractGazeboGroundTruth(new String(msg)).getBytes();
		}

		double leftPressure = 0;
		double rightPressure = 0;
		String message = new String(msg);

		if (isGazebo) {
			extractTime(message);
		}

		while (message.contains("(FRP (n lf") || message.contains("(FRP (n rf")) {
			Vector3D leftForce = getForce(message, "(FRP (n lf");
			if (leftForce == null) {
				return msg;
			}
			Vector3D rightForce = getForce(message, "(FRP (n rf");
			if (rightForce == null) {
				return msg;
			}

			leftPressure += leftForce.getNorm();
			rightPressure += rightForce.getNorm();
			message = message.replaceFirst("\\(FRP \\(n lf", "");
			message = message.replaceFirst("\\(FRP \\(n rf", "");
		}

		if (startCount && !stopCount) {
			if (leftPressure < 0.01 && rightPressure < 0.01) {
				bothLegsOffGround++;
				if (showMessages) {
					System.out.println("off ground");
				}
			} else {
				legOnGround++;
				if (leftPressure >= 0.01 && rightPressure >= 0.01) {
					noLegOffGround++;
				} else {
					oneLegOffGround++;
				}
			}
		}

		return msg;
	}

	private String extractGazeboGroundTruth(String message)
	{
		int groundTruthIndex = message.lastIndexOf(" (mypos");
		if (groundTruthIndex < 0) {
			return message;
		}

		String groundTruth = message.substring(groundTruthIndex).trim();
		List<Object> result = parser.parse(groundTruth);

		List<Object> myPos = (List<Object>) result.get(0);
		groundTruthPosition = new Vector3D(parseFloat(myPos, 1), parseFloat(myPos, 2), parseFloat(myPos, 3));

		List<Object> myOrien = (List<Object>) result.get(1);
		groundTruthOrientation = parseFloat(myOrien, 1);

		// ground truth in gazebo is always at end
		return message.substring(0, groundTruthIndex);
	}

	private void extractTime(String message)
	{
		List<Object> root = parser.parse(message);
		for (Object n : root) {
			List<Object> node = (List<Object>) n;
			if (node.get(0).equals("time")) {
				List<Object> now = (List<Object>) node.get(1);
				time = parseFloat(now, 1);
				break;
			}
		}
	}

	private float parseFloat(List<Object> node, int index)
	{
		return Float.parseFloat((String) node.get(index));
	}

	private Vector3D getForce(String message, String which)
	{
		int forceIndex = message.indexOf(which);
		if (forceIndex >= 0) {
			int fIndex = message.indexOf("(f", forceIndex);
			if (fIndex >= 0) {
				int eIndex = message.indexOf(")", fIndex);
				String forceString = message.substring(fIndex + 3, eIndex);
				String[] values = forceString.split(" ");
				if (values.length == 3) {
					float x = Float.parseFloat(values[0]);
					float y = Float.parseFloat(values[1]);
					float z = Float.parseFloat(values[2]);
					return new Vector3D(x, y, z);
				}
			}
			System.out.println("Strange force message: " + message.substring(forceIndex, forceIndex + 30));
			return null;
		}
		return Vector3D.ZERO;
	}

	/**
	 * Called before a client message has been forwarded to the server
	 * @param message the message received from the client
	 * @return true if the message should be forwarded to the server
	 */
	@Override
	public byte[] onNewClientMessage(byte[] message)
	{
		if (allowPlayerBeaming) {
			return message;
		}

		String msgString = new String(message);
		if (msgString.contains("(beam")) {
			System.out.println("Agent may not beam! Message ignored.");
			if (beaming) {
				msgString = "";
			} else {
				return null;
			}
		}

		if (beaming) {
			msgString += "(beam " + runInfo.getBeamX() + " " + runInfo.getBeamY() + " 0)";
			message = msgString.getBytes();
		}

		return message;
	}

	public int getBothLegsOffGround()
	{
		return bothLegsOffGround;
	}

	public int getOneLegOffGround()
	{
		return oneLegOffGround;
	}

	public int getNoLegOffGround()
	{
		return noLegOffGround;
	}

	public int getLegOnGround()
	{
		if (legOnGround < 100) {
			return 100;
		}
		return legOnGround;
	}

	public void updateProxy(RunInformation runInfo)
	{
		this.runInfo = runInfo;
	}

	public Vector3D getGroundTruthPosition()
	{
		return groundTruthPosition;
	}

	public float getGroundTruthOrientation()
	{
		return groundTruthOrientation;
	}

	public float getTime()
	{
		return time;
	}
}
