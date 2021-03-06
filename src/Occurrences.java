import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.xml.sax.ContentHandler;

public class Occurrences {

    private Map<String, Integer> occurrences;
    private TreeMultimap<Integer, String> occurrencesSorted;

    private int totalOccurrences;

    public Occurrences(ContentHandler words){

        occurrences = new HashMap<String, Integer>();
        occurrencesSorted = TreeMultimap.create(Ordering.natural().reverse(),Ordering.natural());
        totalOccurrences= 0;

        String aux = "";
        boolean keep = false;
        boolean isLink = false;

        for (int i = 0; i < words.toString().length(); i++){

            //Parseamos el fichero correspondiente.

            if (isLink && words.toString().charAt(i) != ' ' && words.toString().charAt(i) != '"'){
                aux += words.toString().charAt(i);
            }

            else if (words.toString().charAt(i) != ' ' &&  words.toString().charAt(i) != ',' && words.toString().charAt(i) != '.'
                    && words.toString().charAt(i) != '\n' && words.toString().charAt(i) != '\t' && words.toString().charAt(i) != '·'
                    && words.toString().charAt(i) != '"' && words.toString().charAt(i) != ':' && !isLink
                    && words.toString().charAt(i) != '-' && words.toString().charAt(i) != '“' && words.toString().charAt(i) != ' '
                    && words.toString().charAt(i) != ';' && words.toString().charAt(i) != '(' && words.toString().charAt(i) != ')'
                    && words.toString().charAt(i) != '!' && words.toString().charAt(i) != '¡' && words.toString().charAt(i) != '?'
                    && words.toString().charAt(i) != '¿' && words.toString().charAt(i) != '”' && words.toString().charAt(i) != '*'){
                keep = true;
                aux += words.toString().charAt(i);

                if (aux.equals("http"))
                    isLink = true;
            }
            else if (keep){

                aux = aux.toLowerCase();

                if(occurrences.containsKey(aux)) {
                    occurrences.put(aux, occurrences.get(aux) + 1);
                }
                else{

                    occurrences.put(aux,1);
                    totalOccurrences++;

                }

                keep = false;
                isLink = false;

                aux = "";

            }
        }

        occurrences.forEach((k,v) -> occurrencesSorted.put(v,k));
    }

    public TreeMultimap<Integer,String> sortedOccurrences(){

        return occurrencesSorted;

    }

    public void occurrencesToFile(String Title){

        FileWriter fichero = null;
        FileWriter ficheroLog = null;
        FileWriter ficheroWord = null;
        PrintWriter pw = null;
        PrintWriter pwLog = null;
        PrintWriter pwWord = null;

        try
        {
            fichero = new FileWriter("datosSalida/"+Title+"-Occurrences.dat");
            pw = new PrintWriter(fichero);

            ficheroLog = new FileWriter("datosSalida/"+Title+"-OccurrencesLog.dat");
            pwLog = new PrintWriter(ficheroLog);

            int j = 1;

            for (Integer key : occurrencesSorted.keySet()){

                Set<String> aux = occurrencesSorted.get(key);

                for (String cadena : aux){
                    pw.println(j + "\t" + key);
                    pwLog.println(Math.log(j) + "\t" + Math.log(key));

                    j++;
                }
            }

            ficheroWord = new FileWriter("datosSalida/"+Title+"-OccurrencesWord.txt");
            pwWord = new PrintWriter(ficheroWord);

            for (Integer key : occurrencesSorted.keySet()){

                Set<String> aux = occurrencesSorted.get(key);

                for (String cadena : aux){
                    pwWord.println(cadena + "\t" + key);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Nuevamente aprovechamos el finally para
                // asegurarnos que se cierra el fichero.
                if (null != fichero && null != ficheroLog) {
                    fichero.close();
                    ficheroLog.close();
                    ficheroWord.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


    }

    public int getValue(){

        return totalOccurrences;
    }
}
