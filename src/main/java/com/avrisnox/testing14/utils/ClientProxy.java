package com.avrisnox.testing14.utils;

import com.avrisnox.testing14.blocks.ModBlocks;
import com.avrisnox.testing14.blocks.TestBlock;
import com.avrisnox.testing14.blocks.TestBlockScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {
	@Override
	public void init() {
		ScreenManager.registerFactory(ModBlocks.TEST_BLOCK_CONTAINER, TestBlockScreen::new);
	}

	@Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

	@Override
	public PlayerEntity getClientPlayer() {
		return Minecraft.getInstance().player;
	}
}
