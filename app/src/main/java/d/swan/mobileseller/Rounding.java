package d.swan.mobileseller;

import java.math.BigDecimal;

/**
 * Created by daniel on 6/8/16.
 */
public class Rounding {

    public float round_up(float arg) {
        return new BigDecimal(arg).setScale(2, BigDecimal.ROUND_UP).floatValue();
    }

    public float round_down(float arg) {
        return new BigDecimal(arg).setScale(2, BigDecimal.ROUND_DOWN).floatValue();
    }
}
