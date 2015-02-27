package com.minespaceships.mod.overhead;

import java.util.regex.*;

import com.example.examplemod.ovae.terminalMenu;
import com.minespaceships.mod.menu.SpaceshipMenu;
import com.minespaceships.mod.menu.MenuDisplay;
import com.minespaceships.mod.menu.NoSpaceshipEntityMenu;
import com.minespaceships.mod.spaceship.Shipyard;
import com.minespaceships.mod.spaceship.Spaceship;
import com.minespaceships.mod.spaceship.SpaceshipCommands;
import com.minespaceships.mod.spaceship.Turn;
import com.minespaceships.util.BlockCopier;
import com.minespaceships.util.Calculator;
import com.minespaceships.util.IMoveable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ChatRegisterEntity extends TileEntity {
	
	//Attributes
	private Spaceship ship;
	private WorldServer remoteWorld;

	private CustomGuiChat terminal;
	private MenuDisplay spaceshipMenu;
	private MenuDisplay noSpaceshipMenu;
	
	private static String recoverSpaceshipMeasures = "recoverSpaceshipMeasurements";
	
	public ChatRegisterEntity() {
		super();		
	}
	@Override
	public void setPos(BlockPos pos){
		super.setPos(pos);
		remoteWorld = (WorldServer)MinecraftServer.getServer().getEntityWorld();
		if(worldObj != null && worldObj == remoteWorld){
			Shipyard.getShipyard().addNavigator(this);
		}
	}
	@Override
	public void invalidate(){
		Shipyard.getShipyard().removeNavigator(this);
		super.invalidate();
	}

	@Override
	public void writeToNBT(NBTTagCompound par1)
	{
	   super.writeToNBT(par1);
	   if(ship != null){
		   int[] a = ship.getOriginMeasurementArray();
		   par1.setIntArray(recoverSpaceshipMeasures, a);
	   }	   
	}

	@Override
	public void readFromNBT(NBTTagCompound par1)
	{
	   super.readFromNBT(par1);
	   int[] measurements = par1.getIntArray(recoverSpaceshipMeasures);
	   if(measurements != null){
		   ship = new Spaceship(measurements);
	   }
	}

	/**
	 * Activates the TileEntity and opens a custom chat to the player
	 * @param player
	 */
	public void Activate(EntityPlayer player){
		//check if the player is our local player, so one cannot open a console for another player
		//on the server
		if(player.equals(Minecraft.getMinecraft().thePlayer)){
			//initialise the terminal
			//terminal = new CustomGuiChat(player, (ChatRegisterEntity)remoteWorld.getTileEntity(pos));
			terminal = new CustomGuiChat(player, this);
			
			//Initialise a default menu for testing reasons
			if(!SpaceshipMenu.getRunBefore()){
				SpaceshipMenu.initMenu(terminal);
			}
			if(!NoSpaceshipEntityMenu.getRunBefore()){
				NoSpaceshipEntityMenu.initMenu(terminal);
			}

			//initialise the menu display.
			spaceshipMenu = new MenuDisplay(SpaceshipMenu.getRootMenu(), terminal);
			noSpaceshipMenu = new MenuDisplay(NoSpaceshipEntityMenu.getRootMenu(), terminal);

			//open our console. 
			Minecraft.getMinecraft().displayGuiScreen(terminal);

			if(terminal.getChatRegisterEntity().getShip() == null){
				noSpaceshipMenu.displayMain(NoSpaceshipEntityMenu.getRootMenu());
			}else{
				//Print out the menu in the console.
				spaceshipMenu.displayMain(SpaceshipMenu.getRootMenu());
			}
		}
	}
	public void setRemoteWorld(WorldServer world){
		remoteWorld = world;
	}
	public WorldServer getRemoteWorld(){
		return remoteWorld;
	}
	/**
	 * Executes the given command, regardless who committed it.
	 * @param command
	 */
	public void onCommand(String command){
		
	}
	/**
	 * Executing the given command considering the player that sent it.
	 * @param command
	 * @param player
	 */
	public void onCommand(String command, EntityPlayer player){
		//display the menu.
		spaceshipMenu.display(command);

		//define a very first command to see if it works.
		if(command.equals("hello")){
			//send something to the player to see if we get a feedback from our command.
			player.addChatComponentMessage(new ChatComponentText("I love you!"));
		//Define the 'calc' command, which parses a math expression
		} else if(command.startsWith("calc")) {
			Calculator.calc(command, player);
		} else if (command.startsWith("init")) {
			SpaceshipCommands.init(command, remoteWorld, this, player, ship);
		} else if (command.startsWith("move")) {
			SpaceshipCommands.move(command, remoteWorld, this, player, ship);
		} else if (command.equals("test1")) {
			SpaceshipCommands.init("init -4;-4;-4 to 4;4;4", remoteWorld, this, player, ship);
			SpaceshipCommands.move("move 0;15;0", remoteWorld, this, player, ship);
		} else if (command.startsWith("turn ")) {
			command = command.substring(4).trim();
			if (command.equals("left")) {
				Turn.ninetyDeg(worldObj, pos, Turn.LEFT);
			} else if (command.equals("right")) {
				Turn.ninetyDeg(worldObj, pos, Turn.RIGHT);
			} else if (command.equals("around")) {
				Turn.around(worldObj, pos);
			} else {
				player.addChatComponentMessage(new ChatComponentText("Invalid direction! Only left, right or around!"));
			}
		} else if(command.equals("status")) {
			SpaceshipCommands.status(remoteWorld, this, player, ship);
		}
		terminalMenu.onCommand(command, player);
	}

	public void setShip(Spaceship ship) {
		ChatRegisterEntity cre = (ChatRegisterEntity)remoteWorld.getTileEntity(pos);
		if(cre != null){
			cre.ship = ship;
		}
	}	
	@Deprecated
	public void hardSetShip(Spaceship ship){
		this.ship = ship;
	}
	public Spaceship getShip() {
		ChatRegisterEntity entity = ((ChatRegisterEntity)remoteWorld.getTileEntity(pos));
		if(entity != null){
			return entity.ship;
		} else {
			return null;
		}
	}
	@Deprecated
	public void createShip(BlockPos minSpan, final BlockPos origin, final BlockPos maxSpan, WorldServer worldS){
		setShip(new Spaceship(minSpan, origin, maxSpan, worldS));
	}
	public void createShip(BlockPos initial, WorldServer worldS) throws Exception{
		setShip(new Spaceship(initial, worldS));
	}
}
