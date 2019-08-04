package com.avrisnox.testing14.blocks;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("testing14:testblock")
    public static TestBlock TEST_BLOCK;

    @ObjectHolder("testing14:testblock")
    public static TileEntityType<TestBlockTile> TEST_BLOCK_TILE;

    // TODO: Put each new block here
}
