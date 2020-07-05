package lectio.cal;

import junit.framework.TestCase;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author marvi
 */
public class CalculateEasterTest extends TestCase {

  Map<Integer, LocalDate> easterDays = new HashMap<Integer, LocalDate>();

  public CalculateEasterTest(String testName) {
    super(testName);
    populateDates();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testForYear() {
    for (Map.Entry<Integer, LocalDate> e : easterDays.entrySet()) {
      assertEquals("Should match", e.getValue(), CalculateEaster.forYear(e.getKey()));
    }
  }

  private void populateDates() {
    easterDays.put(1875, LocalDate.of(1875, 3, 28));
    easterDays.put(1876, LocalDate.of(1876, 4, 16));
    easterDays.put(1877, LocalDate.of(1877, 4, 1));
    easterDays.put(1878, LocalDate.of(1878, 4, 21));
    easterDays.put(1879, LocalDate.of(1879, 4, 13));
    easterDays.put(1880, LocalDate.of(1880, 3, 28));
    easterDays.put(1881, LocalDate.of(1881, 4, 17));
    easterDays.put(1882, LocalDate.of(1882, 4, 9));
    easterDays.put(1883, LocalDate.of(1883, 3, 25));
    easterDays.put(1884, LocalDate.of(1884, 4, 13));
    easterDays.put(1885, LocalDate.of(1885, 4, 5));
    easterDays.put(1886, LocalDate.of(1886, 4, 25));
    easterDays.put(1887, LocalDate.of(1887, 4, 10));
    easterDays.put(1888, LocalDate.of(1888, 4, 1));
    easterDays.put(1889, LocalDate.of(1889, 4, 21));
    easterDays.put(1890, LocalDate.of(1890, 4, 6));
    easterDays.put(1891, LocalDate.of(1891, 3, 29));
    easterDays.put(1892, LocalDate.of(1892, 4, 17));
    easterDays.put(1893, LocalDate.of(1893, 4, 2));
    easterDays.put(1894, LocalDate.of(1894, 3, 25));
    easterDays.put(1895, LocalDate.of(1895, 4, 14));
    easterDays.put(1896, LocalDate.of(1896, 4, 5));
    easterDays.put(1897, LocalDate.of(1897, 4, 18));
    easterDays.put(1898, LocalDate.of(1898, 4, 10));
    easterDays.put(1899, LocalDate.of(1899, 4, 2));
    easterDays.put(1900, LocalDate.of(1900, 4, 15));
    easterDays.put(1901, LocalDate.of(1901, 4, 7));
    easterDays.put(1902, LocalDate.of(1902, 3, 30));
    easterDays.put(1903, LocalDate.of(1903, 4, 12));
    easterDays.put(1904, LocalDate.of(1904, 4, 3));
    easterDays.put(1905, LocalDate.of(1905, 4, 23));
    easterDays.put(1906, LocalDate.of(1906, 4, 15));
    easterDays.put(1907, LocalDate.of(1907, 3, 31));
    easterDays.put(1908, LocalDate.of(1908, 4, 19));
    easterDays.put(1909, LocalDate.of(1909, 4, 11));
    easterDays.put(1910, LocalDate.of(1910, 3, 27));
    easterDays.put(1911, LocalDate.of(1911, 4, 16));
    easterDays.put(1912, LocalDate.of(1912, 4, 7));
    easterDays.put(1913, LocalDate.of(1913, 3, 23));
    easterDays.put(1914, LocalDate.of(1914, 4, 12));
    easterDays.put(1915, LocalDate.of(1915, 4, 4));
    easterDays.put(1916, LocalDate.of(1916, 4, 23));
    easterDays.put(1917, LocalDate.of(1917, 4, 8));
    easterDays.put(1918, LocalDate.of(1918, 3, 31));
    easterDays.put(1919, LocalDate.of(1919, 4, 20));
    easterDays.put(1920, LocalDate.of(1920, 4, 4));
    easterDays.put(1921, LocalDate.of(1921, 3, 27));
    easterDays.put(1922, LocalDate.of(1922, 4, 16));
    easterDays.put(1923, LocalDate.of(1923, 4, 1));
    easterDays.put(1924, LocalDate.of(1924, 4, 20));
    easterDays.put(1925, LocalDate.of(1925, 4, 12));
    easterDays.put(1926, LocalDate.of(1926, 4, 4));
    easterDays.put(1927, LocalDate.of(1927, 4, 17));
    easterDays.put(1928, LocalDate.of(1928, 4, 8));
    easterDays.put(1929, LocalDate.of(1929, 3, 31));
    easterDays.put(1930, LocalDate.of(1930, 4, 20));
    easterDays.put(1931, LocalDate.of(1931, 4, 5));
    easterDays.put(1932, LocalDate.of(1932, 3, 27));
    easterDays.put(1933, LocalDate.of(1933, 4, 16));
    easterDays.put(1934, LocalDate.of(1934, 4, 1));
    easterDays.put(1935, LocalDate.of(1935, 4, 21));
    easterDays.put(1936, LocalDate.of(1936, 4, 12));
    easterDays.put(1937, LocalDate.of(1937, 3, 28));
    easterDays.put(1938, LocalDate.of(1938, 4, 17));
    easterDays.put(1939, LocalDate.of(1939, 4, 9));
    easterDays.put(1940, LocalDate.of(1940, 3, 24));
    easterDays.put(1941, LocalDate.of(1941, 4, 13));
    easterDays.put(1942, LocalDate.of(1942, 4, 5));
    easterDays.put(1943, LocalDate.of(1943, 4, 25));
    easterDays.put(1944, LocalDate.of(1944, 4, 9));
    easterDays.put(1945, LocalDate.of(1945, 4, 1));
    easterDays.put(1946, LocalDate.of(1946, 4, 21));
    easterDays.put(1947, LocalDate.of(1947, 4, 6));
    easterDays.put(1948, LocalDate.of(1948, 3, 28));
    easterDays.put(1949, LocalDate.of(1949, 4, 17));
    easterDays.put(1950, LocalDate.of(1950, 4, 9));
    easterDays.put(1951, LocalDate.of(1951, 3, 25));
    easterDays.put(1952, LocalDate.of(1952, 4, 13));
    easterDays.put(1953, LocalDate.of(1953, 4, 5));
    easterDays.put(1954, LocalDate.of(1954, 4, 18));
    easterDays.put(1955, LocalDate.of(1955, 4, 10));
    easterDays.put(1956, LocalDate.of(1956, 4, 1));
    easterDays.put(1957, LocalDate.of(1957, 4, 21));
    easterDays.put(1958, LocalDate.of(1958, 4, 6));
    easterDays.put(1959, LocalDate.of(1959, 3, 29));
    easterDays.put(1960, LocalDate.of(1960, 4, 17));
    easterDays.put(1961, LocalDate.of(1961, 4, 2));
    easterDays.put(1962, LocalDate.of(1962, 4, 22));
    easterDays.put(1963, LocalDate.of(1963, 4, 14));
    easterDays.put(1964, LocalDate.of(1964, 3, 29));
    easterDays.put(1965, LocalDate.of(1965, 4, 18));
    easterDays.put(1966, LocalDate.of(1966, 4, 10));
    easterDays.put(1967, LocalDate.of(1967, 3, 26));
    easterDays.put(1968, LocalDate.of(1968, 4, 14));
    easterDays.put(1969, LocalDate.of(1969, 4, 6));
    easterDays.put(1970, LocalDate.of(1970, 3, 29));
    easterDays.put(1971, LocalDate.of(1971, 4, 11));
    easterDays.put(1972, LocalDate.of(1972, 4, 2));
    easterDays.put(1973, LocalDate.of(1973, 4, 22));
    easterDays.put(1974, LocalDate.of(1974, 4, 14));
    easterDays.put(1975, LocalDate.of(1975, 3, 30));
    easterDays.put(1976, LocalDate.of(1976, 4, 18));
    easterDays.put(1977, LocalDate.of(1977, 4, 10));
    easterDays.put(1978, LocalDate.of(1978, 3, 26));
    easterDays.put(1979, LocalDate.of(1979, 4, 15));
    easterDays.put(1980, LocalDate.of(1980, 4, 6));
    easterDays.put(1981, LocalDate.of(1981, 4, 19));
    easterDays.put(1982, LocalDate.of(1982, 4, 11));
    easterDays.put(1983, LocalDate.of(1983, 4, 3));
    easterDays.put(1984, LocalDate.of(1984, 4, 22));
    easterDays.put(1985, LocalDate.of(1985, 4, 7));
    easterDays.put(1986, LocalDate.of(1986, 3, 30));
    easterDays.put(1987, LocalDate.of(1987, 4, 19));
    easterDays.put(1988, LocalDate.of(1988, 4, 3));
    easterDays.put(1989, LocalDate.of(1989, 3, 26));
    easterDays.put(1990, LocalDate.of(1990, 4, 15));
    easterDays.put(1991, LocalDate.of(1991, 3, 31));
    easterDays.put(1992, LocalDate.of(1992, 4, 19));
    easterDays.put(1993, LocalDate.of(1993, 4, 11));
    easterDays.put(1994, LocalDate.of(1994, 4, 3));
    easterDays.put(1995, LocalDate.of(1995, 4, 16));
    easterDays.put(1996, LocalDate.of(1996, 4, 7));
    easterDays.put(1997, LocalDate.of(1997, 3, 30));
    easterDays.put(1998, LocalDate.of(1998, 4, 12));
    easterDays.put(1999, LocalDate.of(1999, 4, 4));
    easterDays.put(2000, LocalDate.of(2000, 4, 23));
    easterDays.put(2001, LocalDate.of(2001, 4, 15));
    easterDays.put(2002, LocalDate.of(2002, 3, 31));
    easterDays.put(2003, LocalDate.of(2003, 4, 20));
    easterDays.put(2004, LocalDate.of(2004, 4, 11));
    easterDays.put(2005, LocalDate.of(2005, 3, 27));
    easterDays.put(2006, LocalDate.of(2006, 4, 16));
    easterDays.put(2007, LocalDate.of(2007, 4, 8));
    easterDays.put(2008, LocalDate.of(2008, 3, 23));
    easterDays.put(2009, LocalDate.of(2009, 4, 12));
    easterDays.put(2010, LocalDate.of(2010, 4, 4));
    easterDays.put(2011, LocalDate.of(2011, 4, 24));
    easterDays.put(2012, LocalDate.of(2012, 4, 8));
    easterDays.put(2013, LocalDate.of(2013, 3, 31));
    easterDays.put(2014, LocalDate.of(2014, 4, 20));
    easterDays.put(2015, LocalDate.of(2015, 4, 5));
    easterDays.put(2016, LocalDate.of(2016, 3, 27));
    easterDays.put(2017, LocalDate.of(2017, 4, 16));
    easterDays.put(2018, LocalDate.of(2018, 4, 1));
    easterDays.put(2019, LocalDate.of(2019, 4, 21));
    easterDays.put(2020, LocalDate.of(2020, 4, 12));
    easterDays.put(2021, LocalDate.of(2021, 4, 4));
    easterDays.put(2022, LocalDate.of(2022, 4, 17));
    easterDays.put(2023, LocalDate.of(2023, 4, 9));
    easterDays.put(2024, LocalDate.of(2024, 3, 31));
    easterDays.put(2025, LocalDate.of(2025, 4, 20));
    easterDays.put(2026, LocalDate.of(2026, 4, 5));
    easterDays.put(2027, LocalDate.of(2027, 3, 28));
    easterDays.put(2028, LocalDate.of(2028, 4, 16));
    easterDays.put(2029, LocalDate.of(2029, 4, 1));
    easterDays.put(2030, LocalDate.of(2030, 4, 21));
    easterDays.put(2031, LocalDate.of(2031, 4, 13));
    easterDays.put(2032, LocalDate.of(2032, 3, 28));
    easterDays.put(2033, LocalDate.of(2033, 4, 17));
    easterDays.put(2034, LocalDate.of(2034, 4, 9));
    easterDays.put(2035, LocalDate.of(2035, 3, 25));
    easterDays.put(2036, LocalDate.of(2036, 4, 13));
    easterDays.put(2037, LocalDate.of(2037, 4, 5));
    easterDays.put(2038, LocalDate.of(2038, 4, 25));
    easterDays.put(2039, LocalDate.of(2039, 4, 10));
    easterDays.put(2040, LocalDate.of(2040, 4, 1));
    easterDays.put(2041, LocalDate.of(2041, 4, 21));
    easterDays.put(2042, LocalDate.of(2042, 4, 6));
    easterDays.put(2043, LocalDate.of(2043, 3, 29));
    easterDays.put(2044, LocalDate.of(2044, 4, 17));
    easterDays.put(2045, LocalDate.of(2045, 4, 9));
    easterDays.put(2046, LocalDate.of(2046, 3, 25));
    easterDays.put(2047, LocalDate.of(2047, 4, 14));
    easterDays.put(2048, LocalDate.of(2048, 4, 5));
    easterDays.put(2049, LocalDate.of(2049, 4, 18));
    easterDays.put(2050, LocalDate.of(2050, 4, 10));
    easterDays.put(2051, LocalDate.of(2051, 4, 2));
    easterDays.put(2052, LocalDate.of(2052, 4, 21));
    easterDays.put(2053, LocalDate.of(2053, 4, 6));
    easterDays.put(2054, LocalDate.of(2054, 3, 29));
    easterDays.put(2055, LocalDate.of(2055, 4, 18));
    easterDays.put(2056, LocalDate.of(2056, 4, 2));
    easterDays.put(2057, LocalDate.of(2057, 4, 22));
    easterDays.put(2058, LocalDate.of(2058, 4, 14));
    easterDays.put(2059, LocalDate.of(2059, 3, 30));
    easterDays.put(2060, LocalDate.of(2060, 4, 18));
    easterDays.put(2061, LocalDate.of(2061, 4, 10));
    easterDays.put(2062, LocalDate.of(2062, 3, 26));
    easterDays.put(2063, LocalDate.of(2063, 4, 15));
    easterDays.put(2064, LocalDate.of(2064, 4, 6));
    easterDays.put(2065, LocalDate.of(2065, 3, 29));
    easterDays.put(2066, LocalDate.of(2066, 4, 11));
    easterDays.put(2067, LocalDate.of(2067, 4, 3));
    easterDays.put(2068, LocalDate.of(2068, 4, 22));
    easterDays.put(2069, LocalDate.of(2069, 4, 14));
    easterDays.put(2070, LocalDate.of(2070, 3, 30));
    easterDays.put(2071, LocalDate.of(2071, 4, 19));
    easterDays.put(2072, LocalDate.of(2072, 4, 10));
    easterDays.put(2073, LocalDate.of(2073, 3, 26));
    easterDays.put(2074, LocalDate.of(2074, 4, 15));
    easterDays.put(2075, LocalDate.of(2075, 4, 7));
    easterDays.put(2076, LocalDate.of(2076, 4, 19));
    easterDays.put(2077, LocalDate.of(2077, 4, 11));
    easterDays.put(2078, LocalDate.of(2078, 4, 3));
    easterDays.put(2079, LocalDate.of(2079, 4, 23));
    easterDays.put(2080, LocalDate.of(2080, 4, 7));
    easterDays.put(2081, LocalDate.of(2081, 3, 30));
    easterDays.put(2082, LocalDate.of(2082, 4, 19));
    easterDays.put(2083, LocalDate.of(2083, 4, 4));
    easterDays.put(2084, LocalDate.of(2084, 3, 26));
    easterDays.put(2085, LocalDate.of(2085, 4, 15));
    easterDays.put(2086, LocalDate.of(2086, 3, 31));
    easterDays.put(2087, LocalDate.of(2087, 4, 20));
    easterDays.put(2088, LocalDate.of(2088, 4, 11));
    easterDays.put(2089, LocalDate.of(2089, 4, 3));
    easterDays.put(2090, LocalDate.of(2090, 4, 16));
    easterDays.put(2091, LocalDate.of(2091, 4, 8));
    easterDays.put(2092, LocalDate.of(2092, 3, 30));
    easterDays.put(2093, LocalDate.of(2093, 4, 12));
    easterDays.put(2094, LocalDate.of(2094, 4, 4));
    easterDays.put(2095, LocalDate.of(2095, 4, 24));
    easterDays.put(2096, LocalDate.of(2096, 4, 15));
    easterDays.put(2097, LocalDate.of(2097, 3, 31));
    easterDays.put(2098, LocalDate.of(2098, 4, 20));
    easterDays.put(2099, LocalDate.of(2099, 4, 12));
    easterDays.put(2100, LocalDate.of(2100, 3, 28));
    easterDays.put(2101, LocalDate.of(2101, 4, 17));
    easterDays.put(2102, LocalDate.of(2102, 4, 9));
    easterDays.put(2103, LocalDate.of(2103, 3, 25));
    easterDays.put(2104, LocalDate.of(2104, 4, 13));
    easterDays.put(2105, LocalDate.of(2105, 4, 5));
    easterDays.put(2106, LocalDate.of(2106, 4, 18));
    easterDays.put(2107, LocalDate.of(2107, 4, 10));
    easterDays.put(2108, LocalDate.of(2108, 4, 1));
    easterDays.put(2109, LocalDate.of(2109, 4, 21));
    easterDays.put(2110, LocalDate.of(2110, 4, 6));
    easterDays.put(2111, LocalDate.of(2111, 3, 29));
    easterDays.put(2112, LocalDate.of(2112, 4, 17));
    easterDays.put(2113, LocalDate.of(2113, 4, 2));
    easterDays.put(2114, LocalDate.of(2114, 4, 22));
    easterDays.put(2115, LocalDate.of(2115, 4, 14));
    easterDays.put(2116, LocalDate.of(2116, 3, 29));
    easterDays.put(2117, LocalDate.of(2117, 4, 18));
    easterDays.put(2118, LocalDate.of(2118, 4, 10));
    easterDays.put(2119, LocalDate.of(2119, 3, 26));
    easterDays.put(2120, LocalDate.of(2120, 4, 14));
    easterDays.put(2121, LocalDate.of(2121, 4, 6));
    easterDays.put(2122, LocalDate.of(2122, 3, 29));
    easterDays.put(2123, LocalDate.of(2123, 4, 11));
    easterDays.put(2124, LocalDate.of(2124, 4, 2));
  }
}
