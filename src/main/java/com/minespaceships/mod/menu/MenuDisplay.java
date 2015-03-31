package com.minespaceships.mod.menu;

import java.util.ArrayList;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.minespaceships.mod.CommandMessage;
import com.minespaceships.mod.MineSpaceships;
import com.minespaceships.mod.overhead.ChatRegisterEntity;
import com.minespaceships.mod.overhead.CustomGuiChat;
import com.minespaceships.mod.overhead.IMenuInterface;
import com.minespaceships.mod.spaceship.Spaceship;

/**
 * This class displays a menu structure.
 * @author ovae.
 * @version 20150331.
 */
public class MenuDisplay {

	//The terminal to write in.
	private IMenuInterface terminal;

	//The root of the menu structure.
	private Menu root;

	/**
	 * Creates a new MenuDisplay.
	 * @param root
	 * @param terminal
	 */
	public MenuDisplay(final Menu root, final IMenuInterface terminal){
		if(root.equals(null)){
			throw new IllegalArgumentException("root can not be null.");
		}
		if(terminal.equals(null)){
			throw new IllegalArgumentException("termianl can not be null.");
		}

		this.root = root;
		this.terminal = terminal;
	}

	/**
	 * Get the currently selected menu and prepares the output of that menu.
	 * @param menu
	 * @param command
	 */
	private String preparingOutput(final Menu menu, final String command){
		String out = "";
		if(menu == null){
			return EnumChatFormatting.RED+"unknown command.\nPress 'm' to get back.";
		}
		if(menu instanceof FunctionalMenu){
			return ((FunctionalMenu)menu).activate(command, terminal);
		}

		root = menu;
		//add the menu name
		out += EnumChatFormatting.GOLD+" "+EnumChatFormatting.BOLD+"]--"+(menu.getMenuName().toUpperCase())+" ("+menu.getMenuID()+")--[\n\n";
		//add all sub menus to the string.
		int position = 1;
		ArrayList<Menu> list = menu.getChildrenList();
		for(Menu child: list){
			if(child instanceof FunctionalMenu || child instanceof FunctionalParamMenu){
				//out+= "    "+EnumChatFormatting.GREEN+"["+position+"] "+child.getMenuName()+" ("+child.getMenuID()+")"+'\n';
				out+= "    "+EnumChatFormatting.GREEN+"["+position+"] "+child.getMenuName()+'\n';
			}else{
				//out+= EnumChatFormatting.WHITE+"    ["+position+"] "+child.getMenuName()+" ("+child.getMenuID()+")"+'\n';
				out+= EnumChatFormatting.WHITE+"    ["+position+"] "+child.getMenuName()+'\n';
			}
			position++;
		}
		return out;
	}

	/**
	 * Displays the current selected menu.
	 * @param command
	 */
	public void display(final String command, IMenuInterface terminal){
		if(command.trim().isEmpty()){
			terminal.display(EnumChatFormatting.RED+"unknown command.\nPress 'm' to get back.", true);
			return;
		}
		terminal.display(preparingOutput(root.switchMenu(command, terminal),command),true);
	}

	/**
	 * Displays the root menu.
	 * @param menu
	 */
	public void displayMain(final Menu menu, CustomGuiChat terminal){
		if(menu.equals(null)){
			throw new IllegalArgumentException("Menu can not be null.");
		}
		terminal.display(preparingOutput(menu, ""),true);
	}
}
