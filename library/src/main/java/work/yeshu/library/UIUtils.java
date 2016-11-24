package work.yeshu.library;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by yeshu on 2016/11/24.
 * ui utils
 */

public class UIUtils {

    public static int dp2px(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

