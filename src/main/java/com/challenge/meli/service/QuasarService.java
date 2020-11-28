package com.challenge.meli.service;

import fj.F;
import fj.F3;
import fj.data.Array;
import fj.data.List;
import org.springframework.stereotype.Service;

import static fj.data.List.list;
import static java.lang.Math.pow;

@Service
public class QuasarService {

    private float P1[];
    private float P2[];
    private float P3[];

    public QuasarService(float P1[], float P2[], float P3[]){
        this.P1 = P1;
        this.P2 = P2;
        this.P3 = P3;
    }

    public Array<Float> getLocation(Array<Float> distances){

        float d1 = distances.get(0);
        float d2 = distances.get(1);
        float d3 = distances.get(2);

        double[] ex = new double[2];
        double[] ey = new double[2];
        double[] p3p1 = new double[2];
        double jval = 0;
        double temp = 0;
        double ival = 0;
        double p3p1i = 0;
        float triptx;
        float tripty;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            t = t1 - t2;
            temp += (t*t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            exx = (t1 - t2)/(Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1*t2);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t = t1 - t2 -t3;
            p3p1i += (t*t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
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
        t1 = P1[0];
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = (float) (t1 + t2 + t3);
        t1 = P1[1];
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        tripty = (float) (t1 + t2 + t3);
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
            return lWords.toString();
        }
        return "";
    }

}
