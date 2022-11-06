import java.util.Random;

import edu.uwm.cs351.Appointment;
import edu.uwm.cs351.ApptBook;
import edu.uwm.cs351.Duration;
import edu.uwm.cs351.Period;
import edu.uwm.cs351.Time;
import junit.framework.TestCase;


public class TestEfficiency extends TestCase {
	ApptBook b;
    private Random random;
    
    private static final int POWER = 21; // 1 million entries
    private static final int TESTS = 100000;
    
    Time now = new Time();
    
	private Appointment a(int i) {
		return new Appointment(new Period(now,Duration.SECOND.scale(i+1)),"zzz");
	}

	protected void setUp() throws Exception {
		super.setUp();
		random = new Random();
		try {
			assert b.size() == TESTS : "cannot run test with assertions enabled";
		} catch (NullPointerException ex) {
			throw new IllegalStateException("Cannot run test with assertions enabled");
		}
		b = new ApptBook();
		int max = (1 << (POWER)); // 2^(POWER) = 2 million
		for (int power = POWER; power > 1; --power) {
			int incr = 1 << power;
			for (int i=1 << (power-1); i < max; i += incr) {
				b.insert(a(i));
			}
		}
	}
    
    @Override
    protected void tearDown() {
    	b = null;
    }

    public void testA() {
    	for (int i=0; i < TESTS; ++i) {
    		assertEquals((1<<(POWER-1))-1,b.size());
    	}
    }
    
    public void testB() {
    	for (int i=0; i < TESTS; ++i) {
    		b.start();
    		assertEquals(a(2),b.getCurrent());
    	}
    }
    
    public void testC() {
    	b.start();
    	for (int i=0; i < TESTS; ++i) {
    		assertEquals(a(i*2+2),b.getCurrent());
    		b.advance();
    	}
    }
    
    public void testD() {
    	for (int i=0; i < TESTS; ++i) {
    		int r = random.nextInt(TESTS)+1;
    		b.setCurrent(a(r*2));
    		assertEquals(a(r*2),b.getCurrent());
    		b.setCurrent(a(r*2+1));
    		assertEquals(a(r*2+2),b.getCurrent());
    	}
    }

    public void testE() {
    	ApptBook c = b.clone();
    	assertEquals((1<<(POWER-1))-1, c.size());
    }
    
    public void testF() {
    	ApptBook d = new ApptBook();
    	d.insert(a((1<<(POWER-1))-1));
    	d.insertAll(b);
    	assertEquals((1<<(POWER-1)), d.size());
    }
}
