package test;
import static org.junit.Assert.*;
import org.junit.Test;

import cslp.AbstractEvent;
import cslp.Simulator;

public class AbstractEventTest {

	AbstractEvent abstractEvent = new AbstractEvent(){
		@Override
		public void execute(Simulator simulator) {
			// TODO Auto-generated method stub
		}
	};

	@Test
	public void timeToStringTest() {
		abstractEvent.schedule(86400);
		String expected = "01:00:00:00";
		String result = abstractEvent.timeToString();
		assertEquals(expected, result);
		
		abstractEvent.schedule(90000);
		String expected1 = "01:01:00:00";
		String result1 = abstractEvent.timeToString();
		assertEquals(expected1, result1);

		abstractEvent.schedule(90060);
		String expected2 = "01:01:01:00";
		String result2 = abstractEvent.timeToString();
		assertEquals(expected2, result2);

		abstractEvent.schedule(90061);
		String expected3 = "01:01:01:01";
		String result3 = abstractEvent.timeToString();
		assertEquals(expected3, result3);

	}
	
	
}
