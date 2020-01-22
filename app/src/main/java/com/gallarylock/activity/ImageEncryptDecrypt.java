package com.gallarylock.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class ImageEncryptDecrypt {

    Cipher ecipher;
    Cipher dcipher;

  public ImageEncryptDecrypt(String password) {

        // 8-bytes Salt
        byte[] salt = {
                (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
                (byte)0x56, (byte)0x34, (byte)0xE3, (byte)0x03
        };

        // Iteration count
        int iterationCount = 19;

        try {

            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            // Prepare the parameters to the cipthers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        } catch (InvalidAlgorithmParameterException e) {
            System.out.println("EXCEPTION: InvalidAlgorithmParameterException");
        } catch (InvalidKeySpecException e) {
            System.out.println("EXCEPTION: InvalidKeySpecException");
        } catch (NoSuchPaddingException e) {
            System.out.println("EXCEPTION: NoSuchPaddingException");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("EXCEPTION: NoSuchAlgorithmException");
        } catch (InvalidKeyException e) {
            System.out.println("EXCEPTION: InvalidKeyException");
        }
    }


    /**
     * Takes a single String as an argument and returns an Encrypted version
     * of that String.
     * @param str String to be encrypted
     * @return <code>String</code> Encrypted version of the provided String
     */
    public byte[] encrypt(byte[] str) {
        try {
            // Encode the string into bytes using utf-8
          //  byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
          return ecipher.doFinal(str);

            // Encode bytes to base64 to get a string
            //return new sun.misc.BASE64Encoder().encode(enc);


        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        }

    }


    /**
     * Takes a encrypted String as an argument, decrypts and returns the
     * decrypted String.
     //* @param str Encrypted String to be decrypted
     * @return <code>String</code> Decrypted version of the provided String
     */
    public byte[] decrypt(byte[] dec) {

        try {

            // Decode base64 to get bytes
            //byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            //byte[] dec = Base64Coder.decode(str);

            // Decrypt
            return dcipher.doFinal(dec);

            // Decode using utf-8
           // return new String(utf8, "UTF8");

        } catch (BadPaddingException e) {
            return null;
        } catch (IllegalBlockSizeException e) {
            return null;
        }

    }
    public static boolean delete(final Context context, final File file) {
        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[] {
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            String volumename = MediaStore.getVolumeName(Uri.fromFile(file));
        }
        final Uri filesUri = MediaStore.Files.getContentUri("external");

        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {

            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }


    public static void insertFile(final Context context, final File imageFile) {
        ContentValues image = new ContentValues();

        image.put(MediaStore.Images.Media.TITLE, imageFile.getName());
        image.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
        image.put(MediaStore.Images.Media.DESCRIPTION, imageFile.getName());
        image.put(MediaStore.Images.Media.DATE_ADDED, imageFile.lastModified());
        image.put(MediaStore.Images.Media.DATE_TAKEN, imageFile.lastModified());
        image.put(MediaStore.Images.Media.DATE_MODIFIED, imageFile.lastModified());
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        image.put(MediaStore.Images.Media.ORIENTATION, 0);

        File parent = imageFile.getParentFile();
        String path = parent.toString().toLowerCase();
        String name = parent.getName().toLowerCase();
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
        image.put(MediaStore.Images.Media.SIZE, imageFile.length());

        image.put(MediaStore.Images.Media.DATA, imageFile.getAbsolutePath());

        Uri result = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);
    }
  /*public static   void savefile(File sourceuri, String Destination)
    {
        String sourceFilename= sourceuri.getPath();
        String destinationFilename = Destination;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        val encryptedData = sourceuri.readBytes()
        val decryptedData = ImageEncryptDecrypt(Constant.MY_PASSWORD).decrypt(encryptedData)

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
  public static String encodeFromString(Bitmap bm){
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
      byte[] b = baos.toByteArray();

      return Base64.encodeToString(b, Base64.DEFAULT);
  }
}
