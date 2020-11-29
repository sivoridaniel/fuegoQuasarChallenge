package com.challenge.meli.service;

import com.challenge.meli.properties.QASConfig;
import com.challenge.meli.utils.cache.QasCache;
import fj.F;
import fj.F3;
import fj.P2;
import fj.data.Array;
import fj.data.List;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;

import static fj.P.p;
import static fj.data.List.list;
import static java.lang.Math.pow;

@Service
public class QuasarService {

    private QASConfig qasConfig;
    private QasCache<String, SatelliteValue> qasCache;

    @Autowired
    public QuasarService(QASConfig qasConfig){
        this.qasConfig = qasConfig;
        this.qasCache = new QasCache<>(qasConfig.getExpirationTimeCache());
    }

    public Array<Float> getLocation(Array<Float> distances){

        Float P1[] = this.qasConfig.getPositionSatelliteKenovi();
        Float P2[] = this.qasConfig.getPositionSatelliteSkywalker();
        Float P3[] = this.qasConfig.getPositionSatelliteSkywalker();

        Float d1 = distances.get(0);
        Float d2 = distances.get(1);
        Float d3 = distances.get(2);

        Double[] ex = new Double[2];
        Double[] ey = new Double[2];
        Double[] p3p1 = new Double[2];
        Double jval = Double.valueOf(0);
        Double temp = Double.valueOf(0);
        Double ival = Double.valueOf(0);
        Double p3p1i = Double.valueOf(0);
        Float triptx;
        Float tripty;
        Double xval;
        Double yval;
        Double t1;
        Double t2;
        Double t3;
        Double t;
        Double exx;
        Double d;
        Double eyy;

        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i].doubleValue();
            t2 = P1[i].doubleValue();
            t = t1 - t2;
            temp += (t*t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i].doubleValue();
            t2 = P1[i].doubleValue();
            exx = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i].doubleValue();
            t2 = P1[i].doubleValue();
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i].doubleValue();
            t2 = P1[i].doubleValue();
            t3 = ex[i] * ival;
            t = t1 - t2 -t3;
            p3p1i += (t*t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i].doubleValue();
            t2 = P1[i].doubleValue();
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3)/Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1*t2);
        }
        xval = (pow(d1, 2) - pow(d2, 2) + pow(d, 2))/(2*d);
        yval = ((pow(d1, 2) - pow(d3, 2) + pow(ival, 2) + pow(jval, 2))/(2*jval)) - ((ival/jval)*xval);
        t1 = P1[0].doubleValue();
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = Precision.round(Double.valueOf(Double.sum(Double.sum(t1, t2) , t3)).floatValue(),2, RoundingMode.HALF_DOWN.ordinal());
        t1 = P1[1].doubleValue();
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        tripty = Precision.round(Double.valueOf(Double.sum(Double.sum (t1,t2), t3)).floatValue(),2, RoundingMode.HALF_DOWN.ordinal());
        Array<Float> result = Array.array(triptx, tripty);
        return result;
    }

    /** FUNCTIONS GET MESSAGE **/
    F<List<String>, Boolean> fValiLastWordMessage = (wordsne) -> wordsne.isNotEmpty()?
                                                                    wordsne.foldLeft((c, w) ->
                                                                    (wordsne.exists(wne -> wne.equalsIgnoreCase(w)))?c+1:c, 0)
                                                                    .compareTo(wordsne.length())==0 :
                                                                    false;

    F<Array<Array<String>>, Integer> fMaxSizeMessage = (aMessages) -> aMessages.foldLeft((s,m) -> m.length()>s?m.length():s, 0);

    F3<Array<String>, Integer, List<String>, List<String>> fBuildMessage =
            (aInputMessage, idxIt, aOutputMessage) -> {
                if(aInputMessage.length() > idxIt){
                    String word = aInputMessage.get(idxIt);
                    return (word.isEmpty()||
                            (!aOutputMessage.isEmpty() &&
                                    (aOutputMessage.index(0).equalsIgnoreCase(word) ||
                                            (aOutputMessage.length()>1) && aOutputMessage.index(1).equalsIgnoreCase(word))))?
                            aOutputMessage:aOutputMessage.cons(word);
                }
                return aOutputMessage;
            };

    /**
     * Use the functions fValiLastWordMessage, fMaxSizeMessage and fBuildMessage to get interpreted message
     * @param messages: Satellite messages from Kenobi, Skywalker and Sato
     * @return String: Interpreted message
     */
    public String getMessage(Array<Array<String>> messages){

        Array<String> messageKenWFB = messages.get(0).reverse();
        Array<String> messageSkyWFB = messages.get(1).reverse();
        Array<String> messageSatoWFB = messages.get(2).reverse();

        List<String> lastWordsNotEmpties = list(messageKenWFB.get(0), messageSkyWFB.get(0), messageSatoWFB.get(0))
                                           .removeAll(String::isEmpty);
        Boolean isMessageValid = fValiLastWordMessage.f(lastWordsNotEmpties);

        if(isMessageValid){
            Integer maxSize = fMaxSizeMessage.f(messages);
            List<Integer> sizes = List.range(0, maxSize);
            List<String> lWords= sizes.foldLeft((lw,i) -> {
                List<String> lw1 = fBuildMessage.f(messageKenWFB, i, lw);
                List<String> lw2 = fBuildMessage.f(messageSkyWFB, i, lw1);
                List<String> lw3 = fBuildMessage.f(messageSatoWFB, i, lw2);
                return lw3;
            }, list());
            return String.join(" ", lWords);
        }
        return "";
    }


    @Builder(toBuilder = true)
    @Getter
    static class SatelliteValue{
        private Array<Float> distance;
        private Array<String> message;
    }

    public P2<Array<Float>, String> getLocationAndMessage(String nameSatellite, Array<Float> distance,
                                                          Array<String> message) {
        qasCache.clean();
        SatelliteValue satInputValue = SatelliteValue.builder().distance(distance).message(message).build();
        qasCache.set(nameSatellite, satInputValue);
        List<String> satellitesNames = qasConfig.getSatellitesNames();

        if(qasCache.containsAll(satellitesNames)){
            P2<Array<Float>, Array<Array<String>>> pDistancesMessages = satellitesNames.foldLeft((p, sn) -> {
                SatelliteValue satItValue = qasCache.get(sn).some();
                return p(p._1().append(satItValue.getDistance()), p._2().append(Array.array(satItValue.getMessage())));
            }, p(Array.empty(), Array.empty()));

            return p(getLocation(pDistancesMessages._1()), getMessage(pDistancesMessages._2()));

        }

        return p(Array.empty(),"");
    }
}
