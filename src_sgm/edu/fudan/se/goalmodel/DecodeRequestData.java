/**
 * 
 */
package edu.fudan.se.goalmodel;

import android.graphics.Bitmap;

/**
 * @author whh
 *
 */
public class DecodeRequestData {
	
	public static String decodeToString(byte[] content){
		return new String(content);
	}
	
	public static Bitmap decodeToBitmap(byte[] content){
		return null;
	}

}
