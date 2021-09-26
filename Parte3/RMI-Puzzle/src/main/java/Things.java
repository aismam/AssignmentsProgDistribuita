import java.util.ArrayList;
import java.util.List;

public class Things {

    public static void main(String[] args){

        List<Integer> as = new ArrayList<>();

        as.add(0);
        as.add(1);
        as.add(2);
        as.add(3);
        as.add(4);

        as.remove(2);
        as.add(2,222);

        System.out.println(as);

    }
}
