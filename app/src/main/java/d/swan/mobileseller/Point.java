package d.swan.mobileseller;

/**
 * Created by VIN on 07.06.2016.
 */
public class Point {
    String name;
    int hatch, article, amount;
    float priceUnit, priceTotal;

    Point(String name, int hatch, int article, float priceUnit, float priceTotal, int amount) {
        this.name = name;
        this.hatch = hatch;
        this.article = article;
        this.priceUnit = priceUnit;
        this.priceTotal = priceTotal;
        this.amount = amount;
    }
}
