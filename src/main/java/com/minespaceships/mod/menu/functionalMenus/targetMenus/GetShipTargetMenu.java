package com.minespaceships.mod.menu.functionalMenus.targetMenus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.minespaceships.mod.menu.FunctionalMenu;
import com.minespaceships.mod.menu.Menu;
import com.minespaceships.mod.menu.functionalMenus.energyMenus.TerminalUtil;
import com.minespaceships.mod.overhead.IMenuInterface;
import com.minespaceships.mod.spaceship.Shipyard;
import com.minespaceships.mod.spaceship.Spaceship;
import com.minespaceships.mod.target.EntityTarget;
import com.minespaceships.mod.target.ITargetHolder;
import com.minespaceships.mod.target.PositionTarget;
import com.minespaceships.mod.target.SpaceshipTarget;
import com.minespaceships.mod.target.Target;

public class GetShipTargetMenu extends Menu {

	public GetShipTargetMenu() {
		super("Target Spaceship");
	}

	@Override
	public Menu getMenu(IMenuInterface terminal) {
		GetShipTargetMenu list = new GetShipTargetMenu();
		World world = terminal.getChatRegisterEntity().getWorld();
		List<Spaceship> ships = Shipyard.getShipyard(world).getShipList();
		ArrayList<SpaceshipTarget> targets = new ArrayList<SpaceshipTarget>();
		Spaceship ship = Shipyard.getShipyard(world).getShip(terminal.getChatRegisterEntity().getPos(), world);
		for(Spaceship s : ships){
			if(s != ship){
				targets.add(new SpaceshipTarget(s.getOrigin(), s));
			}
		}
		SpaceshipTarget pos = new SpaceshipTarget(terminal.getChatRegisterEntity().getPos(), ship);
		Collections.sort(targets, new Target.distanceComparator(pos, terminal.getChatRegisterEntity().getWorld()));
		for(int i = 0; i < 10 && i < targets.size() &&  !targets.isEmpty(); i++){
			list.addSubMenu(new SpaceshipTargetMenu(targets.get(i), terminal.getChatRegisterEntity().getWorld()));
		}
		return list;
	}
}
