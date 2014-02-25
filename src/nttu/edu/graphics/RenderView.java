package nttu.edu.graphics;

import java.io.IOException;
import nttu.edu.activities.LevelSelectionActivity;
import nttu.edu.activities.PlayActivity;
import nttu.edu.handler.ImageInfo;
import nttu.edu.hud.BestScore;
import nttu.edu.hud.TimeBasedScore;
import nttu.edu.level.HUD;
import nttu.edu.level.Stage;
import nttu.edu.score.Format;
import nttu.edu.sound.Sound;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RenderView extends SurfaceView implements Runnable {
	SurfaceHolder holder;
	Handler tickHandler;
	Handler renderHandler;
	
	PlayActivity parent;
	Stage stage;
	
	//-------------------
	public HUD hud;
	BestScore best;
	public TimeBasedScore timeScore;
	//-------------------
	
	Thread thread = null;
	volatile boolean running = false;
	volatile boolean pausing = false;
	volatile boolean creating = false;
	int width;
	int height;
	AlertDialog dialogLoss;
	AlertDialog dialogWin;
	Dialog dialogPause;
	
	boolean dialogFlag;
	public static float AspectRatio;
	public static RectF bounds;
	public static float cameraX, cameraY;
	
	public RenderView(PlayActivity c) {
		super(c);
		this.parent = c;
		Display d = c.getWindowManager().getDefaultDisplay();
		this.width = d.getWidth();
		this.height = d.getHeight();
		AspectRatio = c.getAspectRatio();
		timeScore = TimeBasedScore.loadTimers(parent, "dialogue/time.txt", timeScore);
	}
	
	public void resume() {
		this.holder = this.getHolder();
		best = new BestScore(Format.loadFormat(parent));
		running = true;
		pausing = false;
		creating = false;
		thread = new Thread(this);
		thread.setName("Game Thread");
		thread.start();
	}
	
	public void pause() {
		running = false;
		pausing = true;
		creating = false;
		
		boolean retry = true;
		if (thread != null) {
			while (retry) {
				try {
					thread.join();
					retry = false;
				}
				catch (InterruptedException e) {
					retry = true;
				}
			}
		}
	}
	
	public void resetGame() {
		pausing = false;
		stage.reset();
		timeScore.reset();
	}
	
	public void pauseGame() {
		pausing = true;
		timeScore.pauseTimer();
		dialogPause.show();
	}
	
	public void unpauseGame() {
		pausing = false;
		timeScore.unpauseTimer();
	}
	
	public boolean isGamePaused() {
		return pausing;
	}
	
	public synchronized void tick() {
		if (tickHandler != null) {
			tickHandler.post(new Runnable() {
				public void run() {
					if (!stage.gameInfoPause)
						timeScore.execute();
					if (!pausing) {
						stage.tick();
						hud.tick(stage);
						best.tick(stage);
						if (!stage.hasWon() && !stage.isGameOver())
							timeScore.unpauseTimer();
					}
					else
						timeScore.pauseTimer();
				}
			});
		}
		if (!stage.isGameDialogEmpty() && stage.gameDialogFlag == false) {
			Thread g = new Thread(new Runnable() {
				public void run() {
					parent.runOnUiThread(new Runnable() {
						public void run() {
							stage.gameDialog.show();
						}
					});
				}
			});
			g.setName("Game Dialog Thread");
			g.start();
			stage.gameDialogFlag = true;
		}
		if (stage.isGameOver() && this.dialogFlag == false) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					parent.runOnUiThread(new Runnable() {
						public void run() {
							// SCORE - This is where we retrieve the score for each individual stage.
							timeScore.pauseTimer();
							stage.resetGameScore();
							parent.setScoreText("Currently Accumulated Score: ", stage.getOldAccumulatedScore(), 0);
							dialogLoss.show();
						}
					});
				}
			});
			t.setName("Losing Dialog Thread");
			t.start();
			this.dialogFlag = true;
		}
		if (stage.hasWon() && this.dialogFlag == false) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					parent.runOnUiThread(new Runnable() {
						public void run() {
							timeScore.pauseTimer();
							stage.addTemporaryScore(timeScore.getScore());
							stage.addTemporaryTotalScore(timeScore.getScore());
							stage.setHighScore();
							stage.setNewAccumulatedScore();
							if (parent.stageNumber <= LevelSelectionActivity.MAX_STAGES) {
								// SCORE - This is where we retrieve the score for each individual stage.
								parent.setScoreText("Accumulated Score: ", stage.getNewAccumulatedScore(), 1);
							}
							dialogWin.show();
						}
					});
				}
			});
			t.setName("Winning Dialog Thread");
			t.start();
			this.dialogFlag = true;
		}
	}
	
	public synchronized void render() {
		if (holder != null) {
			if (renderHandler != null) {
				renderHandler.post(new Runnable() {
					public void run() {
						if (holder.getSurface().isValid()) {
							Canvas c = holder.lockCanvas();
							if (c == null)
								return;
							if (bounds == null) {
								bounds = new RectF(c.getClipBounds());
								bounds.set(bounds.left - 32f, bounds.top - 32f, bounds.right + 32f, bounds.bottom + 32f);
							}
							stage.render(c, AspectRatio, width / 2, height / 2);
							hud.render(c);
							best.render(c);
							timeScore.render(c);
							if (stage.isGameOver()) {
								c.drawBitmap(Art.gameOver, (width - (Art.gameOver.getWidth())) / 2, (height - (Art.gameOver.getHeight())) / 4, null);
								c.drawBitmap(Art.gameOver, (width - (Art.gameOver.getWidth())) / 2, (height - (Art.gameOver.getHeight())) / 4 * 3, null);
							}
							holder.unlockCanvasAndPost(c);
						}
					}
				});
			}
		}
	}
	
	public void run() {
		while (running) {
			if (!creating) {
				tick();
				render();
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setHandler(Handler tick, Handler render) {
		tickHandler = tick;
		renderHandler = render;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void setLosingDialog(AlertDialog d) {
		dialogLoss = d;
		dialogFlag = false;
	}
	
	public AlertDialog getLosingDialog() {
		return dialogLoss;
	}
	
	public void setWinningDialog(AlertDialog d) {
		dialogWin = d;
		dialogFlag = false;
	}
	
	public AlertDialog getWinningDialog() {
		return dialogWin;
	}
	
	public void setPausingDialog(Dialog pauseDialog) {
		dialogPause = pauseDialog;
		dialogFlag = false;
	}
	
	public Dialog getPausingDialog() {
		return dialogPause;
	}
	
	public void setDialogFlag(boolean value) {
		dialogFlag = value;
	}
	
	public void createStage(PlayActivity a) throws IOException {
		creating = true;
		try {
			if (a.stageNumber <= LevelSelectionActivity.MAX_STAGES)
				this.stage = Art.loadStage(a, this.stage, a.stageNumber);
			else
				throw new IOException("Not allowed to load targeted stage.");
		}
		catch (IOException e) {
			creating = false;
			this.pause();
			throw e;
		}
		this.stage.gameDialog = ImageInfo.createImageDialog(a, stage);
		this.stage.setGameDialogFlag();
		this.stage.generate();
		this.hud = Art.loadHUD(this);
		timeScore.resetAndLoad(a.stageNumber);
		dialogFlag = false;
		creating = false;
		if (Sound.pool != null) {
			Sound.emergencyLoad(parent, parent.getAssets());
		}
		a.getLoadingScreenHandler().sendEmptyMessage(0);
	}
	
	public void clearTotalScore() {
		stage.clearGameScore();
	}
}
