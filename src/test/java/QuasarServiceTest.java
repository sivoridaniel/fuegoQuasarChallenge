import fj.data.Array;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import com.challenge.meli.service.QuasarService;

@RunWith(SpringRunner.class)
public class QuasarServiceTest {

    private QuasarService quasarService;

    @Before
    public void setup() {
        float[] positionKenobi = {-500f, -200f};
        float[] positionSkywalker = {100f, -100f};
        float[] positionSato = {500f, 100f};
        this.quasarService = new QuasarService(positionKenobi, positionSkywalker, positionSato);
    }

    @Test
    public void cantInterpretedMessage(){

        Array<String> messageKen = Array.array("","","", "", "un", "");
        Array<String> messageSky = Array.array("","", "es", "un", "");
        Array<String> messageSato = Array.array("Este", "", "", "");
        Array<Array<String>> satelliteMessages = Array.array(messageKen, messageSky, messageSato);

        String interpretedMessage = this.quasarService.getMessage(satelliteMessages);

        Assert.assertTrue(interpretedMessage.isEmpty());

    }


}
