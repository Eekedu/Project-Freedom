package ca.eekedu.Project_Freedom;

import javazoom.jlme.util.Player;

import java.io.File;
import java.io.FileInputStream;
import java.util.TreeMap;

import static ca.eekedu.Project_Freedom.MainGame.notificationHandler;

public class AudioPlaylist {

	public static volatile boolean canPlay = false, isPlaying = false;
	public LOOPTYPE loopType = LOOPTYPE.NOREPEAT;
	private Thread localThread;
	private Player musicPlayer;
	private TreeMap<Integer, File> musicFiles = new TreeMap<>();
	private int currentTrack = 0;

	AudioPlaylist() {
	}

	AudioPlaylist(LOOPTYPE loopType) throws Exception {
		this.loopType = loopType;
	}

	public boolean add(String fileName) {
		try {
			musicFiles.put((!musicFiles.isEmpty()) ? musicFiles.lastKey() + 1 : 0, new File(fileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void next() throws Exception {
		if (currentTrack == musicFiles.lastKey()) {
			currentTrack = musicFiles.firstKey();
		} else {
			currentTrack = musicFiles.higherKey(currentTrack);
		}
	}

	public boolean play() throws Exception {
		if (!musicFiles.isEmpty()) {
			canPlay = true;
			try {
				Runnable r = () -> {
						try {
							do {
								musicPlayer = new Player(new FileInputStream(musicFiles.get(currentTrack)));
								isPlaying = true;
								notificationHandler.addNotification("Now playing: " + musicFiles.get(currentTrack).getName(),
										Notifications.NOTIFICATION_TYPE.INFORMATION);
								musicPlayer.play();
								isPlaying = false;
								if (loopType.equals(LOOPTYPE.REPEATALL)) {
									next();
								}
							} while (!loopType.equals(LOOPTYPE.NOREPEAT) && canPlay);
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
				};
				localThread = new Thread(r);
				//localThread.setDaemon(true);
				localThread.start();
			} catch (Exception e) {
				notificationHandler.addNotification("An Error has occurred while trying to play a song",
						Notifications.NOTIFICATION_TYPE.ERROR);
				return false;
			}
		} else {
			notificationHandler.addNotification("No music to play in the music folder",
					Notifications.NOTIFICATION_TYPE.ERROR);
			return false;
		}
		return true;
	}

	public void stopNext() throws Exception {
		musicPlayer.stop();
	}

	public void stop() {
		canPlay = false;
		musicPlayer.stop();
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public enum LOOPTYPE {
		NOREPEAT, REPEAT, REPEATALL
	}

}
