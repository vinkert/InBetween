/**
 * Created by Vincent for Coding on 4/24/2017.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CityStateReader {
    public static void main(String[] args)  {
        //File to be parsed
        //Format of each line: "City Name,State,United States"
        String dir = "C:\\dev\\projects\\CityStateParser\\data\\city_state.csv";
        BufferedReader br;
        String line;
        String splitToken = ",";
        List<String> cities = new ArrayList<String>();
        try{
            FileWriter writer = new FileWriter("C:\\dev\\projects\\CityStateParser\\data\\city_state.txt");
            br = new BufferedReader(new FileReader(dir));
            StringBuffer str = new StringBuffer();
            while(((line = br.readLine()) != null)) {
                String[] result = line.split(splitToken);
                str.delete(0,str.length());
                result[0] = result[0].substring(1);
                if(Character.isLetter(result[0].charAt(0))) {
                    str.append(result[0] + ", ");
                    str.append(result[1]);
                }
                else {
                    continue;
                }
                //Output format: City Name, State
                writer.write(str.toString() + '\n');
                cities.add(str.toString());
            }
            writer.close();
        }catch(Exception e){}

    }
}
