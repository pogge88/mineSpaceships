package com.minespaceships.mod.menu.functionalMenus.spaceshipNavigation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.minespaceships.mod.CommandMessage;
import com.minespaceships.mod.MineSpaceships;
import com.minespaceships.mod.menu.FunctionalMenu;
import com.minespaceships.mod.menu.Menu;
import com.minespaceships.mod.overhead.CustomGuiChat;
import com.minespaceships.mod.overhead.IMenuInterface;
import com.minespaceships.mod.spaceship.Shipyard;
import com.minespaceships.mod.spaceship.Spaceship;
import com.minespaceships.mod.spaceship.SpaceshipCommands;

/**
 * Lands the spaceship.
 * @author ovae.
 * @version 20150323.
 */
public class LandingMenu extends Menu implements FunctionalMenu{

	/**
	 * Creates a new landingMenu.
	 * @param name
	 */
	public LandingMenu() {
		super("land");
	}

	/**
	 * The functionality of the menu is activated by this method.
	 * @param command
	 * @param terminal
	 */
	@Override
	public String activate(String command, IMenuInterface terminal) {
		if(command.trim().isEmpty()){
			return "command can not be empty.";
		}
		if(command.equals(null)){
			return "command can not be null.";
		}

		terminal.getChatRegisterEntity().onCommand(SpaceshipCommands.land, terminal.getPlayerEntity());
		return SpaceshipCommands.land+" not implementes yet.\nPress m to get back.";
	}

}