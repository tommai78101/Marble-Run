package nttu.edu.alt;

public interface Obstacle {
	public boolean check(Obstacle obstacle);
	
	public void respond(Obstacle obstacle);
}
