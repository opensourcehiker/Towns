package town.town;

import org.bukkit.Chunk;

public class SimpleChunk {
	private int x;
	private int z;
	
	public SimpleChunk(int x, int z) {
		this.setX(x);
		this.setZ(z);
	}
	
	public SimpleChunk(Chunk chunk) {
		this.setX(chunk.getX());
		this.setZ(chunk.getZ());
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

}
