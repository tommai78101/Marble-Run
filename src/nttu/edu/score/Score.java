package nttu.edu.score;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

// Scoreboard: This class lists all 10 players' high scores.
// Activity: ScoreActivity uses this class Score.

//TODO: Continue working on the scoreboard, and figure a way to:
// #1: Calculate the points the player earned while playing a level (demo level, play against time).
// #2: Obtain the player's name after the player completes a level (demo level, when goal is reached).
// #3: Display 10 players (place, name, and score) when player presses "High Score" menu button.
// #4: Only insert entries after player finishes a level. Only display entries, otherwise.

public class Score {
	private static final String DB_NAME = "Scoreboard";
	private static final String DB_TABLE = "Leaderboard";
	private static final int DB_VERSION = 1;
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "_name";
	private static final String KEY_SCORE = "_score";
	
	private class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context c) {
			super(c, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT NOT NULL, " + KEY_SCORE + " INTEGER NOT NULL);");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE + ";");
			this.onCreate(db);
		}
	}
	
	private final Context context;
	private DBHelper helper;
	private SQLiteDatabase database;
	
	public Score(Context c) {
		this.context = c;
	}
	
	public Score open() throws SQLiteException{
		this.helper = new DBHelper(this.context);
		this.database = helper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		this.helper.close();
	}
	
	public void createEntry(String name, long score){
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, name);
		cv.put(KEY_SCORE, score);
		this.database.insert(DB_TABLE, null, cv);
	}
}
