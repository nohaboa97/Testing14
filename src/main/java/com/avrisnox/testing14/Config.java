package com.avrisnox.testing14;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_POWER = "power";
	public static final String CATEGORY_TEST_BLOCK = "testblock";

	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec CLIENT_CONFIG;

	public static ForgeConfigSpec.IntValue TEST_BLOCK_MAX_POWER;
	public static ForgeConfigSpec.IntValue TEST_BLOCK_GENERATE;
	public static ForgeConfigSpec.IntValue TEST_BLOCK_OUTPUT;
	public static ForgeConfigSpec.IntValue TEST_BLOCK_TICKS;

	static {
		COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.comment("Power settings").push(CATEGORY_POWER);
		setup_TestBlock();
		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

	private static void setup_TestBlock() {
		COMMON_BUILDER.comment("TestBlock settings").push(CATEGORY_TEST_BLOCK);

		TEST_BLOCK_MAX_POWER = COMMON_BUILDER.comment("Maximum power for testblock")
				.defineInRange("maxPower", 100000, 0, Integer.MAX_VALUE);
		TEST_BLOCK_GENERATE = COMMON_BUILDER.comment("Per nether star power for testblock")
				.defineInRange("genPower", 1000, 0, Integer.MAX_VALUE);
		TEST_BLOCK_OUTPUT = COMMON_BUILDER.comment("Maximum power output per tick for testblock")
				.defineInRange("maxOutput", 100, 0, Integer.MAX_VALUE);
		TEST_BLOCK_TICKS = COMMON_BUILDER.comment("Ticks per nether star")
				.defineInRange("ticks", 20, 0, Integer.MAX_VALUE);

		COMMON_BUILDER.pop();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path)
				.sync()
				.autosave()
				.writingMode(WritingMode.REPLACE)
				.build();
		configData.load();
		spec.setConfig(configData);
	}
}
