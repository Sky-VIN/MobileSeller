package d.swan.mobileseller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by VIN on 07.06.2016.
 */
public class PointAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Point> points;

    public PointAdapter(Context context, ArrayList<Point> points) {
        this.context = context;
        this.points = points;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return points.size();
    }

    @Override
    public Object getItem(int position) {
        return points.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Point getPoint(int position) {
        return ((Point) getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = layoutInflater.inflate(R.layout.row, parent, false);

        Point point = getPoint(position);
        ((TextView) view.findViewById(R.id.tvName)).setText(point.name);
        ((TextView) view.findViewById(R.id.tvAmount)).setText(String.valueOf(point.amount + " шт"));
        ((TextView) view.findViewById(R.id.tvPriceUnit)).setText(String.valueOf(point.priceUnit + " грн/ед"));
        ((TextView) view.findViewById(R.id.tvPriceTotal)).setText(String.valueOf(point.priceTotal + " грн"));

        return view;
    }

    public float getSummary() {
        float result = 0;
        for (Point point : points)
            if (point.amount > 0)
                result += point.priceUnit * point.amount;

        return new Rounding().round_up(result);
    }

    public ArrayList<Point> getSelectedPoints() {
        ArrayList<Point> result = new ArrayList<>();

        for (Point point : points)
            if (point.amount > 0)
                result.add(point);

        return result;
    }
}
