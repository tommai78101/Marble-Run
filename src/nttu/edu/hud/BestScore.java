package nttu.edu.hud;

import nttu.edu.graphics.RenderView;
import nttu.edu.level.Stage;
import nttu.edu.score.Format;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class BestScore {
	private Paint paint;
	private Paint bgPaint;
	private long score;
	private float textSize;
	private long highScore;
	private long accumulatedScore;
	private Format format;
	
	public BestScore(Format f) {
		textSize = 20f;
		format = f;
		paint = new Paint();
		paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		paint.setColor(Color.WHITE);
		paint.setTextSize(textSize);
		bgPaint = new Paint();
		bgPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
		bgPaint.setColor(Color.BLACK);
		bgPaint.setTextSize(textSize);
	}
	
	public void render(Canvas c) {
		c.drawText("High Stage Score: " + highScore, 10 * RenderView.AspectRatio, textSize * RenderView.AspectRatio, bgPaint);
		c.drawText("High Stage Score: " + highScore, 10 * RenderView.AspectRatio + 1f, textSize * RenderView.AspectRatio + 1f, paint);
		c.drawText("Current Stage Score: " + score, 10 * RenderView.AspectRatio, (textSize * 2) * RenderView.AspectRatio, bgPaint);
		c.drawText("Current Stage Score: " + score, 10 * RenderView.AspectRatio + 1f, (textSize * 2) * RenderView.AspectRatio + 1f, paint);
		c.drawText("Accumulated Score: " + accumulatedScore, 10 * RenderView.AspectRatio, (textSize * 3) * RenderView.AspectRatio, bgPaint);
		c.drawText("Accumulated Score: " + accumulatedScore, 10 * RenderView.AspectRatio + 1f, (textSize * 3) * RenderView.AspectRatio + 1f, paint);
	}
	
	public void tick(Stage s) {
		if (format != null) {
			score = s.getTemporaryScore();
			accumulatedScore = s.getNewAccumulatedScore();
			long temp = Long.valueOf(format.getScore(s.number));
			highScore = temp > score ? temp : score;
		}
	}
}
