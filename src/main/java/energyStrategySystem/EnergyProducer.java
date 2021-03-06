package energyStrategySystem;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EnergyProducer implements IEnergyC{
	private int producedEnergy;
	private boolean status;
	private int prior;
	
	public EnergyProducer(int energy, int priority){
		if(priority<1||priority>3){
			throw new IllegalArgumentException ("priority must be 1, 2 or 3");
		}
		prior= priority;
		
		producedEnergy=energy;
	
		
		
	}
	
	public void setStatus(boolean b){
		status=b;
	}
	
	public int getPriority(){
		return prior;
	}
	
	
	public boolean getStatus(){
		return status;
	}
	
	
	public int getEnergy(){
		return producedEnergy;
	}
	
	@Override
	public String toString(){
		return "Energy: " +producedEnergy +'\n';
	}

	@Override
	public boolean getStatus(BlockPos pos, World world) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setStatus(boolean b, BlockPos pos, World world,
			boolean sendChange) {
		// TODO Auto-generated method stub
		
	}
	


}
