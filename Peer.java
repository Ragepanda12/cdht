public class Peer {
	private int self;
	private int firstChild;
	private int secondChild;
   private int firstPredecessor;
   private int secondPredecessor;
	private int port;
	private final int addPort = 50000;

	public Peer(int identity){
		self = identity;
		port = identity + addPort;
	}
	public void setFirstChild(int child){
		firstChild = child;
	}
	public void setSecondChild(int child){
		secondChild = child;
	}
   public void setFirstPredecessor(int predecessor){
      this.firstPredecessor = predecessor;
   }
   public void setSecondPredecessor(int predecessor){
      this.secondPredecessor = predecessor;
   }
   public int getFirstPredecessor(){
      return this.firstPredecessor;
   }
   public int getSecondPredecessor(){
      return this.secondPredecessor;
   }
	public int getPort(){
		return this.port;
	}
	public int getFirstChild(){
		return this.firstChild;
	}
	public int getSecondChild(){
		return this.secondChild;
	}
	
}
