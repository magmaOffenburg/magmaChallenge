/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.connection.impl;

import java.nio.ByteBuffer;

/**
 *
 * @author kdorer
 */
@FunctionalInterface
public interface IByteBufferReceiver {
	void update(ByteBuffer buffer);
}
