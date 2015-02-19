/**
 * 
 */
package com.minespaceships.mod.spaceship;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.minespaceships.mod.overhead.ChatRegisterEntity;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * @author jannes
 *
 */
public class SpaceshipCommands {

	public static void init(String command, final WorldServer worldObj, final ChatRegisterEntity commandBlock, final EntityPlayer player) {		
		command = command.substring("init".length()).replaceAll("\\s", "");

		Pattern poffset = Pattern.compile("([\\-\\+]?[0-9]+);([\\-\\+]?[0-9]+);([\\-\\+]?[0-9]+)to([\\-\\+]?[0-9]+);([\\-\\+]?[0-9]+);([\\-\\+]?[0-9]+)");
		Matcher moffset = poffset.matcher(command);
		if(moffset.matches()) {
			BlockPos from = new BlockPos(Integer.valueOf(moffset.group(1)), Integer.valueOf(moffset.group(2)), Integer.valueOf(moffset.group(3)));
			BlockPos to = new BlockPos(Integer.valueOf(moffset.group(4)), Integer.valueOf(moffset.group(5)), Integer.valueOf(moffset.group(6)));
			commandBlock.setShip(new Spaceship(from, commandBlock.getPos(), to, worldObj));
			
			player.addChatComponentMessage(new ChatComponentText("Initialized Spaceship at [" + commandBlock.getPos().getX() + "; " + commandBlock.getPos().getY() + "; " + commandBlock.getPos().getZ() + "] from (" + moffset.group(1) + "; " + moffset.group(2) + "; " + moffset.group(3) + ") to (" + moffset.group(4) + "; " + moffset.group(5) + "; " + moffset.group(6) + ")"));
		} else {
			player.addChatComponentMessage(new ChatComponentText("init: Error processing intput"));
			player.addChatComponentMessage(new ChatComponentText("usage: init #;#;# to #;#;#"));
		}
	}
	
	public static void move(String command, final WorldServer worldObj, final ChatRegisterEntity commandBlock, final EntityPlayer player) {
		command = command.substring("move".length()).replaceAll("\\s", "");
		
		Spaceship ship = commandBlock.getShip();
		if(ship == null) {
			player.addChatComponentMessage(new ChatComponentText("move: Please initialize the Spaceship first"));
			return;
		}

		Pattern poffset = Pattern.compile("([\\-\\+]?[0-9]+);([\\-\\+]?[0-9]+);([\\-\\+]?[0-9]+)");
		Matcher moffset = poffset.matcher(command);
		if(moffset.matches()) {
			BlockPos vec_move = new BlockPos(Integer.valueOf(moffset.group(1)), Integer.valueOf(moffset.group(2)), Integer.valueOf(moffset.group(3)));
			ship.moveTo(vec_move);
			
			player.addChatComponentMessage(new ChatComponentText("Moved Spaceship by (" + moffset.group(1) + "; " + moffset.group(2) + "; " + moffset.group(3) + ")"));
		} else {
			player.addChatComponentMessage(new ChatComponentText("move: Error processing intput"));
			player.addChatComponentMessage(new ChatComponentText("usage: move #;#;#"));
		}
	}

}
