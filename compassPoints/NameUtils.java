package compassPoints;

import java.util.ArrayList;
import java.util.Random;

public class NameUtils {
	
	public synchronized static String RandomName(ArrayList<CompassPoint> compassPoints){
		
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
		
		String name= "";
		boolean contains = true;
		int numContained = 0;
		boolean containsAll = false;
		
		for(CompassPoint compassPoint: compassPoints){
			for(String NATOname: names){
				if(compassPoint.getName().equals(NATOname)){
					numContained++;
			  }
			}
		}
		
	if(numContained == 26){
			containsAll = true;
		}
		
		StringBuilder nameBuilder = new StringBuilder(name);
		for(int i = 0; i <= 4; i++){
			Random r = new Random();
			char c = (char)(r.nextInt(26) + 'a');
			nameBuilder.append(c);
		}
		
		nameBuilder.replace(0, 1, String.valueOf(nameBuilder.charAt(1)).toUpperCase());
		name = nameBuilder.toString();
		
	while(contains == true && containsAll == false){
		contains = false;
		
		Random r = new Random();
		char c = (char)(r.nextInt(26) + 'a');
		
		if(c == 'a'){
			name = names.get(0);
		}else if(c == 'b'){
			name = names.get(1);
		}else if(c == 'c'){
			name = names.get(2);
		}else if(c == 'd'){
			name = names.get(3);
		}else if(c == 'e'){
			name = names.get(4);
		}else if(c == 'f'){
			name = names.get(5);
		}else if(c == 'g'){
			name = names.get(6);
		}else if(c == 'h'){
			name = names.get(7);
		}else if(c == 'i'){
			name = names.get(8);
		}else if(c == 'j'){
			name = names.get(9);
		}else if(c == 'k'){
			name = names.get(10);
		}else if(c == 'l'){
			name = names.get(11);
		}else if(c == 'm'){
			name = names.get(12);
		}else if(c == 'n'){
			name = names.get(13);
		}else if(c == 'o'){
			name = names.get(14);
		}else if(c == 'p'){
			name = names.get(15);
		}else if(c == 'q'){
			name = names.get(16);
		}else if(c == 'r'){
			name = names.get(17);
		}else if(c == 's'){
			name = names.get(18);
		}else if(c == 't'){
			name = names.get(19);
		}else if(c == 'u'){
			name = names.get(20);
		}else if(c == 'v'){
			name = names.get(21);
		}else if(c == 'w'){
			name = names.get(22);
		}else if(c == 'x'){
			name = names.get(23);
		}else if(c == 'y'){
			name = names.get(24);
		}else if(c == 'z'){
			name = names.get(25);
		}
		
		for(CompassPoint compassPoint: compassPoints){
			if(compassPoint.getName().equals(name)){
				contains = true;
				break;
			}
		}
		
	}		
		return name;
	}
}
