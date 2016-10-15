package GUI;

/**
 * Created by andri on 03-Aug-16.
 */
public class InputManager {

    public static boolean onClick;
    public static boolean onMove;
    public static boolean onShift;
    public static boolean onSpace;
    public static boolean onDown;
    public static boolean onUp;
    public static boolean onRight;
    public static boolean onLeft;
    public static boolean onNum8;
    public static boolean onNum2;

    public static boolean onA;
    public static boolean onD;
    public static boolean onW;
    public static boolean onS;

    public static int[] clickPosition = new int[2];
    public static int[] position = new int[2];
    public static int[] shift = new int[2];


    public static void onMouseMove(int _x, int _y){
        onMove = true;
        shift[0] = _x - position[0];
        shift[1] = _y - position[1];
        position[0] = _x;
        position[1] = _y;
    }

    public static void onMouseClick(int _x, int _y, boolean click){
        onClick = click;
        clickPosition[0] = _x;
        clickPosition[1] = _y;
    }
}
