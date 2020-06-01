import java.util.Arrays;
public class Solution {
    public static int[] solarPanels(int src) {
        int[] r = new int[0];
        while (src > 0) {
            int closestRoot = getClosestSquareRoot(src);

            if ((Math.pow(closestRoot, 2)) > src)
                closestRoot -= 1;

            int square = (int) Math.pow(closestRoot, 2);
            r = Arrays.copyOf(r, r.length+1);
            r[r.length -1] = square;
            src = src - square;
        }
        return r;
    }

    public static int getClosestSquareRoot(int src) {
        int r = (int) (Math.sqrt((double) src) + 0.5);
        return r;
    }
}
