package de.ibapl.spsw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * @author Arne Plöse
 */
public abstract class AbstractReadWriteTest extends AbstractPortTest {

	@Override
	@BeforeEach
	public void setUp(TestInfo testInfo) throws Exception {
		super.setUp(testInfo);
		assumeRWTest();
	}

}