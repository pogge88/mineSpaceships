package com.minespaceships.mod.spaceship;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.vecmath.Vector3d;

import com.google.common.collect.ImmutableList;
import com.minespaceships.mod.overhead.ChatRegisterEntity;
import com.minespaceships.util.BlockCopier;
import com.minespaceships.util.Vec3Op;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class Spaceship {
	private BlockPos minPosition;
	private BlockPos maxPosition;
	private BlockPos span;
	private BlockPos origin;
	private WorldServer worldS;	
	private Vector<ChatRegisterEntity> navigators;
	private BlockMap blockmap;
	
	public Spaceship(final BlockPos minPosition,final BlockPos maxPosition, WorldServer worldS){
		setMeasurements(minPosition, maxPosition);
		this.worldS = worldS;
		initializeBase();
	}
	
	public Spaceship(final BlockPos minPosition, int dimX, int dimY, int dimZ, WorldServer worldS){
		BlockPos recSpan = new BlockPos(dimX, dimY, dimZ);
		setMeasurements(minPosition, ((BlockPos) recSpan).add(minPosition));
		this.worldS = worldS;
		initializeBase();
	}
	
	public Spaceship(final BlockPos minSpan, final BlockPos origin, final BlockPos maxSpan, WorldServer worldS){
		setMeasurements(((BlockPos) minSpan).add(origin), ((BlockPos) maxSpan).add(origin));
		this.origin = origin;
		this.worldS = worldS;
		initializeBase();
	}
	
	public Spaceship(int[] originMeasurement){
		readOriginMeasurementArray(originMeasurement);
		worldS = (WorldServer)MinecraftServer.getServer().getEntityWorld();
		initializeBase();
	}
	
	private void initializeBase(){
		navigators = new Vector<ChatRegisterEntity>();
		Shipyard.getShipyard().addShip(this);
	}
	
	public BlockPos getMinPosition(){
		return minPosition;
	}
	
	public BlockPos getMaxPosition(){
		return maxPosition;
	}
	
	public void addNavigator(ChatRegisterEntity nav){
		if(!navigators.contains(nav)){
			navigators.add(nav);
		}
	}
	
	public void removeNavigator(BlockPos entity){
		navigators.remove(entity);
	}
	
	public int getNavigatorCount(){
		return navigators.size();
	}
	
	public int[] getOriginMeasurementArray(){
		BlockPos minSpan = minPosition.subtract(origin);
		BlockPos maxSpan = maxPosition.subtract(origin);
		int[] a = {minSpan.getX(), minSpan.getY(), minSpan.getZ(),
				origin.getX(), origin.getY(), origin.getZ(),
				maxSpan.getX(), maxSpan.getY(), maxSpan.getZ()};
		return a;
	}
	
	public void readOriginMeasurementArray(int[] array){
		try {
			BlockPos minSpan = new BlockPos(array[0], array[1], array[2]);
			BlockPos maxSpan = new BlockPos(array[6], array[7], array[8]);
			origin = new BlockPos(array[3], array[4], array[5]);
			setMeasurements(minSpan.add(origin), maxSpan.add(origin));
			origin = new BlockPos(array[3], array[4], array[5]);
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Could not read OriginMeasurementArray (probably an error with NBT). Try creating a new World.");
			System.out.println("Printing Exception Stack:");
			System.out.println(ex.getMessage());
		}
	}
	
	private void setMeasurements(final BlockPos minPos, final BlockPos maxPos){
		minPosition = minPos;
		maxPosition = maxPos;
		span = ((BlockPos) maxPos).subtract(minPos);
		origin = Vec3Op.scale(span, 0.5);
	}
	
	public void setTarget(BlockPos position){
		moveTo(position.subtract(origin));
	}
	
	public void moveTo(BlockPos addDirection){
		//copyTo(addDirection, worldC);
		moveTo(addDirection, worldS);
	}
	
	private void moveTo(BlockPos addDirection, World world){
		//list of positions left to be build
		Vector<BlockPos> position = new Vector<BlockPos>();
		//list of positions that need to be removed in revers order to prevent other blocks from cracking
		Vector<BlockPos> removal = new Vector<BlockPos>();
		
		//get all positions that can't be placed right now
		BlockPos add = new BlockPos(addDirection);
		for(int x = 0; x < span.getX(); x++){
			for(int y = 0; y < span.getY(); y++){
				for(int z = 0; z < span.getZ(); z++){
					BlockPos Pos = new BlockPos(x,y,z);
					Pos = Pos.add(minPosition);
					Block block = world.getBlockState(Pos).getBlock();
					if(!block.isAir(world, Pos)){
						BlockPos nextPos = Pos.add(add);
						if(block.canPlaceBlockAt(world, nextPos)){
							//build the buildable block
							BlockCopier.copyBlock(world, Pos, nextPos);
							//remember to remove it
							removal.add(Pos);
						} else {
							//remember to buid the Block later
							position.add(Pos);
						}
					}
				}
			}
		}
		//get through all the unbuildable positions that are left and build them until all have been moved.
		//also make a safety layer. If after some layers of fragilness the blocks still can't be placed they certainly never will.
		int i = 0;
		while(!position.isEmpty() && i < 3){
			Iterator<BlockPos> posIterator = position.iterator();
			while(posIterator.hasNext()){
				BlockPos Pos = posIterator.next();
				Block block = world.getBlockState(Pos).getBlock();
				if(!block.isAir(world, Pos)){
					BlockPos nextPos = Pos.add(add);
					if(block.canPlaceBlockAt(world, nextPos)){
						BlockCopier.copyBlock(world, Pos, nextPos);
						posIterator.remove();
						//again: remember to remove the Block
						removal.add(Pos);
					}
				}
			}
			i++;
		}
		//if there are blocks left
		if(i == 3){
			for(BlockPos Pos : position){
				//force placement
				BlockPos nextPos = Pos.add(add);
				BlockCopier.copyBlock(world, Pos, nextPos);
				//again: remember to remove the Block. Now we need to append these at the front as they make problems when deleted last. This is cause of some deep Minecraft thingy
				removal.insertElementAt(Pos, 0);
			}
		}
		//remove the Blocks in reversed order, so that the most fragile ones are removed last.
		ListIterator<BlockPos> reverseRemoval = removal.listIterator(removal.size());
		while(reverseRemoval.hasPrevious()){
			BlockCopier.removeBlock(world, reverseRemoval.previous());
		}
		//Render the ship, move the entities and move the ships measurements
		world.markBlockRangeForRenderUpdate(minPosition, maxPosition);  
		moveEntities(addDirection);
		for(ChatRegisterEntity ent : navigators){
			if(ent.getShip() != null){
				ent.getShip().moveMeasurements(addDirection);
			}
		}
	}
	
	private void moveMeasurements(BlockPos addDirection){
		minPosition = minPosition.add(addDirection);
		maxPosition = maxPosition.add(addDirection);
		origin = origin.add(addDirection);
	}
	
	private void moveEntities(BlockPos addDirection){
		List<Entity> entities = worldS.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(minPosition, maxPosition));
		for(Entity ent : entities){			
			if(ent instanceof EntityPlayer){
				((EntityPlayer)ent).addPotionEffect(new PotionEffect(Potion.blindness.getId(),10));
			}
			Vec3 newPos = ent.getPositionVector().add(new Vec3(addDirection.getX(), addDirection.getY(), addDirection.getZ()));
			ent.setPositionAndUpdate(newPos.xCoord, newPos.yCoord, newPos.zCoord);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("minPosition: " + minPosition.toString());
		sb.append("\nmaxPosition: " + maxPosition.toString());
		sb.append("\nspan: " + span.toString());
		sb.append("\norigin: " + origin.toString());
		sb.append("\nworldServer: " + worldS == null ? "Not Known.\n" : "Known\n");
		return sb.toString();
	}
	
	public World getWorld() {
		return this.worldS;
	}
	
	public Boolean contains(BlockPos pos) {
		return this.blockmap.contains(pos);
	}
	
	public void remove(BlockPos pos) {
		this.blockmap.remove(pos);
	}
}
