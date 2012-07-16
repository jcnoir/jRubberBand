import org.black.jtranscribe.generated.rubberband.RubberbandLibrary;
import org.junit.Test;


/**
 * User: jcnoir
 * Date: 14/07/12
 */
public class TestJni {


    @Test
    public void test_bridj_c() {

        RubberbandLibrary.RubberBandState state;
        state = RubberbandLibrary.rubberband_new(44100, 2, 0, 1.0, 1.0);
        System.out.println("INIT done !");
        RubberbandLibrary.rubberband_reset(state);
        System.out.println("Available ? : " + RubberbandLibrary.rubberband_available(state));
    }


}
