package lectio.cal;

public class Reading {

  private String sweRef;
  private String enRef;
  private String text;

  public Reading(String sweRef, String enRef, String text) {
    this.sweRef = sweRef;
    this.enRef = enRef;
    this.text = text;
  }

  public Reading(String sweRef, String enRef) {
    this.sweRef = sweRef;
    this.enRef = enRef;
  }

  public String getSweRef() {
    return sweRef;
  }

  public String getEnRef() {
    return enRef;
  }

  public String getText() {
    return text;
  }

}
