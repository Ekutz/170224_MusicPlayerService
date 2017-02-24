package com.jspark.android.musicplayerservice;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jsPark on 2017. 2. 1..
 */

public class DataLoader {

    private static List<Music> datas = new ArrayList<>();
    Context context;

    final static Uri URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    final static String PROJECTIONS[] = new String[] {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION
    };

    public static List<Music> load(Context context) {
        if(datas==null|| datas.size()==0) {
            getContacts(context);
        }
        return datas;
    }

    public static void getContacts(Context context) {

        ContentResolver resolver = context.getContentResolver();

        String sortOrder =  MediaStore.Audio.Media.ALBUM_ID+ " ASC";

        Cursor cursor = resolver.query(URI, PROJECTIONS, null, null, sortOrder);

        if(cursor.moveToFirst()) {
            while(cursor.moveToNext()) {
                Music mMusic = new Music();

                int idx = cursor.getColumnIndex(PROJECTIONS[0]);
                mMusic.id = (cursor.getString(idx));
                idx = cursor.getColumnIndex(PROJECTIONS[1]);
                mMusic.album_id = (cursor.getInt(idx));
                idx = cursor.getColumnIndex(PROJECTIONS[2]);
                mMusic.title = (cursor.getString(idx));
                idx = cursor.getColumnIndex(PROJECTIONS[3]);
                mMusic.artist = (cursor.getString(idx));
                idx = cursor.getColumnIndex(PROJECTIONS[4]);
                mMusic.length = (cursor.getInt(idx));

                mMusic.album_img = getAlbumImgSimple(""+mMusic.album_id);

                mMusic.uri = getMusicUri(mMusic.id);

                datas.add(mMusic);
            }
            cursor.close();
        }
    }

    private static Uri getMusicUri(String music_id) {
        Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return Uri.withAppendedPath(contentUri, music_id);
    }

    private static Uri getAlbumImgSimple(String album_id) {
        return Uri.parse("content://media/external/audio/albumart/"+album_id);
    }
}
