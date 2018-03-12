package com.lymenglong.laptop.audiobookapp1verion2.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lymenglong.laptop.audiobookapp1verion2.model.BookType;
import com.lymenglong.laptop.audiobookapp1verion2.model.Chapter;
import com.lymenglong.laptop.audiobookapp1verion2.model.Home;
import com.lymenglong.laptop.audiobookapp1verion2.model.Category;
import com.lymenglong.laptop.audiobookapp1verion2.model.Story;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteAssetHelper {


    private static final int DATABASE_VERSION = 1;
    private Story storyModel;
    private Home homeModel;
    private Chapter chapterModel;
    private Category categoryModel;
    private BookType typeModel;

    public static final String DBNAME = "DBManager16.sqlite";
    public static final String  GET_ALL_STORY= "SELECT categoryId, categoryName FROM category";
    public static final String  GET_ALL_HOME= "SELECT homeID, homeName FROM home";
    public static final String GET_CHAPTER_OF_STORY =
            "SELECT st_main.stID, st_main.dename, st_main.decontent "
                    +"FROM category, st_main WHERE st_main.stID = category.categoryId and st_main.stID = ";
    public static final String GET_ALL_CHAPTER =
            "SELECT st_main.stID, st_main.dename, st_main.decontent, st_main.fileUrl "
                    +"FROM category, st_main WHERE st_main.stID = category.categoryId and st_main.stID = ";
    public static final String GET_ALL_SMALL_CHAPTER =
            "SELECT category.categoryId, category.categoryName "
                    +"FROM category , book_type WHERE category.homeId = book_type.typeID and book_type.typeID = ";
public static final String GET_TYPE_OF_BOOK =
            "SELECT book_type.typeID, book_type.typeName, book_type.homeID "
                    +"FROM home, book_type WHERE book_type.homeID = home.homeID and book_type.homeID = ";

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null,DATABASE_VERSION );
        this.mContext = context;

    }

    /**
     * Lấy danh sách truyện
     * @return list truyện trong db
     */

    public ArrayList<Home> getHomeList() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Home> homes = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_ALL_HOME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            homeModel = new Home(cursor.getInt(0), cursor.getString(1));
            homes.add(homeModel);
            cursor.moveToNext();
        }
        cursor.close();
        return homes;
    }

    /**
     * Lấy danh sách truyện
     * @return list truyện trong db
     */
    public ArrayList<Story> getListStory() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Story> stories = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_ALL_STORY, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            storyModel = new Story(cursor.getInt(0), cursor.getString(1));
            stories.add(storyModel);
            cursor.moveToNext();
        }
        cursor.close();
        return stories;
    }

    /**
     * Lấy danh sách chương của truyện
     * @param id id định danh của truyện
     * @return danh sách các chương của truyện có id tương ứng
     */
    public ArrayList<Chapter> getListChapter(int id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Chapter> chapters = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_TYPE_OF_BOOK + String.valueOf(id), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            chapterModel = new Chapter(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            chapters.add(chapterModel);
            cursor.moveToNext();
        }
        cursor.close();
        return chapters;
    }

    public ArrayList<Chapter> getListAllChapter(int id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Chapter> chapters = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_ALL_CHAPTER + String.valueOf(id), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            chapterModel = new Chapter(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            chapters.add(chapterModel);
            cursor.moveToNext();
        }
        cursor.close();
        return chapters;
    }


    public ArrayList<Category> getListSmallChapter(int id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<Category> categories = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_ALL_SMALL_CHAPTER + String.valueOf(id), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            categoryModel = new Category(cursor.getInt(0), cursor.getString(1));
            categories.add(categoryModel);
            cursor.moveToNext();
        }
        cursor.close();
        return categories;
    }



    public ArrayList<BookType> getListBookType(int id){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        ArrayList<BookType> types = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(GET_TYPE_OF_BOOK + String.valueOf(id), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            typeModel = new BookType(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            types.add(typeModel);
            cursor.moveToNext();
        }
        cursor.close();
        return types;
    }
}
