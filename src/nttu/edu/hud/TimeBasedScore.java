package nttu.edu.hud;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nttu.edu.graphics.RenderView;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class TimeBasedScore implements Runnable {
	private final List<Time> defaultTimes;
	private Time time;
	private Thread timerThread;
	private final Paint paint;
	private boolean hasBeenLoaded;
	private boolean threadRunning;
	
	public String countdown;
	
	private class Time implements Comparable<Time> {
		public int stageID;
		public int minutes;
		public int seconds;
		private boolean running;
		
		public Time(int i, int m, int s) {
			stageID = i;
			minutes = m;
			seconds = s;
		}
		
		public long totalSeconds() {
			return (minutes * 60 + seconds);
		}
		
		public void tick() {
			if (running) {
				seconds--;
				if (seconds < 0) {
					seconds = 59;
					minutes--;
					if (minutes < 0) {
						minutes = 0;
						seconds = 0;
					}
				}
			}
			return;
		}
		
		public void pause() {
			running = false;
		}
		
		public void unpause() {
			running = true;
		}
		
		public int compareTo(Time t) {
			if (this.stageID > t.stageID)
				return -1;
			if (this.stageID < t.stageID)
				return 1;
			return 0;
		}
		
		@Override
		public String toString() {
			String m = minutes > 9 ? String.valueOf(minutes) : "0" + String.valueOf(minutes);
			String s = seconds > 9 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);
			return "Remaining: " + m + ":" + s;
		}
		
		@Override
		public Time clone() {
			Time t = defaultTimes.get(stageID - 1);
			return new Time(t.stageID, t.minutes, t.seconds);
		}
	}
	
	private TimeBasedScore() {
		defaultTimes = new ArrayList<Time>();
		countdown = "Remaining: ";
		threadRunning = false;
		timerThread = new Thread(this);
		hasBeenLoaded = false;
		time = null;
		paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		paint.setTextSize(20f);
	}
	
	public static TimeBasedScore loadTimers(Activity a, String filename, TimeBasedScore temp) {
		if (temp != null && temp.hasBeenLoaded)
			return temp;
		TimeBasedScore tbs = new TimeBasedScore();
		AssetManager assets = a.getAssets();
		BufferedReader reader = null;
		try {
			BufferedInputStream input = new BufferedInputStream(assets.open(filename));
			reader = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				int value = Integer.parseInt(parts[0]);
				int minutes = Integer.parseInt(parts[1].split(":")[0]);
				int seconds = Integer.parseInt(parts[1].split(":")[1]);
				final Time t = tbs.new Time(value, minutes, seconds);
				tbs.defaultTimes.add(t);
			}
			Arrays.sort(tbs.defaultTimes.toArray());
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		tbs.hasBeenLoaded = true;
		return tbs;
	}
	
	public void clearTimers() {
		while (defaultTimes.size() > 0)
			defaultTimes.remove(0);
	}
	
	public void execute() {
		if (!threadRunning) {
			if (timerThread != null) {
				timerThread.start();
				threadRunning = true;
			}
		}
	}
	
	public void run() {
		if (time != null) {
			while (time.totalSeconds() > 0) {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					break;
				}
				if (Thread.currentThread().isInterrupted())
					break;
				time.tick();
				countdown = time.toString();
			}
			countdown = time.toString();
		}
	}
	
	public void pauseTimer() {
		if (time != null)
			time.pause();
	}
	
	public void unpauseTimer() {
		if (time != null)
			time.unpause();
	}
	
	public void render(Canvas c) {
		if (countdown.length() > 0)
			c.drawText(countdown, 10 * RenderView.AspectRatio, (paint.getTextSize() * 4) * RenderView.AspectRatio, paint);
	}
	
	//Don't check for (time == null). Intended.
	public void resetAndLoad(int stageNumber) {
		boolean check = false;
		hasBeenLoaded = false;
		for (Time t : defaultTimes) {
			if (t.stageID == stageNumber) {
				time = t.clone();
				check = true;
				break;
			}
			else
				check = false;
		}
		checkingDefaultTimes(check);
	}
	
	private void checkingDefaultTimes(boolean check) {
		if (check) {
			countdown = time.toString();
			if (timerThread != null) {
				timerThread.interrupt();
				timerThread = null;
			}
			timerThread = new Thread(this);
			threadRunning = false;
			hasBeenLoaded = true;
		}
		else {
			if (timerThread != null) {
				timerThread.interrupt();
				timerThread = null;
			}
			time = null;
			countdown = "No timer.";
		}
	}
	
	public void reset() {
		boolean check = false;
		hasBeenLoaded = false;
		for (Time t : defaultTimes) {
			if (time == null)
				break;
			if (t.stageID == time.stageID) {
				time = t.clone();
				check = true;
				break;
			}
			else
				check = false;
		}
		checkingDefaultTimes(check);
	}
	
	public boolean isLoaded() {
		return hasBeenLoaded;
	}
	
	public long getScore() {
		if (time != null)
			return time.totalSeconds() * 10;
		return 0;
	}
}
