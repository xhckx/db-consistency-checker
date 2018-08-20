package de.htwk.imn.consistencychecker;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.htwk.imn.consistencychecker.database.DataBase;

public class MonotonicWriteConsistencyCheckerTest {

	@Mock
	private DataBase db;

	@InjectMocks
	private MonotonicWriteConsistencyChecker mwc = new MonotonicWriteConsistencyChecker();
	private ConsistencyCheckResult result;

	@Before
	public void setUp() throws Exception {
		LogManager.getLogger(ReadYourWritesConsistencyChecker.class).setLevel(Level.OFF);
		MockitoAnnotations.initMocks(this);
		Mockito.doNothing().when(db).writeData(any(String.class), any(String.class), any(String.class));
	}

	@Test
	public void mwcWrite() {
		result = mwc.checkMWC(db, 2, true);
		assertThat(result.getTotalChecks(), equalTo(2L));
	}

	@Test
	public void mwcPass() {
		when(db.readData(any(String.class), any(String.class))).thenReturn("2");
		result = mwc.checkMWC(db, 1, false);
		assertThat(result.getTotalChecks(), equalTo(1L));
		assertThat(result.getViolations(), equalTo(0L));
	}

	@Test
	public void mwcFail() {
		when(db.readData(any(String.class), any(String.class))).thenReturn("1");
		result = mwc.checkMWC(db, 2, false);
		assertThat(result.getTotalChecks(), equalTo(2L));
		assertThat(result.getViolations(), equalTo(2L));
	}

}
