package d.swan.mobileseller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VIN on 07.06.2016.
 */
public class Point implements Parcelable {

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

    private Point(Parcel in) {
        this.name = in.readString();
        this.hatch = in.readInt();
        this.article = in.readInt();
        this.amount = in.readInt();
        this.priceUnit = in.readFloat();
        this.priceTotal = in.readFloat();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(hatch);
        parcel.writeInt(article);
        parcel.writeInt(amount);
        parcel.writeFloat(priceUnit);
        parcel.writeFloat(priceTotal);
    }
}
