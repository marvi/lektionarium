package lectio.cal;

import java.time.LocalDate;
import java.util.List;

public interface LiturgicalDay {
    String name();
    LocalDate date();
    List<Memorial> memorials();
    LiturgicalColor liturgicalColor();
}

