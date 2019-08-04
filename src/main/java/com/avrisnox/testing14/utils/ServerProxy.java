package com.avrisnox.testing14.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

	@Override
	public void init() {

	}

	@Override
    public World getClientWorld() {
        throw new IllegalStateException("This cannot be run on the server");
    }

	@Override
	public PlayerEntity getClientPlayer() {
		throw new IllegalStateException("This cannot be run on the server");
	}
}
