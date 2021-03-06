package com.minespaceships.mod.menu.functionalMenus.spaceshipNavigation;

import net.minecraft.util.BlockPos;

import com.minespaceships.mod.menu.FunctionalMenu;
import com.minespaceships.mod.menu.FunctionalParamMenu;
import com.minespaceships.mod.menu.Menu;
import com.minespaceships.mod.overhead.CustomGuiChat;
import com.minespaceships.mod.overhead.IMenuInterface;
import com.minespaceships.mod.spaceship.Shipyard;
import com.minespaceships.mod.spaceship.Spaceship;
import com.minespaceships.mod.spaceship.SpaceshipCommands;

/**
 * Move the spaceship position up.
 * @author ovae.
 * @version 20150402.
 */
public class LiftoffMenu extends Menu implements FunctionalMenu{

	/**
	 * Creates a new liftoffMenu.
	 * @param name
	 */
	public LiftoffMenu() {
		super("Liftoff");
	}

	/**
	 * The functionality of the menu is activated by this method.
	 * @param command
	 * @param terminal
	 */
	@Override
	public String activate(IMenuInterface terminal, String data) {
		terminal.getChatRegisterEntity().executeCommand(SpaceshipCommands.moveUp+" 23", terminal.getPlayerEntity());
		terminal.display(SpaceshipCommands.liftoff+"\nPress m to get back.", terminal.getPlayerEntity(), false);
		return "";
	}

	@Override
	public String getData() {
		return null;
	}

}
