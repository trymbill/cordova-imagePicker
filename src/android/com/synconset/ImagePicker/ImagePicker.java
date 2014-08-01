/**
 * An Image Picker Plugin for Cordova/PhoneGap.
 */
package com.synconset;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.net.Uri;
import android.util.Base64;

public class ImagePicker extends CordovaPlugin {
    public static String TAG = "ImagePicker";
    
    private CallbackContext callbackContext;
    private JSONObject params;
    
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
      this.callbackContext = callbackContext;
      this.params = args.getJSONObject(0);
    if (action.equals("getPictures")) {
      Intent intent = new Intent(cordova.getActivity(), MultiImageChooserActivity.class);
      int max = 20;
      int desiredWidth = 0;
            int desiredHeight = 0;
            int quality = 100;
      if (this.params.has("maximumImagesCount")) {
          max = this.params.getInt("maximumImagesCount");
      }
      if (this.params.has("width")) {
          desiredWidth = this.params.getInt("width");
      }
      if (this.params.has("height")) {
          desiredWidth = this.params.getInt("height");
      }
      if (this.params.has("quality")) {
                quality = this.params.getInt("quality");
            }
      intent.putExtra("MAX_IMAGES", max);
      intent.putExtra("WIDTH", desiredWidth);
      intent.putExtra("HEIGHT", desiredHeight);
      intent.putExtra("QUALITY", quality);
            if (this.cordova != null) {
                this.cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
            }
    }
    return true;
  }
  
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> fileNames = data.getStringArrayListExtra("MULTIPLEFILENAMES");

            ArrayList<String> encodedImages = new ArrayList<String>();
            for(String fileName : fileNames) {
              try {
                Uri fileUri = Uri.parse(fileName);
                InputStream inputStream = new FileInputStream(fileUri.getPath());
                byte[] bytes;
                byte[] buffer = new byte[8192];
                int bytesRead;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                  while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }
                bytes = output.toByteArray();

                String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
                encodedImages.add(encodedString);
              } catch(FileNotFoundException fnfe) {
              }
            }

            JSONArray res = new JSONArray(encodedImages);
            this.callbackContext.success(res);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            JSONArray res = new JSONArray();
            this.callbackContext.success(res);
        } else {
            this.callbackContext.error("No images selected");
        }
  }
}
