/***************************************************************************
 *  JLayerME is a JAVA library that decodes/plays/converts MPEG 1/2 Layer 3.
 *  Project Homepage: http://www.javazoom.net/javalayer/javalayerme.html.
 *  Copyright (C) JavaZOOM 1999-2005.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *---------------------------------------------------------------------------
 */
/*
  Needed to get rid of the println statements
  And the main method
  I think that's all I changed
  -Brettink
*/
package javazoom.jlme.util;


import javazoom.jlme.decoder.BitStream;
import javazoom.jlme.decoder.Decoder;
import javazoom.jlme.decoder.Header;
import javazoom.jlme.decoder.SampleBuffer;

import javax.sound.sampled.*;
import java.io.InputStream;

public class Player {
	private static Decoder decoder;
	private static SourceDataLine line;
	private static BitStream bitstream;
	public volatile boolean playable = true;
	//Runtime rt = null;


	public Player(InputStream stream) throws Exception {
		bitstream = new BitStream(stream);
		//rt = Runtime.getRuntime();
	}


	public static void startOutput(AudioFormat playFormat) throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, playFormat);

		if (!AudioSystem.isLineSupported(info)) {
			throw new LineUnavailableException("sorry, the sound format cannot be played");
		}
		line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(playFormat);
		line.start();
	}


	public static void stopOutput() {
		if (line != null) {
			line.drain();
			line.stop();
			line.close();
			line = null;
		}
	}

	private static void usage() {
		System.out.println("Usage : ");
		System.out.println("       java javazoom.jlme.util.Player [mp3file] [-url mp3url]");
		System.out.println("");
		System.out.println("            mp3file : MP3 filename to play");
		System.out.println("            mp3url  : MP3 URL to play");
	}

	public void play() throws Exception {
		int length;
		Header header = bitstream.readFrame();
		decoder = new Decoder(header, bitstream);
		startOutput(new AudioFormat(decoder.getOutputFrequency(), 16, decoder.getOutputChannels(), true, false));
		while (playable) {
			try {
				SampleBuffer output = decoder.decodeFrame();
				length = output.size();
				if (length == 0) break;
				//{
				line.write(output.getBuffer(), 0, length);
				bitstream.closeFrame();
				header = bitstream.readFrame();
				//System.out.println("Mem:"+(rt.totalMemory() - rt.freeMemory())+"/"+rt.totalMemory());
				//}
			} catch (Exception e) {
				//e.printStackTrace();
				break;
			}
		}
		playable = false;
		stopOutput();
		bitstream.close();
	}

	public void stop() {
		playable = false;
	}
}