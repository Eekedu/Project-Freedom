package ca.eekedu.Project_Freedom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class KeyBinds extends HashMap<String, Integer> {

	private static final long serialVersionUID = -3262002073024191000L;

	KeyBinds(){
		File keybinds = new File("keybinds.cfg");
		if (!keybinds.exists()){
			try {
				populate_defaults(keybinds);
			} catch (Exception e) {}
		}
		read(keybinds);
	}
	
	public void populate_defaults(File keybinds) throws Exception{
		FileWriter fw;
		fw = new FileWriter(keybinds);
		fw.write("C_UP:\t87\n"
				+ "C_DOWN:\t83\n"
				+ "C_LEFT:\t65\n"
				+ "C_RIGHT:\t68\n"
				+ "SIZE_UP:\t38\n"
				+ "SIZE_DOWN:\t40\n"
				+ "DO_DRAW:\t32\n"
				+ "CLICK_M:\t16\n"
				+ "COLOR_C:\t67\n"
				+ "COLOR_B:\t107\n"
				+ "COLOR_D:\t109\n"
				+ "MOUSE_P:\t0x1\n"
				+ "CENTER_B:\t32");
		fw.close();
	}
	
	public void read(File keybinds){
		try {
			FileReader fr = new FileReader(keybinds);
			BufferedReader br = new BufferedReader(fr);
			String line; int count = 0;
			while((line = br.readLine()) != null){
				String[] key = line.split(":\t");
				switch (key[0]){
					case "C_UP": case "C_DOWN": case "C_LEFT": case "C_RIGHT": 
					case "SIZE_UP": case "SIZE_DOWN": case "DO_DRAW": case "CLICK_M": 
					case "COLOR_C": case "COLOR_B": case "COLOR_D": case "MOUSE_P": case "CENTER_B": {
						put(key[0], Integer.decode((key[1])));
						count++; break;
					}
					default: break;
				}
			}
			br.close();
			fr.close();
			if (count < 13){
				clear();
				keybinds.delete();
				populate_defaults(keybinds);
				read(keybinds);
			}
		} catch (Exception e) {
			clear();
			keybinds.delete();
			try {
				populate_defaults(keybinds);
				read(keybinds);
			} catch (Exception e1) {}
		}
	}
	
}
