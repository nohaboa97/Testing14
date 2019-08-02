package com.avrisnox.testing14.utils;

import com.avrisnox.testing14.blocks.ModBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {
	public ItemGroup itemGroup = new ItemGroup("testing14") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.TEST_BLOCK);
		}
	};

	public void init() {

	}
}
