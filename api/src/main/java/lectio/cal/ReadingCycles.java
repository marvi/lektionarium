package lectio.cal;

import java.util.HashMap;
import java.util.Map;

public class ReadingCycles {

  private Map<String, Map<Cycle, Readings>> cycles;

  public ReadingCycles() {
    this.cycles = new HashMap<>();
  }

  public void addCycle(String holyDay, int c, Readings r) {
    cycles.putIfAbsent(holyDay, new HashMap<>());
    cycles.get(holyDay).putIfAbsent(Cycle.from(c), r);
  }

  public Readings readingsForCycle(String holyDay, int cycle) {
    Cycle c = Cycle.from(cycle);
    return cycles.get(holyDay).get(c);
  }

  public boolean hasReadings(String name) {
    return(cycles.containsKey(name));
  }


  public enum Cycle {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);

    public int getValue() {
      return value;
    }

    int value;

    Cycle(int value) {
      this.value = value;
    }

    private static final Map<Integer, ReadingCycles.Cycle> _map = new HashMap<>();

    static {
      for (ReadingCycles.Cycle cycle : ReadingCycles.Cycle.values())
        _map.put(cycle.value, cycle);
    }

    public static ReadingCycles.Cycle from(int value) {
      return _map.get(value);
    }
  }

}

