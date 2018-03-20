package de.ibapl.spsw.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class AbstractReadWriteTest extends AbstractPortTest {

	@BeforeEach
	public void setUp(TestInfo testInfo) throws Exception {
		super.setUp(testInfo);
		assumeRWTest();
	}

}