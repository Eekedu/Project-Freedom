package ca.eekedu.Project_Freedom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

import static ca.eekedu.Project_Freedom.MainGame.notificationHandler;

public class KeyBinds extends HashMap<String, Integer> {

	KeyBinds() {
		File keybinds = new File("config/keybinds.cfg");
		if (!keybinds.exists()){
			try {
				populate_defaults(keybinds);
			} catch (Exception e) {
				notificationHandler.addNotification("Can't create or read keybinds.cfg! - " +
						e.getMessage(), Notifications.NOTIFICATION_TYPE.ERROR);
			}
		}
		read(keybinds);
	}
	
	public void populate_defaults(File keybinds) throws Exception{
		FileWriter fw;
		fw = new FileWriter(keybinds);
		fw.write("MOUSE_UP:\t87\n"
				+ "CHAR_DOWN:\t83\n"
				+ "CHAR_LEFT:\t65\n"
				+ "CHAR_RIGHT:\t68\n"
				+ "CHAR_JUMP:\t32\n"
				+ "SIZE_UP:\t38\n"
				+ "SIZE_DOWN:\t40\n"
				+ "DO_DRAW:\t81\n"
				+ "CLICK_M:\t16\n"
				+ "COLOR_C:\t67\n"
				+ "COLOR_B:\t107\n"
				+ "COLOR_D:\t109\n"
				+ "MOUSE_P:\t0x1\n"
				+ "CENTER_B:\t32\n"
				+ "SELECT_O:\t86\n"
				+ "INVENT_B:\t69");
		fw.close();
	}

	public void read(File keybinds) {
		try {
			FileReader fr = new FileReader(keybinds);
			BufferedReader br = new BufferedReader(fr);
			String line; int count = 0;
			while((line = br.readLine()) != null){
				String[] key = line.split(":\t");
				switch (key[0]){
					case "MOUSE_UP":
					case "CHAR_DOWN":
					case "CHAR_LEFT":
					case "CHAR_RIGHT":
					case "CHAR_JUMP":
					case "SIZE_UP": case "SIZE_DOWN": case "DO_DRAW": case "CLICK_M": 
					case "COLOR_C": case "COLOR_B": case "COLOR_D": case "MOUSE_P": 
					case "CENTER_B": case "SELECT_O": case "INVENT_B": {
						if (key[0].equals("MOUSE_P")) {
							if (Integer.decode(key[1]) < 1 || Integer.decode(key[1]) > 3){
								break;
							}
						}
						put(key[0], Integer.decode((key[1])));
						count++; break;
					}
					default: break;
				}
			}
			br.close();
			fr.close();
			if (count < 16) {
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
			} catch (Exception e1) {
				notificationHandler.addNotification("Can't create or read keybinds.cfg! - " +
						e.getMessage(), Notifications.NOTIFICATION_TYPE.ERROR);
			}
		}
	}
	
}
