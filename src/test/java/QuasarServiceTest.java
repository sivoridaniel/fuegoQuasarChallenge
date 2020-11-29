import com.challenge.meli.properties.QASConfig;
import com.challenge.meli.utils.cache.QasCache;
import fj.data.Array;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import com.challenge.meli.service.QuasarService;

@RunWith(SpringRunner.class)
public class QuasarServiceTest {

    private QuasarService quasarService;
    @MockBean
    private QASConfig qasConfigProperties;

    @Before
    public void setup() {

        Float[] positionKenobi = {-500f, -200f};
        Float[] positionSkywalker = {100f, -100f};
        Float[] positionSato = {500f, 100f};

        BDDMockito.given(qasConfigProperties.getPositionSatelliteKenovi())
                  .willReturn(positionKenobi);
        BDDMockito.given(qasConfigProperties.getPositionSatelliteSkywalker())
                  .willReturn(positionSkywalker);
        BDDMockito.given(qasConfigProperties.getPositionSatelliteSato())
                  .willReturn(positionSato);
        BDDMockito.given(qasConfigProperties.getExpirationTimeCache())
                  .willReturn(60000L);

        this.quasarService = new QuasarService(qasConfigProperties);
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
