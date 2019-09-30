package backtracking;

public class Square {
	int x;
	int y;
	String group;
	int value;
	int id;
	Square(String group, int id, int x, int y) {
		this.group = group;
		this.x = x;
		this.y = y;
		this.value = 0;
		this.id = id;
	}
	
	public int getVal() {return this.value; }
	public void changeVal(int newValue) {this.value = newValue;}
	public String getGroup() { return this.group; }	
	public int getID() {return this.id;}
	public boolean insertVal(int x) { 
		this.value = x;
		return true;
	}
	public int[] getCoordinates() {
		int[] coordinates = {this.x, this.y};
		return coordinates;
	}
	public boolean beside(Square forComparison) {
		if ((this.getCoordinates()[0] == forComparison.getCoordinates()[0]) || (this.getCoordinates()[1] == forComparison.getCoordinates()[1])) {
			return true;
		}
		return false;
	}
	

}
