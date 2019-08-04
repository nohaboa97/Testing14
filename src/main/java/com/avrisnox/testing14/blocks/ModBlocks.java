package com.avrisnox.testing14.blocks;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("testing14:testblock")
    public static TestBlock TEST_BLOCK;

    @ObjectHolder("testing14:testblock")
    public static TileEntityType<TestBlockTile> TEST_BLOCK_TILE;

    @ObjectHolder("testing14:testblock")
    public static ContainerType<TestBlockContainer> TEST_BLOCK_CONTAINER;

    // TODO: Put each new block here
}
