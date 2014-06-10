package co.justgame.compasspoints.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import co.justgame.compasspoints.compasspoint.CompassPoint;

public class NameUtils {

    public synchronized static String RandomName(ArrayList<CompassPoint> compassPoints){

        ArrayList<String> cpNames = new ArrayList<String>();
        for(CompassPoint sp: compassPoints){
            cpNames.add(sp.getName());
        }

        ArrayList<String> names = new ArrayList<String>();
        names.add("Alpha");
        names.add("Bravo");
        names.add("Charlie");
        names.add("Delta");
        names.add("Echo");
        names.add("Foxtrot");
        names.add("Golf");
        names.add("Hotel");
        names.add("India");
        names.add("Juliett");
        names.add("Kilo");
        names.add("Lima");
        names.add("Mike");
        names.add("November");
        names.add("Oscar");
        names.add("Papa");
        names.add("Quebec");
        names.add("Romeo");
        names.add("Sierra");
        names.add("Tango");
        names.add("Uniform");
        names.add("Victor");
        names.add("Walrus");
        names.add("X-ray");
        names.add("Yankee");
        names.add("Zulu");

        String name = "";

        int numContained = 0;
        Iterator<String> it = names.iterator();
        while(it.hasNext()){
            String n = it.next();
            if(cpNames.contains(n)){
                numContained++;
                it.remove();
            }
        }

        if(numContained == 26){
            StringBuilder nameBuilder = new StringBuilder(name);
            for(int i = 0; i <= 4; i++){
                Random r = new Random();
                char c = (char) (r.nextInt(26) + 'a');
                nameBuilder.append(c);
            }
            nameBuilder.replace(0, 1, String.valueOf(nameBuilder.charAt(1)).toUpperCase());
            name = nameBuilder.toString();
        }else{
            Random r = new Random();
            int i = (r.nextInt(names.size()));
            name = names.get(i);
        }

        return name;
    }
}
