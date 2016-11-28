package cslp;

public class BinExceedThresholdEvent extends AbstractEvent {

	private Bin bin;
	
	public BinExceedThresholdEvent(int eventTime, Bin bin) {
		schedule(eventTime);
		this.bin = bin;
	}
	
	@Override
	public void execute(Simulator simulator) {
		// TODO Auto-generated method stub
		
	}

	
}
