package d.swan.mobileseller;

import android.content.res.Resources;

import java.util.ArrayList;

/**
 * Created by VIN on 20.06.2016.
 */
public final class PriceListFiller {

    private Resources resources;

    PriceListFiller(Resources resources) {
        this.resources = resources;
    }

    public ArrayList<Point> getRetailPrice() {
        ArrayList<Point> result = new ArrayList<>();
        String[] name_array = resources.getStringArray(R.array.name_array);
        String[] retail_array = resources.getStringArray(R.array.retail_array);
        int count = 0;
        for (String name : name_array)
            result.add(new Point(name, Float.parseFloat(retail_array[count++]), 0, 0));

        return result;
    }

    public ArrayList<Point> getWholesalePrice() {
        ArrayList<Point> result = new ArrayList<>();
        String[] name_array = resources.getStringArray(R.array.name_array);
        String[] wholesale_array = resources.getStringArray(R.array.wholesale_array);
        int count = 0;
        for (String name : name_array)
            result.add(new Point(name, Float.parseFloat(wholesale_array[count++]), 0, 0));

        return result;
    }
}
