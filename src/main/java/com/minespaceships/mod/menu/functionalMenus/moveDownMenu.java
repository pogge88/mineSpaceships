package com.minespaceships.mod.menu.functionalMenus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import com.minespaceships.mod.menu.FunctionalParamMenu;
import com.minespaceships.mod.menu.Menu;
import com.minespaceships.mod.overhead.CustomGuiChat;
import com.minespaceships.mod.spaceship.Spaceship;
import com.minespaceships.mod.spaceship.SpaceshipCommands;

/**
 * 
 * @author ovae.
 * @version 20150226
 */
public class moveDownMenu extends Menu implements FunctionalParamMenu{

	//The terminal to write in.
	private CustomGuiChat terminal;

	public moveDownMenu(String name, CustomGuiChat terminal) {
		super(name);
		this.terminal=terminal;
	}

	@Override
	public String activate(String command) {
		if(command.trim().isEmpty()){
			return "command can not be empty.";
		}
		if(command.equals(null)){
			return "command can not be null.";
		}
		int playerRotation = MathHelper.floor_double((double)(terminal.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		command = SpaceshipCommands.processDirectionMoveCommand(command, playerRotation);
		
		double x,y,z;
		Pattern pattern = Pattern.compile("([\\-\\+]?[0-9]*[\\.]?[0-9]+);[\\s*]?([\\-\\+]?[0-9]*[\\.]?[0-9]*);[\\s*]?([\\-\\+]?[0-9]*[\\.]?[0-9]*)");
		Matcher matcher = pattern.matcher(command);
		if(matcher.matches()){
			x = Double.valueOf(matcher.group(1));
			y = Double.valueOf(matcher.group(2));
			z = Double.valueOf(matcher.group(3));

			try{
				Spaceship ship = this.terminal.getChatRegisterEntity().getShip();
				//(double)x, (double)y, (double)z
				BlockPos position = new BlockPos(x, y, z);
				if(ship == null) {
					this.terminal.display("move: Please initialise the Spaceship first", true);
				}
				ship.moveTo(position);
				
				return ">> To target <<";
			}catch(Exception e){
				System.err.println("ship is broken");
			}
		}
		return "move down not implemented yet!";
	}

}