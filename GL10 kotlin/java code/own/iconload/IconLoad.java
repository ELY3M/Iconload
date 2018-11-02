package own.iconload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.util.Log;
import java.io.File;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

public class IconLoad {
    private static final float icon_sizing_factor = 0.4f;
    ///private static String iconfile = "star_cyan.png";
    private boolean BUSY;
    final String TAG = "PYKLoad";

    private int[] cropRect = new int[4];

    Context context;
    //DisplayMetrics dm = context.getResources().getDisplayMetrics();
    //private float display_density = dm.densityDpi;
    private int drawHeight;
    private int drawWidth;

    private int[] textures = new int[1];
    private boolean texturesLoaded = false;



    public IconLoad(String iconfile, float display_density) {
        FillCropRect(iconfile, display_density);
    }

    private void FillCropRect(String iconfile, float display_density) {
        Log.i(TAG, "FillCropRect");
        Options options = new Options();
        options.inScaled = false;
        File file = new File(Constants.FilesPath+iconfile);
        if (file.exists()) {
            Log.i(TAG, "file exists");
            Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            try {
                cropRect[0] = 0;
                cropRect[1] = decodeFile.getHeight();
                cropRect[2] = decodeFile.getWidth();
                cropRect[3] = -decodeFile.getHeight();
                drawWidth = (int) ((((float) cropRect[2]) * display_density) * icon_sizing_factor);
                drawHeight = (int) ((((float) (-cropRect[3])) * display_density) * icon_sizing_factor);
                decodeFile.recycle();

                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Croprect set to ");
                stringBuilder2.append(cropRect[0]);
                stringBuilder2.append(" ");
                stringBuilder2.append(cropRect[1]);
                stringBuilder2.append(" ");
                stringBuilder2.append(cropRect[2]);
                stringBuilder2.append(" ");
                stringBuilder2.append(cropRect[3]);
                stringBuilder2.append(" ");
                Log.i(TAG, stringBuilder2.toString());
                return;
            } catch (Exception e) {
                Log.e(TAG, "Exception setting the cropRect");
                e.printStackTrace();
                return;
            }
        } else {
            Log.e(TAG, "Unable to find texture " + file.getAbsolutePath());
        }

    }

    private void loadGLTexture(GL10 gl, String iconfile) {
        Log.i(TAG, "loadGLTexture");
        if (BUSY) {
            Log.e(TAG, "Loading Textures Already Busy");
            return;
        }
        BUSY = true;
        Log.d(TAG, "Loading Textures");
        texturesLoaded = TextureOperations.invalidateTexture(gl, textures);
        Options options = new Options();
        options.inScaled = false;
        File file = new File(Constants.FilesPath+iconfile);
        if (file.exists()) {
            Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            textures[0] = TextureOperations.loadTextureFromBitmapFast(gl, decodeFile);
            decodeFile.recycle();
            texturesLoaded = true;
            BUSY = false;
            return;
        } else {
            Log.e(TAG, "Unable to find texture " + file.getAbsolutePath());
            BUSY = false;
        }
    }



    public void drawIcon(GL10 gl, String iconfile, int x, int y) {
        Log.i(TAG, "drawIcon");
        if (!texturesLoaded) {
            Log.i(TAG, "Loading Textures in drawIcon");
            loadGLTexture(gl, iconfile);
        }

        draw(gl, x, y);
    }

    public void unloadTextures(GL10 gl) {
        Log.i(TAG, "unloadtextures");
        texturesLoaded = TextureOperations.invalidateTexture(gl, textures);
    }

    public void draw(GL10 gl10, int x, int y) {
        Log.i(TAG, "draw");
        gl10.glEnable(GL10.GL_TEXTURE_2D);
        gl10.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        ((GL11) gl10).glTexParameteriv(GL10.GL_TEXTURE_2D, 35741, cropRect, 0);
        ((GL11Ext) gl10).glDrawTexiOES(x, y, 0, drawWidth, drawHeight);
        gl10.glDisable(GL10.GL_TEXTURE_2D);
    }



}
