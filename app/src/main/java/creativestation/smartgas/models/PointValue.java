package creativestation.smartgas.models;

public class PointValue{
    String tgl;
    int firstValue, lastValue;

    public PointValue(){
    }

    public PointValue(String tgl, int firstValue, int lastValue){
        this.tgl=tgl;
        this.firstValue=firstValue;
        this.lastValue=lastValue;
    }

    public String gettgl() {
        return tgl;
    }

    public int getFirstValue() {
        return firstValue;
    }

    public int getLastValue() {
        return lastValue;
    }
}
