package lectio.readings;

import junit.framework.TestCase;
import lectio.cal.LectioRepository;
import lectio.cal.ReadingCycles;
import lectio.cal.Readings;

import java.util.Map;

/**
 * @author marvi
 */
public class TestLoadReadings extends TestCase {

  public TestLoadReadings(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testLoadReadings() {
   ReadingCycles result = LectioRepository.getLectio();
   Readings r = result.readingsForCycle("Domssöndagen", ReadingCycles.Cycle.FIRST.getValue());
   assertNotNull(r.getGo().getSweRef());

  }
}
