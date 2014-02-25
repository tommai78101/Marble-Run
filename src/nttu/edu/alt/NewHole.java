package nttu.edu.alt;

import nttu.edu.entity.Entity;
import nttu.edu.level.Stage;
import android.graphics.Canvas;

public class NewHole extends Entity implements Obstacle {
	
	@Override
	public void tick(Stage s) {
	}
	
	@Override
	public void render(Canvas c, float centerX, float centerY) {
	}
	
	@Override
	public void reset() {
	}
	
	public boolean check(Obstacle obstacle) {
		return false;
	}
	
	public void respond(Obstacle obstacle) {
	}
	
}
