package nttu.edu.level;

import java.util.ArrayList;
import java.util.List;
import nttu.edu.ball.Cue;
import nttu.edu.ball.Marble;
import nttu.edu.entity.Border;
import nttu.edu.entity.Bumper;
import nttu.edu.entity.Coin;
import nttu.edu.entity.Coin.ColorType;
import nttu.edu.entity.Connector;
import nttu.edu.entity.CurvePipe;
import nttu.edu.entity.Funnel;
import nttu.edu.entity.Funnel.Direction;
import nttu.edu.entity.Hole;
import nttu.edu.entity.Path;
import nttu.edu.entity.Path.Orientation;
import nttu.edu.entity.Pipe;
import nttu.edu.entity.Ramp;
import nttu.edu.entity.ShortFunnel;
import nttu.edu.entity.Tee;
import nttu.edu.entity.Terrain;
import nttu.edu.entity.Void;
import nttu.edu.graphics.RenderView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;

public class Stage implements DialogInterface.OnClickListener {
	
	public int width;
	public int height;
	public int[] data;
	public int number;
	
	public final int SIZE;
	
	public static List<Terrain> terrain;
	public static List<Path> paths;
	public List<Marble> marbles;
	public List<Coin> coins;
	public Hole hole;
	public Tee tee;
	public Cue cue;
	
	public AlertDialog gameDialog;
	public boolean gameDialogFlag;
	public boolean gameInfoPause;
	
	private boolean generated;
	public boolean gameOver;
	public boolean gameWin;
	
	/*// This is the local score you have earned in a single stage session.
	public long score;
	//This is the local score single session may have. Once the player reaches the end, add this to score.
	//If a player retries, temporaryScore b\must be set to 0, and score must be reset to its prior value.
	public long temporaryScore;
	// TODO: Total Score goes here. This will go to the High Score screen.
	public long totalScore;
	*/
	
	private long temporaryScore;
	private long highScore;
	private long temporaryTotalScore;
	private long totalScore;
	
	public boolean scored;
	// This is the amount of marbles needed to reach.
	public int marbleCount;
	
	public Stage(int w, int h) {
		width = w;
		height = h;
		data = new int[w * h];
		terrain = new ArrayList<Terrain>();
		paths = new ArrayList<Path>();
		marbles = new ArrayList<Marble>();
		coins = new ArrayList<Coin>();
		
		SIZE = (int) (32 * RenderView.AspectRatio);
		
		gameOver = false;
		gameWin = false;
		
		gameDialogFlag = false;
		gameDialog = null;
		gameInfoPause = true;
		
		synchronized (this) {
			generated = false;
		}
		//score = 0L;
		//temporaryScore = 0L;
		scored = false;
	}
	
