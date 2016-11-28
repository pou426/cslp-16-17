package cslp;

public class BinOverflowEvent extends AbstractEvent {

	private Bin bin;
	
	public BinOverflowEvent(int eventTime, Bin bin) {
		schedule(eventTime);
		this.bin = bin;
	}
	@Override
	public void execute(Simulator simulator) {
		// TODO Auto-generated method stub
		
	}

}
