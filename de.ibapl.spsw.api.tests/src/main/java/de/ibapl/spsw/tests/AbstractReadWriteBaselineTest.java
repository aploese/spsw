/*-
 * #%L
 * SPSW Provider
 * %%
 * Copyright (C) 2009 - 2017 Arne Plöse
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */
package de.ibapl.spsw.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.ibapl.spsw.api.Baudrate;
import de.ibapl.spsw.tests.tags.BaselineTest;

/**
 * Unit test for simple App. Timeout is computed 8 data bits + 2 stop bits +
 * parity bit + start bit == 12
 */
public abstract class AbstractReadWriteBaselineTest extends AbstractReadWriteTest {

	/**
	 * Send two bytes with half the InterbyteReadTimeout and expect a return from read within OverallReadTimeout - InterbyteReadTimeout
	 * @throws Exception
	 */
	@BaselineTest
	@Test
	public void testInterbyteReadTimeout() throws Exception {
		openDefault();
		final byte[] sendData = new byte[] { 12, 15 };
		final Object LOCK = new Object();
		readSpc.setTimeouts(200, 1000, 2000);
		new Thread(() -> {
			final byte[] data = new byte[3];
			try {
				assertTimeoutPreemptively(
						Duration.ofMillis(readSpc.getOverallReadTimeout() - readSpc.getInterByteReadTimeout()), () -> {
							final int len = readSpc.getInputStream().read(data);
							assertEquals(2, len);
						});
				assertEquals(sendData[0], data[0]);
				assertEquals(sendData[1], data[1]);
				synchronized (LOCK) {
					LOCK.notifyAll();
				}
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}).start();
		Thread.sleep(100);
		writeSpc.getOutputStream().write(sendData[0]);
		Thread.sleep(readSpc.getInterByteReadTimeout() / 2);
		writeSpc.getOutputStream().write(sendData[1]);
		synchronized (LOCK) {
			LOCK.wait(readSpc.getOverallReadTimeout());
		}
	}
}