	public synchronized void generate() {
		this.generated = false;
		while (!terrain.isEmpty())
			terrain.remove(0);
		while (!marbles.isEmpty())
			marbles.remove(0);
		while (!paths.isEmpty())
			paths.remove(0);
		
		Pipe p = null;
		CurvePipe cp = null;
		Coin c = null;
		Border border = new Border(0, 0);
		Funnel funnel = null;
		for (int i = 0; i < data.length; i++) {
			int w = i % width;
			int h = i / width;
			
			// Refactor this, as it causes unnecessary memory allocation.
			switch (data[i]) {
				case 0xFFFFFFFF:
				default:
					break;
				case 0xFFCCCC00:
					c = new Coin(ColorType.YELLOW, (float) w * SIZE, (float) h * SIZE);
					coins.add(c);
					c = null;
					break;
				case 0xFFCC0000:
					c = new Coin(ColorType.RED, (float) w * SIZE, (float) h * SIZE);
					coins.add(c);
					c = null;
					break;
				case 0xFF00CC00:
					c = new Coin(ColorType.GREEN, (float) w * SIZE, (float) h * SIZE);
					coins.add(c);
					c = null;
					break;
				case 0xFF0000CC:
					c = new Coin(ColorType.BLUE, (float) w * SIZE, (float) h * SIZE);
					coins.add(c);
					c = null;
					break;
				case 0xFFFF0000:
					tee = new Tee(w * SIZE, h * SIZE, RenderView.AspectRatio);
					tee.setPriority(1);
					cue = new Cue((width - 1) * SIZE, (height - 1) * SIZE, RenderView.AspectRatio);
					cue.setStartingPosition(tee);
					cue.setPriority(100);
					terrain.add(tee);
					break;
				case 0xFF00FF00:
					Marble m = new Marble((width - 1) * SIZE, (height - 1) * SIZE, i, RenderView.AspectRatio);
					m.setStartingPosition((float) w * SIZE, (float) h * SIZE);
					m.setPriority(100);
					marbles.add(m);
					m = null;
					break;
				case 0xFF000000:
					hole = new Hole((float) w * SIZE, (float) h * SIZE, RenderView.AspectRatio);
					hole.setPriority(2);
					terrain.add(hole);
					break;
				case 0xFF8563FF:
					p = new Pipe();
					p.setOrientation(Orientation.HORIZONTAL);
					p.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					p.setPriority(50);
					p.setAspectRatio(RenderView.AspectRatio);
					paths.add(p);
					break;
				case 0xFFFF7580:
					p = new Pipe();
					p.setOrientation(Orientation.VERTICAL);
					p.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					p.setPriority(50);
					p.setAspectRatio(RenderView.AspectRatio);
					paths.add(p);
					break;
				case 0xFF56FF9C:
					cp = new CurvePipe();
					cp.setOrientation(Orientation.LEFT_TO_UP);
					cp.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					cp.setPriority(50);
					cp.setAspectRatio(RenderView.AspectRatio);
					paths.add(cp);
					break;
				case 0xFFD154FF:
					cp = new CurvePipe();
					cp.setOrientation(Orientation.LEFT_TO_DOWN);
					cp.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					cp.setPriority(50);
					cp.setAspectRatio(RenderView.AspectRatio);
					paths.add(cp);
					break;
				case 0xFFFCFF60:
					cp = new CurvePipe();
					cp.setOrientation(Orientation.RIGHT_TO_UP);
					cp.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					cp.setPriority(50);
					cp.setAspectRatio(RenderView.AspectRatio);
					paths.add(cp);
					break;
				case 0xFF94FF7C:
					cp = new CurvePipe();
					cp.setOrientation(Orientation.RIGHT_TO_DOWN);
					cp.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					cp.setPriority(50);
					cp.setAspectRatio(RenderView.AspectRatio);
					paths.add(cp);
					break;
				case 0xFFFF00FF:
					border.borderWidth = (float) w * SIZE;
					border.borderHeight = (float) h * SIZE;
					break;
				case 0xFFFFAAFF:
					border.borderWidth = (float) w * SIZE;
					border.borderHeight = (float) h * SIZE;
					Void tile = new Void((float) w * SIZE, (float) h * SIZE, RenderView.AspectRatio);
					tile.setPriority(3);
					terrain.add(tile);
					tile = null;
					break;
				case 0xFF404040:
					Void voidTile = new Void((float) w * SIZE, (float) h * SIZE, RenderView.AspectRatio);
					voidTile.setPriority(3);
					terrain.add(voidTile);
					voidTile = null;
					break;
				case 0xFFFF9E7A:
					Bumper b = new Bumper((float) w * SIZE, (float) h * SIZE, RenderView.AspectRatio);
					b.setPriority(4);
					terrain.add(b);
					b = null;
					break;
				case 0xFF3C706D:
					funnel = new ShortFunnel(RenderView.AspectRatio);
					funnel.setDirection(Direction.LEFT);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF34124C:
					funnel = new ShortFunnel(RenderView.AspectRatio);
					funnel.setDirection(Direction.UP);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF380B0D:
					funnel = new ShortFunnel(RenderView.AspectRatio);
					funnel.setDirection(Direction.RIGHT);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF30510F:
					funnel = new ShortFunnel(RenderView.AspectRatio);
					funnel.setDirection(Direction.DOWN);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF4E6587:
					funnel = new Ramp(RenderView.AspectRatio);
					funnel.setDirection(Direction.LEFT);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF493F30:
					funnel = new Ramp(RenderView.AspectRatio);
					funnel.setDirection(Direction.RIGHT);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF443151:
					funnel = new Ramp(RenderView.AspectRatio);
					funnel.setDirection(Direction.UP);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFF415B40:
					funnel = new Ramp(RenderView.AspectRatio);
					funnel.setDirection(Direction.DOWN);
					funnel.setPlacement((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(funnel);
					funnel = null;
					break;
				case 0xFFA8FF1E:
					Connector connector = new Connector((float) w * SIZE, (float) h * SIZE, 0);
					paths.add(connector);
					break;
			}
		}
		terrain.add(0, border);
		border = null;
		reset();
	}
	
	public synchronized void render(final Canvas c, float ratio, final float centerX, final float centerY) {
		if (c == null)
			return;
		c.drawRGB(211, 148, 99);
		if (this.generated) {
			for (Terrain t : terrain) {
				if (t != null) {
					t.render(c, centerX, centerY);
				}
			}
			for (Path p : paths) {
				if (p != null) {
					p.render(c, centerX, centerY);
				}
			}
			for (Marble m : marbles) {
				if (m != null) {
					m.render(c, centerX, centerY);
				}
			}
			for (Coin k : coins) {
				if (k != null) {
					k.render(c, centerX, centerY);
				}
			}
			if (cue != null)
				cue.render(c, centerX, centerY);
		}
	}
	
	public synchronized void tick() {
		if (gameInfoPause)
			return;
		if (generated) {
			for (Terrain t : terrain) {
				t.tick(this);
			}
			for (Path p : paths) {
				p.tick(this);
			}
			for (Marble m : marbles) {
				m.tick(this);
				if (m.isAtGoal() && !(m.hasBeenScored())) {
					addTemporaryScore(m.SCORE);
					addTemporaryTotalScore(m.SCORE);
					m.setScoredFlag(true);
					marbleCount--;
				}
			}
			for (Coin c : coins) {
				c.tick(this);
			}
			cue.tick(this);
			if (marbleCount != -999) {
				if (marbleCount == 0) {
					gameWin = true;
					gameOver = false;
					if (!(this.getTotalScoredFlag())) {
						this.setTotalScoredFlag(true);
					}
				}
				else if (cue.hasReachedGoal() || cue.isDead()) {
					gameWin = false;
					gameOver = true;
				}
			}
			else {
				if (cue.hasReachedGoal()) {
					gameWin = true;
				}
				else if (cue.isDead()) {
					gameOver = true;
				}
			}
		}
	}
	
	public synchronized void reset() {
		gameOver = false;
		gameWin = false;
		resetGameScore();
		this.setTotalScoredFlag(false);
		for (Path p : paths)
			if (p != null)
				p.reset();
		for (Terrain t : terrain)
			if (t != null)
				t.reset();
		for (Coin c : coins) {
			if (c != null)
				c.reset();
		}
		if (cue != null)
			cue.reset();
		if (marbles.isEmpty())
			marbleCount = -999;
		else {
			for (Marble m : marbles)
				if (m != null)
					m.reset();
			marbleCount = marbles.size();
		}
		this.generated = true;
	}
	
	public boolean isGameOver() {
		return gameOver && cue.isDead();
	}
	
	public boolean hasWon() {
		return gameWin;
	}
	
	public void setGameDialog(AlertDialog dialog) {
		gameDialog = dialog;
	}
	
	public void buildGameDialog(AlertDialog.Builder builder) {
		if (builder != null)
			gameDialog = builder.create();
	}
	
	public void setGameDialogFlag() {
		if (gameDialog != null) {
			gameDialogFlag = false;
			gameInfoPause = true;
		}
	}
	
	public boolean checkGameDialogFlag() {
		return gameDialogFlag;
	}
	
	public boolean isGameDialogEmpty() {
		return gameDialog == null;
	}
	
	public void togglePauseFlag() {
		if (gameInfoPause)
			gameInfoPause = false;
	}
	
	public void addTemporaryScore(long v) {
		temporaryScore += v;
	}
	
	public void addTemporaryTotalScore(long v) {
		temporaryTotalScore += v;
	}
	
	public void setHighScore() {
		highScore = temporaryScore;
	}
	
	public void setNewAccumulatedScore() {
		totalScore = temporaryTotalScore;
	}
	
	public void resetGameScore() {
		temporaryScore = 0L;
		temporaryTotalScore = totalScore;
	}
	
	public long getHighScore() {
		return highScore;
	}
	
	public long getTemporaryScore() {
		return temporaryScore;
	}
	
	public long getNewAccumulatedScore() {
		return temporaryTotalScore;
	}
	
	public long getOldAccumulatedScore() {
		return totalScore;
	}
	
	public void clearGameScore() {
		totalScore = temporaryTotalScore = highScore = temporaryScore = 0L;
	}
	
	public void cloneScores(Stage s) {
		this.totalScore = s.totalScore;
		this.highScore = s.highScore;
		this.temporaryScore = 0;
		this.temporaryTotalScore = s.totalScore;
	}
	
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		this.togglePauseFlag();
	}
	
	public boolean hasFinishedGenerating() {
		return this.generated;
	}
	
	public void setTotalScoredFlag(boolean value) {
		scored = value;
	}
	
	public boolean getTotalScoredFlag() {
		return scored;
	}
	
}
