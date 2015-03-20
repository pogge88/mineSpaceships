package com.minespaceships.mod;

import io.netty.buffer.ByteBuf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.minespaceships.mod.overhead.ChatRegisterEntity;
import com.minespaceships.mod.spaceship.Shipyard;

public class ShipSyncHandlerClient implements IMessageHandler<CommandMessage, IMessage>  {
	
    @Override
    public IMessage onMessage(CommandMessage message, MessageContext ctx) {
    	Side side = FMLCommonHandler.instance().getEffectiveSide();
    	Shipyard.getShipyard().load(message.getText(), Minecraft.getMinecraft().theWorld);    	
        return null;
    }
}