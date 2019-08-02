package com.avrisnox.testing14.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class TestBlock extends Block {
    public TestBlock() {
        super(Properties.create(Material.WOOD)
                .sound(SoundType.WOOD)
                .hardnessAndResistance(2.0f)
                .lightValue(14)
        );
        setRegistryName("testblock");
    }
}
