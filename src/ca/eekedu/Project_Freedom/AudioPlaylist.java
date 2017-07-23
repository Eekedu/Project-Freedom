package ca.eekedu.Project_Freedom;

import javazoom.jlme.util.Player;

import java.io.File;
import java.io.FileInputStream;
import java.util.TreeMap;

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
				Runnable r = new Runnable() {
					@Override
					public void run() {
						try {
							do {
								musicPlayer = new Player(new FileInputStream(musicFiles.get(currentTrack)));
								isPlaying = true;
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
					}
				};
				localThread = new Thread(r);
				localThread.setDaemon(true);
				localThread.start();
			} catch (Exception e) {
				return false;
			}
		} else {
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
