import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.Appointment;
import edu.uwm.cs351.ApptBook;
import edu.uwm.cs351.Duration;
import edu.uwm.cs351.Period;
import edu.uwm.cs351.Time;


public class TestInternals extends ApptBook.TestInvariantChecker {
	protected Time now = new Time();
	protected Appointment e1 = new Appointment(new Period(now,Duration.HOUR),"1: think");
	protected Appointment e2 = new Appointment(new Period(now,Duration.DAY),"2: current");
	protected Appointment e3 = new Appointment(new Period(now.add(Duration.HOUR),Duration.HOUR),"3: eat");
	protected Appointment e3a = new Appointment(new Period(now.add(Duration.HOUR),Duration.HOUR),"3: eat");
	protected Appointment e4 = new Appointment(new Period(now.add(Duration.HOUR.scale(2)),Duration.HOUR.scale(8)),"4: sleep");
	protected Appointment e5 = new Appointment(new Period(now.add(Duration.DAY),Duration.DAY),"5: tomorrow");

	private int reports = 0;
		
	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = getReporter();
		try {
			setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			setReporter(null);
		} finally {
			setReporter(savedReporter);
		}

	}

	//// Tests
	// The tests fall into different groups that
	// are not ordered with respect to each other.
	// (So, for example, failing testC7 doesn't preclude 
	// one from getting credit for passing all testF tests.)

	/**
	 * Compute the height of a non-cyclic structure.
	 * @param r subtree to compute height for, may be null
	 * @return height of "tree" (or DAG).
	 */
	protected int computeHeight(Node r) {
		int h = 0;
		while (!checkHeight(r,h)) {
			++h;
		}
		return h;
	}
	
	/// Locked tests
	
	public void test() {
		testHeight(false);
		testCount(false);
	}
	
	private void testHeight(boolean ignored) {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e3, null, n2);
		Node n4 = newNode(e4, null, null);
		Node n5 = newNode(e5, n3, n4);
		//    5
		//  /   \
		// 3     4
		//  \
		//   2
		//  /
		// 1
		// What is the height of the given trees?
		assertEquals(Ti(1644057369), computeHeight(null));
		assertEquals(Ti(813640801), computeHeight(n1));
		assertEquals(Ti(1416275309), computeHeight(n2));
		assertEquals(Ti(685578957), computeHeight(n3));
		assertEquals(Ti(876002599), computeHeight(n4));
		assertEquals(Ti(895736807), computeHeight(n5));
	}
	
	private void testCount(boolean ignored) {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, null, n1);
		Node n3 = newNode(e3, null, null);
		Node n4 = newNode(e4, n3, null);
		Node n5 = newNode(e5, n2, n4);
		//    5
		//  /   \
		// 2     4
		//  \   /
		//   1 3
		// What is the count of the given trees?
		assertEquals(Ti(2070230118), countNodes(null));
		assertEquals(Ti(309785970), countNodes(n1));
		assertEquals(Ti(1779957010), countNodes(n2));
		assertEquals(1, countNodes(n3));
		assertEquals(2, countNodes(n4));
		assertEquals(Ti(1985889205), countNodes(n5));
		testCount2(ignored);
	}
	
	private void testCount2(boolean ignored) {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, n1); // n1 is used twice!
		Node n3 = newNode(e3, n2, n2);
		Node n4 = newNode(e4, n3, n3);
		Node n5 = newNode(e5, n4, n4);
		assertEquals(1, countNodes(n1));
		assertEquals(Ti(817590831), countNodes(n2));
		assertEquals(Ti(1132768343), countNodes(n3));
		assertEquals(Ti(1771765716), countNodes(n4));
		assertEquals(Ti(149585225), countNodes(n5));
	}
	
	/// testCx: tests of countNodes
	
	public void testC0() {
		assertEquals(0, countNodes(null));
	}
	
	public void testC1() {
		assertEquals(1, countNodes(newNode(null,null,null)));
	}
	
	public void testC2() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		assertEquals(2, countNodes(n2));
	}
	
	public void testC3() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e3, null, n2);
		assertEquals(3, countNodes(n3));
	}
	
	public void testC4() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e3, null, null);
		Node n4 = newNode(e4, n2, n3);
		assertEquals(4, countNodes(n4));
	}
	
	public void testC5() {
		Node n1 = newNode(e1, null, null);
		Node n3 = newNode(e3, null, null);
		Node n4 = newNode(e4, n3, null);
		Node n5 = newNode(e5, n4, null);
		Node n2 = newNode(e2, n1, n5);
		assertEquals(5, countNodes(n2));
	}
	
	public void testC6() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, n1);
		Node n3 = newNode(e3, n2, n2);
		Node n4 = newNode(e4, n3, n3);
		Node n5 = newNode(e5, n4, n4);
		assertEquals(31, countNodes(n5));
	}
	
	public void testC7() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, n1);
		Node n3 = newNode(e3, n2, n2);
		Node n4 = newNode(e4, n3, n3);
		Node n5 = newNode(e5, n4, n4);
		Node n6 = newNode(e1, n5, n5);
		Node n7 = newNode(e2, n6, n6);
		assertEquals(127, countNodes(n7));
	}
	
	public void testC8() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, n1);
		Node n3 = newNode(e3, n2, n2);
		Node n4 = newNode(e4, n3, null);
		Node n5 = newNode(e5, n4, n4);
		Node n6 = newNode(e1, n5, n5);
		Node n7 = newNode(e2, null, n6);
		Node n8 = newNode(e3, n7, n7);
		assertEquals(73, countNodes(n8));
	}
	
	public void testC9() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e3, n2, null);
		Node n4 = newNode(e1, n3, null);
		Node n5 = newNode(e2, n4, n1);
		Node n6 = newNode(e3, n5, n2);
		Node n7 = newNode(e1, n3, n6);
		Node n8 = newNode(e2, n4, n7);
		Node n9 = newNode(e3, n5, n8);
		assertEquals(25, countNodes(n9));
	}
	
	
	/// testFx: tests of findCursor
	
	public void testF0() {
		assertEquals(true, foundCursor(null));
		setCursor(newNode(null,null,null));
		assertEquals(false, foundCursor(null));
	}
	
	public void testF1() {
		Node n1 = newNode(e1, null, null);
		assertEquals(true, foundCursor(n1));
		setCursor(n1);
		assertEquals(true, foundCursor(n1));
		Node n2 = newNode(e1, null, null);
		assertEquals(false, foundCursor(n2));
	}
	
	public void testF2() {
		Node n1 = newNode(e2, null, null);
		Node n2 = newNode(e2, null, n1);
		assertEquals(true, foundCursor(n2));
		setCursor(n1);
		assertEquals(true, foundCursor(n2));
		setCursor(n2);
		assertEquals(true, foundCursor(n2));
		setCursor(newNode(e2, null, null));
		assertEquals(false, foundCursor(n2));
		setCursor(newNode(e2, null, n1));
		assertEquals(false, foundCursor(n2));
	}
	
	public void testF3() {
		Node n1 = newNode(e3, null, null);
		Node n2 = newNode(e3, null, n1);
		Node n3 = newNode(e3, n2, null);
		
		assertEquals(true, foundCursor(n3));
		setCursor(n1);
		assertEquals(true, foundCursor(n3));
		setCursor(n2);
		assertEquals(true, foundCursor(n3));
		setCursor(n3);
		assertEquals(true, foundCursor(n3));
		setCursor(newNode(e2, null, null));
		assertEquals(false, foundCursor(n3));
		setCursor(newNode(e2, null, n1));
		assertEquals(false, foundCursor(n3));
		setCursor(newNode(e2, n2, null));
		assertEquals(false, foundCursor(n3));
	}
	
	public void testF4() {
		Node n1 = newNode(e4, null, null);
		Node n2 = newNode(e4, null, null);
		Node n3 = newNode(e4, n2, null);
		Node n4 = newNode(e4, n1, n3);
		
		assertEquals(true, foundCursor(n4));
		setCursor(n1);
		assertEquals(true, foundCursor(n4));
		setCursor(n2);
		assertEquals(true, foundCursor(n4));
		setCursor(n3);
		assertEquals(true, foundCursor(n4));
		setCursor(n4);
		assertEquals(true, foundCursor(n4));
		setCursor(newNode(e4, null, null));
		assertEquals(false, foundCursor(n4));
	}
	
	public void testF5() {
		Node n1 = newNode(e5, null,null);
		Node n2 = newNode(e5, null, n1);
		Node n3 = newNode(e5, null, n2);
		Node n4 = newNode(e5, n3, null);
		Node n5 = newNode(e5, n4, null);
		
		assertEquals(true, foundCursor(n5));
		setCursor(n1);
		assertEquals(true, foundCursor(n5));
		setCursor(n2);
		assertEquals(true, foundCursor(n5));
		setCursor(n3);
		assertEquals(true, foundCursor(n5));
		setCursor(n4);
		assertEquals(true, foundCursor(n5));
		setCursor(n5);
		assertEquals(true, foundCursor(n5));
		setCursor(newNode(e5, null, null));
		assertEquals(false, foundCursor(n5));			
	}
	
	public void testF6() {
		Node n1 = newNode(null,null,null);
		Node n2 = newNode(null, n1, n1);
		Node n3 = newNode(null,null, null);
		Node n4 = newNode(null, n1, n3);
		Node n5 = newNode(null, n2, n4);
		Node n6 = newNode(null, n3, n5);
		
		assertEquals(true, foundCursor(n6));
		setCursor(n1);
		assertEquals(true, foundCursor(n6));
		setCursor(n2);
		assertEquals(true, foundCursor(n6));
		setCursor(n3);
		assertEquals(true, foundCursor(n6));
		setCursor(n4);
		assertEquals(true, foundCursor(n6));
		setCursor(n5);
		assertEquals(true, foundCursor(n6));
		setCursor(n6);
		assertEquals(true, foundCursor(n6));
		setCursor(newNode(null, null, null));
		assertEquals(false, foundCursor(n6));
	}
	
	public void testF7() {
		Node n1 = newNode(e1, null, null);
		Node n3 = newNode(e1, null, null);
		Node n5 = newNode(e1, null, null);
		Node n7 = newNode(e1, null, null);
		Node n2 = newNode(e1, n1, n3);
		Node n6 = newNode(e1, n5, n7);
		Node n4 = newNode(e1, n2, n6);
		
		assertEquals(true, foundCursor(n4));
		setCursor(n1);
		assertEquals(true, foundCursor(n4));
		setCursor(n2);
		assertEquals(true, foundCursor(n4));
		setCursor(n3);
		assertEquals(true, foundCursor(n4));
		setCursor(n4);
		assertEquals(true, foundCursor(n4));
		setCursor(n5);
		assertEquals(true, foundCursor(n4));
		setCursor(n6);
		assertEquals(true, foundCursor(n4));
		setCursor(n7);
		assertEquals(true, foundCursor(n4));
		setCursor(newNode(e1, null, null));
		assertEquals(false, foundCursor(n4));
	}
	
	public void testF8() {
		Node n1 = newNode(e2, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e2, n2, n1);
		Node n4 = newNode(e2, n3, n2);
		Node n5 = newNode(e2, n4, n3);
		Node n6 = newNode(e2, n5, n4);
		Node n7 = newNode(e2, n6, n5);
		Node n8 = newNode(e2, n7, n6);
		
		assertEquals(true, foundCursor(n8));
		setCursor(n1);
		assertEquals(true, foundCursor(n8));
		setCursor(n2);
		assertEquals(true, foundCursor(n8));
		setCursor(n3);
		assertEquals(true, foundCursor(n8));
		setCursor(n4);
		assertEquals(true, foundCursor(n8));
		setCursor(n5);
		assertEquals(true, foundCursor(n8));
		setCursor(n6);
		assertEquals(true, foundCursor(n8));
		setCursor(n7);
		assertEquals(true, foundCursor(n8));
		setCursor(n8);
		assertEquals(true, foundCursor(n8));
		setCursor(newNode(e2, null, null));
		assertEquals(false, foundCursor(n8));
	}
	
	public void testF9() {
		Node n0 = newNode(e3, null, null); Node n0a = newNode(e3, null, null);
		Node n1 = newNode(e3, n0, n0a); Node n1a = newNode(e3, n0a, n0a);
		Node n2 = newNode(e3, n1a, n1); Node n2a = newNode(e3, n1a, n1a);
		Node n3 = newNode(e3, n2, n2a); Node n3a = newNode(e3, n2a, n2a);
		Node n4 = newNode(e3, n3a, n3); Node n4a = newNode(e3, n3a, n3a);
		Node n5 = newNode(e3, n4, n4a); Node n5a = newNode(e3, n4a, n4a);
		Node n6 = newNode(e3, n5a, n5); Node n6a = newNode(e3, n5a, n5a);
		Node n7 = newNode(e3, n6, n6a); Node n7a = newNode(e3, n6a, n6a);
		Node n8 = newNode(e3, n7a, n7); Node n8a = newNode(e3, n7a, n7a);
		Node n9 = newNode(e3, n8, n8a); Node n9a = newNode(e3, n8a, n8a);
		Node n10 = newNode(e3, n9a, n9);
		
		assertEquals(true, foundCursor(n10));
		setCursor(n0);
		assertEquals(true, foundCursor(n10));
		setCursor(n1);
		assertEquals(true, foundCursor(n10));
		setCursor(n2);
		assertEquals(true, foundCursor(n10));
		setCursor(n3);
		assertEquals(true, foundCursor(n10));
		setCursor(n4);
		assertEquals(true, foundCursor(n10));
		setCursor(n5);
		assertEquals(true, foundCursor(n10));
		setCursor(n6);
		assertEquals(true, foundCursor(n10));
		setCursor(n7);
		assertEquals(true, foundCursor(n10));
		setCursor(n8);
		assertEquals(true, foundCursor(n10));
		setCursor(n9);
		assertEquals(true, foundCursor(n10));
		setCursor(n10);
		assertEquals(true, foundCursor(n10));
		setCursor(newNode(e3, null, null));
		assertEquals(false, foundCursor(n10));
		
		setCursor(n0);
		assertEquals(false, foundCursor(n9a));			
	}
	
	
	/// testHx: tests of checkHeight
	
	public void testH0() {
		assertTrue(checkHeight(null,0));
		assertFalse(checkHeight(null, -1));
		assertTrue(checkHeight(null, 1));
	}
	
	public void testH1() {
		Node n = newNode(e3,null,null);
		assertTrue(checkHeight(n, 1));
		assertFalse(checkHeight(n, 0));
		assertTrue(checkHeight(n, 2));
	}
	
	public void testH2() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(null, n1, null);
		assertTrue(checkHeight(n2, 2));
		assertTrue(checkHeight(n1, 1));
		assertFalse(checkHeight(n2, 1));
		assertTrue(checkHeight(n2, 3));
	}
	
	public void testH3() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, null, null);
		Node n3 = newNode(e3, n1, n2);
		assertTrue(checkHeight(n3, 3));
		assertTrue(checkHeight(n3, 2));
		assertFalse(checkHeight(n3, 1));
		assertTrue(checkHeight(n3, 4));
	}
	
	public void testH4() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, null, null);
		Node n3 = newNode(e3, n1, n2);
		Node n4 = newNode(e4, null, n3);
		assertTrue(checkHeight(n4, 3));
		assertFalse(checkHeight(n4, 2));
		assertFalse(checkHeight(n4, 1));
		assertTrue(checkHeight(n3, 4));
	}
	
	public void testH5() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e3, n2, null);
		Node n4 = newNode(e4, null, n3);
		Node n5 = newNode(e5, null, n4);
		assertFalse(checkHeight(n5, 3));
		assertFalse(checkHeight(n5, 4));
		assertTrue(checkHeight(n5, 5));
		assertTrue(checkHeight(n5, 6));
		assertFalse(checkHeight(n5, 2));
		assertFalse(checkHeight(n5, 1));
		assertFalse(checkHeight(n5, 0));
		assertFalse(checkHeight(n5, -1));
	}
	
	public void testH6() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		Node n3 = newNode(e3, n2, null);
		Node n4 = newNode(e4, null, null);
		Node n5 = newNode(e5, n3, n4);
		Node n6 = newNode(null, n5, null);
		assertFalse(checkHeight(n6, 3));
		assertFalse(checkHeight(n6, 4));
		assertTrue(checkHeight(n6, 5));
		assertTrue(checkHeight(n6, 6));
	}
	
	public void testH7() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, n1);
		Node n3 = newNode(e3, n2, n2);
		Node n4 = newNode(e4, n3, n1);
		assertFalse(checkHeight(n4, 2));
		assertFalse(checkHeight(n4, 3));
		assertTrue(checkHeight(n4, 4));
		assertTrue(checkHeight(n4, 5));
	}
	
	public void testH8() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, n1, null);
		n1.setRight(n2);
		assertFalse(checkHeight(n1, 0));
		assertFalse(checkHeight(n1, 1));
		assertFalse(checkHeight(n1, 2));
		assertFalse(checkHeight(n1, 3));
		assertFalse(checkHeight(n1, 4));
		assertFalse(checkHeight(n1, 8000));
	}
	
	public void testH9() {
		Node n1 = newNode(null, null, null);
		Node n2 = newNode(null, n1, n1);
		Node n3 = newNode(null, n2, n2);
		Node n4 = newNode(null, n3, n3);
		Node n5 = newNode(null, n4, n4);
		Node n6 = newNode(null, n5, n5);
		Node n7 = newNode(null, n6, n6);
		Node n8 = newNode(null, n7, n7);
		Node n9 = newNode(null, n8, n8);
		Node n10 = newNode(null, n9, n9);
		
		assertTrue(checkHeight(n10, 10));
		
		Node n11 = newNode(null, n10, n10);
		Node n12 = newNode(null, n11, n11);
		Node n13 = newNode(null, n12, n12);
		Node n14 = newNode(null, n13, n13);
		Node n15 = newNode(null, n14, n14);
		Node n16 = newNode(null, n15, n15);
		Node n17 = newNode(null, n16, n16);
		Node n18 = newNode(null, n17, n17);
		Node n19 = newNode(null, n18, n18);
		Node n20 = newNode(null, n19, n19);
		
		// this will explore one millions paths
		assertTrue(checkHeight(n20, 25));

		n1.setLeft(n20);
		
		assertFalse(checkHeight(n20, 10_000));
	}
	
	
	/// testIx: tests of firstInTree
	
	public void testI1() {
		Node n = newNode(e1,null,null);
		assertSame(n, firstInTree(n));
	}
	
	public void testI2() {
		Node n1 = newNode(e2, null, null);
		Node n2 = newNode(e2, n1, null);
		assertSame(n1, firstInTree(n2));
	}
	
	public void testI3() {
		Node n1 = newNode(e2, null, null);
		Node n2 = newNode(e2, null, n1);
		assertSame(n2, firstInTree(n2));
	}
	
	public void testI4() {
		Node n1 = newNode(e4, null, null);
		Node n2 = newNode(e4, null, null);
		Node n3 = newNode(e4, n1, n2);
		Node n4 = newNode(e4, n3, n2);
		assertSame(n1, firstInTree(n4));
	}
	
	public void testI5() {
		Node n2 = newNode(e5, null, null);
		Node n1 = newNode(e5, null, n2);
		Node n4 = newNode(e5, null, null);
		Node n5 = newNode(e5, n4, null);
		Node n3 = newNode(e5, n1, n5);
		assertSame(n1, firstInTree(n3));
	}
	
	public void testI9() {
		Node n0 = newNode(e3, null, null); Node n0a = newNode(e3, null, null);
		Node n1 = newNode(e3, n0, n0a); Node n1a = newNode(e3, n0a, n0a);
		Node n2 = newNode(e3, n1, n1a); Node n2a = newNode(e3, n1a, n1a);
		Node n3 = newNode(e3, n2, n2a); Node n3a = newNode(e3, n2a, n2a);
		Node n4 = newNode(e3, n3, n3a); Node n4a = newNode(e3, n3a, n3a);
		Node n5 = newNode(e3, n4, n4a); Node n5a = newNode(e3, n4a, n4a);
		Node n6 = newNode(e3, n5, n5a); Node n6a = newNode(e3, n5a, n5a);
		Node n7 = newNode(e3, n6, n6a); Node n7a = newNode(e3, n6a, n6a);
		Node n8 = newNode(e3, n7, n7a); Node n8a = newNode(e3, n7a, n7a);
		Node n9 = newNode(e3, n8, n8a); Node n9a = newNode(e3, n8a, n8a);
		Node n10 = newNode(e3, n9, n9a);
		
		assertSame(n0, firstInTree(n10));
	}
	
	
	/// testNx: tests of nextInTree
	
	public void testN0() {
		assertNull(nextInTree(null, e5, true, null));
		assertNull(nextInTree(null, e5, false, null));
	
		Node n0 = newNode(null, null, null);
		assertSame(n0, nextInTree(null, e5, true, n0));
		assertSame(n0, nextInTree(null, e5, false, n0));
	}

	public void testN1() {
		Node n0 = newNode(null, null, null);
		Node n1 = newNode(e3, null, null);
		
		assertNull(nextInTree(n1, e3, false, null));
		assertSame(n1, nextInTree(n1, e3, true, null));
		assertSame(n0, nextInTree(n1, e3, false, n0));
		assertSame(n1, nextInTree(n1, e3, true, n0));
	
		assertSame(n1, nextInTree(n1, e2, false, null));
		assertSame(n1, nextInTree(n1, e2, true, null));
		assertSame(n1, nextInTree(n1, e2, false, n0));
		assertSame(n1, nextInTree(n1, e2, true, n0));
	
		assertNull(nextInTree(n1, e4, false, null));
		assertNull(nextInTree(n1, e4, true, null));
		assertSame(n0, nextInTree(n1, e4, false, n0));
		assertSame(n0, nextInTree(n1, e4, true, n0));
	}

	public void testN2() {
		Node n0 = newNode(null, null, null);
		Node n1 = newNode(e4, null, null);
		Node n2 = newNode(e2, null, n1);
	
		assertSame(n2, nextInTree(n2, e1, false, null));
		assertSame(n2, nextInTree(n2, e1, true, null));
		assertSame(n2, nextInTree(n2, e1, false, n0));
		assertSame(n2, nextInTree(n2, e1, true, n0));
	
		assertSame(n1, nextInTree(n2, e2, false, null));
		assertSame(n2, nextInTree(n2, e2, true, null));
		assertSame(n1, nextInTree(n2, e2, false, n0));
		assertSame(n2, nextInTree(n2, e2, true, n0));
	
		assertSame(n1, nextInTree(n2, e3, false, null));
		assertSame(n1, nextInTree(n2, e3, true, null));
		assertSame(n1, nextInTree(n2, e3, false, n0));
		assertSame(n1, nextInTree(n2, e3, true, n0));
	
		assertNull(nextInTree(n2, e4, false, null));
		assertSame(n1, nextInTree(n2, e4, true, null));
		assertSame(n0, nextInTree(n2, e4, false, n0));
		assertSame(n1, nextInTree(n2, e4, true, n0));
		
		assertNull(nextInTree(n2, e5, false, null));
		assertNull(nextInTree(n2, e5, true, null));
		assertSame(n0, nextInTree(n2, e5, false, n0));
		assertSame(n0, nextInTree(n2, e5, true, n0));
	}

	public void testN3() {
		Node n0 = newNode(null, null, null);
		Node n2 = newNode(e2, null, null);
		Node n1 = newNode(e4, n2, null);
		Node n = n1;
	
		assertSame(n2, nextInTree(n, e1, false, null));
		assertSame(n2, nextInTree(n, e1, true, null));
		assertSame(n2, nextInTree(n, e1, false, n0));
		assertSame(n2, nextInTree(n, e1, true, n0));
	
		assertSame(n1, nextInTree(n, e2, false, null));
		assertSame(n2, nextInTree(n, e2, true, null));
		assertSame(n1, nextInTree(n, e2, false, n0));
		assertSame(n2, nextInTree(n, e2, true, n0));
	
		assertSame(n1, nextInTree(n, e3, false, null));
		assertSame(n1, nextInTree(n, e3, true, null));
		assertSame(n1, nextInTree(n, e3, false, n0));
		assertSame(n1, nextInTree(n, e3, true, n0));
	
		assertNull(nextInTree(n, e4, false, null));
		assertSame(n1, nextInTree(n, e4, true, null));
		assertSame(n0, nextInTree(n, e4, false, n0));
		assertSame(n1, nextInTree(n, e4, true, n0));
		
		assertNull(nextInTree(n, e5, false, null));
		assertNull(nextInTree(n, e5, true, null));
		assertSame(n0, nextInTree(n, e5, false, n0));
		assertSame(n0, nextInTree(n, e5, true, n0));
	}

	public void testN5() {
		Node n0 = newNode(null, null, null);
		Node n2 = newNode(e1, null, null);
		Node n1 = newNode(e1, null, n2);
		Node n4 = newNode(e3, null, null);
		Node n5 = newNode(e5, n4, null);
		Node n3 = newNode(e3, n1, n5);
		Node n = n3;
	
		assertSame(n3, nextInTree(n, e1, false, n0));
		assertSame(n1, nextInTree(n, e1, true, n0));
	
		assertSame(n3, nextInTree(n, e2, false, n0));
		assertSame(n3, nextInTree(n, e2, true, n0));
	
		assertSame(n5, nextInTree(n, e3, false, n0));
		assertSame(n3, nextInTree(n, e3, true, n0));
	
		assertSame(n5, nextInTree(n, e4, false, n0));
		assertSame(n5, nextInTree(n, e4, true, n0));
	
		assertSame(n0, nextInTree(n, e5, false, n0));
		assertSame(n5, nextInTree(n, e5, true, n0));
	}

	public void testN7() {
		Appointment e1a = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1b = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1c = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1d = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Node n1a = newNode(e1a, null, null);
		Node n2 = newNode(e2, n1a, null);
		Node n1b = newNode(e1b, null, n2);
		Node n3 = newNode(e3, n1b, null);
		Node n1c = newNode(e1c, null, n3);
		Node n4 = newNode(e4, n1c, null);
		Node n1d = newNode(e1d, null, n4);		
		Node n = n1d;
	
		Node n0 = newNode(e1, null, null);
		
		assertSame(n2, nextInTree(n, e1, false, n0));
		assertSame(n1d, nextInTree(n, e1, true, n0));
		
		assertSame(n3, nextInTree(n, e2, false, n0));
		assertSame(n2, nextInTree(n, e2, true, n0));
		
		assertSame(n4, nextInTree(n, e3, false, n0));
		assertSame(n3, nextInTree(n, e3, true, n0));
		
		assertSame(n0, nextInTree(n, e4, false, n0));
		assertSame(n4, nextInTree(n, e4, true, n0));
	}

	public void testN9() {
		// your code should "miss" the bad tree structure here
		Node bad1 = newNode(e2, null, null);
		Node bad2 = newNode(e2, bad1, bad1);
		bad1.setLeft(bad2);
		bad1.setRight(bad2);
		
		Node n4 = newNode(e4, newNode(e3, null, null), bad1);
		Node n5 = newNode(e5, n4, bad2);
		Node n3 = newNode(e3, bad1, n5);
		Node n = n3;
		
		assertSame(n4, nextInTree(n, e3, false, bad1));
	}

	
	/// testRx: tests of allInRange
	
	public void testR0() {
		assertReporting(true, () -> allInRange(null, null, null));
		assertReporting(true, () -> allInRange(null, null, e1));
		assertReporting(true, () -> allInRange(null, e2, null));
		assertReporting(true, () -> allInRange(null, e1, e2));
		assertReporting(true, () -> allInRange(null, e1, e1));
		assertReporting(true, () -> allInRange(null, e2, e1));
	}
	
	public void testR1() {
		Node n = newNode(e3, null, null);
		assertReporting(true, () -> allInRange(n, null, null));
		assertReporting(true, () -> allInRange(n, e2, null));
		assertReporting(false,() -> allInRange(n, e4, null));
		assertReporting(true, () -> allInRange(n, e3, null));
		assertReporting(true, () -> allInRange(n, null, e4));
		assertReporting(false,() -> allInRange(n, null, e2));
		assertReporting(false,() -> allInRange(n, null, e3));
		assertReporting(false,() -> allInRange(n, e3, e3));
		assertReporting(true, () -> allInRange(n, e2, e4));
		assertReporting(true, () -> allInRange(n, e3, e4));
	}
	
	public void testR2() {
		Node n2 = newNode(e2, null, null);
		Node n4 = newNode(e4, n2, null);
		assertReporting(true, () -> allInRange(n4, null, null));
		assertReporting(true, () -> allInRange(n4, e1, null));
		assertReporting(true, () -> allInRange(n4, e2, null));
		assertReporting(false,() -> allInRange(n4, e3, null));
		assertReporting(true, () -> allInRange(n4, null, e5));
		assertReporting(false,() -> allInRange(n4, null, e4));
		assertReporting(true, () -> allInRange(n4, e1, e5));
		assertReporting(false,() -> allInRange(n4, e1, e4));
		assertReporting(true, () -> allInRange(n4, e2, e5));
		assertReporting(false,() -> allInRange(n4, e3, e5));			
	}
	
	public void testR3() {
		Node n2 = newNode(e2, null, null);
		Node n4 = newNode(e4, null, n2);
		assertReporting(false,() -> allInRange(n4, null, null));
		assertReporting(false,() -> allInRange(n4, e1, null));
		assertReporting(false,() -> allInRange(n4, null, e5));
		assertReporting(false,() -> allInRange(n4, e1, e5));			
	}
	
	public void testR4() {
		Node n3 = newNode(e3, null, null);
		Node n3a = newNode(e3, n3, null);
		Node n3b = newNode(e3, null, n3);
		assertReporting(false,() -> allInRange(n3a, null, null));
		assertReporting(true, () -> allInRange(n3b, null, null));
		assertReporting(false,() -> allInRange(n3a, e1, e5));
		assertReporting(true, () -> allInRange(n3b, e1, e5));
		assertReporting(false,() -> allInRange(n3a, e3, e4));
		assertReporting(true, () -> allInRange(n3b, e3, e4));
		assertReporting(false,() -> allInRange(n3b, e3, e3a));			
	}
	
	public void testR5() {
		Node n1 = newNode(e1, null, null);
		Node n2 = newNode(e2, null, null);
		Node n5 = newNode(e5, null, null);
		Node n4 = newNode(e4, null, null);
		Node n3 = newNode(e3, n2, n4);
		
		assertReporting(true, () -> allInRange(n3, null, null));
		assertReporting(false,() -> allInRange(n3, e3, null));
		assertReporting(true, () -> allInRange(n3, e2, e5));
		assertReporting(false,() -> allInRange(n3, null, e4));
		
		n2.setRight(n5);
		assertReporting(false,() -> allInRange(n3, null, null));
		assertReporting(false,() -> allInRange(n3, e1, null));
		n2.setRight(null);
		
		n4.setLeft(n1);
		assertReporting(false,() -> allInRange(n3, null, null));
		assertReporting(false,() -> allInRange(n3, null, e5));
		n4.setLeft(null);
		
		n2.setLeft(n1);
		n4.setRight(n5);
		assertReporting(true, () -> allInRange(n3, null, null));
	}
	
	public void testR6() {
		Node n3 = newNode(e3, null, null);
		Node n2 = newNode(e2, null, n3);
		Node n1 = newNode(e1, null, n2);
		Node n4 = newNode(e4, n1, null);
		Node n5 = newNode(e5, null, null);
		
		assertReporting(true, () -> allInRange(n4, e1, null));
		assertReporting(false,() -> allInRange(n4, e2, null));
		assertReporting(true, () -> allInRange(n4, null, e5));
		assertReporting(false,() -> allInRange(n4, null, e4));
		
		n3.setRight(n5);
		assertReporting(false,() -> allInRange(n4, e1, null));
		assertReporting(false,() -> allInRange(n4, null, null));
	}
	
	public void testR7() {
		Appointment e1a = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1b = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1c = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1d = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Node n1a = newNode(e1a, null, null);
		Node n2 = newNode(e2, n1a, null);
		Node n1b = newNode(e1b, null, n2);
		Node n3 = newNode(e3, n1b, null);
		Node n1c = newNode(e1c, null, n3);
		Node n4 = newNode(e4, n1c, null);
		Node n1d = newNode(e1d, null, n4);
		
		Node n = n1d;
		
		assertReporting(true, () -> allInRange(n, e1, null));
		assertReporting(false,() -> allInRange(n, null, e4));
		assertReporting(true, () -> allInRange(n, e1, e5));
	}
	
	public void testR8() {
		Node n0 = newNode(null, null, null);
		assertReporting(false,() -> allInRange(n0, null, null));
		assertReporting(false,() -> allInRange(n0, e1, e5));
		
		Node n2 = newNode(e2, null, null);
		Node n1 = newNode(e1, null, n2);
		Node n4 = newNode(e4, null, null);
		Node n5 = newNode(e5, n4, null);
		Node n3 = newNode(e3, n1, n5);
		
		assertReporting(true, () -> allInRange(n3, e1, null));
		
		n1.setRight(n0);
		assertReporting(false, () -> allInRange(n3, e1, null));
		n1.setRight(n2);
		
		n5.setLeft(n0);
		assertReporting(false, () -> allInRange(n3, e1, null));
		n5.setLeft(n4);;
		
		assertReporting(true, () -> allInRange(n3, e1, null));
	}
	
	public void testR9() {
		Appointment e1a = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1b = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Appointment e1c = new Appointment(new Period(now,Duration.HOUR),"1: think");
		Node n2 = newNode(e2, null, null);
		Node n1a = newNode(e1b, null, n2);
		Node n1b = newNode(e1b, null, n1a);
		Node n3 = newNode(e3, n1b, null);
		Node n1c = newNode(e1c, null, n3);
		Node n1d = newNode(e1a, null, n1c);
		Node n4 = newNode(e4, n1d, null);
		Node n1e = newNode(e1b, null, n4);
		Node n1f = newNode(e1c, null, n1e);
		
		Node n = n1f;
		
		assertReporting(true, () -> allInRange(n, e1, null));
		assertReporting(false,() -> allInRange(n, null, e4));
		assertReporting(true, () -> allInRange(n, e1, e5));
		
		Node n1 = newNode(e1, null, null);
		n1a.setLeft(n1);
		assertReporting(false,() -> allInRange(n, null, null));
	}
	
	
	/// testWx: tests of wellFormed
	
	private void assertWellFormed(boolean expected) {
		assertReporting(expected, () -> wellFormed());
	}

	public void testW0() {
		setManyItems(1);
		assertWellFormed(false);
		setManyItems(0);
		assertWellFormed(true);
		setManyItems(-1);
		assertWellFormed(false);
	}
	
	public void testW1() {
		setCursor(newNode(e1, null, null));
		Node n1 = newNode(e1,null,null);
		assertWellFormed(false);
		setRoot(n1);
		setManyItems(1);
		assertWellFormed(false);
		setCursor(n1);
		assertWellFormed(true);
		setCursor(null);
		assertWellFormed(true);
	}
	
	public void testW2() {
		Node n1 = newNode(e2, null, null);
		Node n2 = newNode(e4, n1, null);
		setRoot(n2);
		setCursor(n1);
		setManyItems(2);
		assertWellFormed(true);
		setManyItems(3);
		assertWellFormed(false);
		n1.setRight(newNode(e5, null, null));
		assertWellFormed(false);
	}
	
	public void testW3() {
		Node n3 = newNode(e3, null, null);
		Node n3a = newNode(e3a, null, n3);
		Node n4 = newNode(e4, n3a, null);
		setRoot(n4);
		setManyItems(3);
		assertWellFormed(true);
		
		n3.setRight(n3a);
		assertWellFormed(false);
		
		setManyItems(5);
		assertWellFormed(false);
	}
	
	public void testW4() {
		Node n2 = newNode(e1, null, null);
		Node n1 = newNode(e1, null, n2);
		Node n4 = newNode(e3, null, null);
		Node n5 = newNode(e5, n4, null);
		Node n3 = newNode(e3, n1, n5);
		setRoot(n3);
		setManyItems(5);
		assertWellFormed(true);
		
		setCursor(n1);
		assertWellFormed(true);
		
		setCursor(n2);
		assertWellFormed(true);
		
		setCursor(n3);
		assertWellFormed(true);
		
		setCursor(n4);
		assertWellFormed(true);
		
		setCursor(n5);
		assertWellFormed(true);
		
		setCursor(newNode(e1, null, null));
		assertWellFormed(false);
	}
	
		
	public void testZ0() {
		new ApptBook();
	}
}
