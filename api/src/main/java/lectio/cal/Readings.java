package lectio.cal;

public class Readings {

  String theme;

  Reading ot;
  Reading ep;
  Reading go;
  Reading ps;
  Reading alt;

  public Readings(String theme, Reading ot, Reading ep, Reading go, Reading ps, Reading alt) {
    this.theme = theme;
    this.ot = ot;
    this.ep = ep;
    this.go = go;
    this.ps = ps;
    this.alt = alt;
  }

  public String getTheme() {
    return theme;
  }

  public Reading getOt() {
    return ot;
  }

  public Reading getEp() {
    return ep;
  }

  public Reading getGo() {
    return go;
  }

  public Reading getPs() {
    return ps;
  }

  public Reading getAlt() {
    return alt;
  }

}
