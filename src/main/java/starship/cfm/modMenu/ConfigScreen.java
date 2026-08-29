package starship.cfm.modMenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import starship.cfm.CompactFishingMessage;

import java.util.List;


public class ConfigScreen {

    public static Screen buildScreen(CompactFishingMessage cfm, Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.cfm.config"));

        ConfigCategory category = builder.getOrCreateCategory(Component.translatable("category.cfm.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigData config = cfm.getConfig();

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.cfm.compact_fishmsg"), config.enableCompactFishmsg)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableCompactFishmsg = newValue)
                .setTooltip(Component.translatable("tooltip.cfm.compact_fishmsg"))
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.cfm.trevor_opener"), config.enableTreasureReciMsg)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> config.enableTreasureReciMsg = newValue)
                .setTooltip(Component.translatable("tooltip.cfm.trevor_opener"))
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.cfm.record_overlay"), config.enableFishRecordOverlay)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableFishRecordOverlay = newValue)
                .setTooltip(Component.translatable("tooltip.cfm.record_overlay"))
                .build());

        Window window = Minecraft.getInstance().getWindow();
        int scaledWidth = window.getGuiScaledWidth();
        int scaledHeight = window.getGuiScaledHeight();

        category.addEntry(entryBuilder.startSubCategory(
                Component.translatable("group.cfm.render_settings"),
                List.of(
                        entryBuilder.startIntSlider(Component.translatable("option.cfm.render_x"), config.fishRecordRenderTextX, 0, scaledWidth)
                                .setDefaultValue(10)
                                .setSaveConsumer(newValue -> config.fishRecordRenderTextX = newValue)
                                .build(),
                        entryBuilder.startIntSlider(Component.translatable("option.cfm.render_y"), config.fishRecordRenderTextY, 0, scaledHeight)
                                .setDefaultValue(10)
                                .setSaveConsumer(newValue -> config.fishRecordRenderTextY = newValue)
                                .build(),
                        entryBuilder.startFloatField(Component.translatable("option.cfm.font_size"), config.fishRecordRenderScale)
                                .setDefaultValue(1.0f)
                                .setSaveConsumer(newValue -> config.fishRecordRenderScale = newValue)
                                .build(),
                        entryBuilder.startIntSlider(Component.translatable("option.cfm.bg_color"), config.fishRecordBackgroundAlphaColor, 0, 255)
                                .setDefaultValue(0x88)
                                .setSaveConsumer(newValue -> config.fishRecordBackgroundAlphaColor = newValue)
                                .build(),
                        entryBuilder.startColorField(Component.translatable("option.cfm.text_color"), config.fishRecordTextRGBColor)
                                .setDefaultValue(0xFFFFFF)
                                .setSaveConsumer(newValue -> config.fishRecordTextRGBColor = newValue)
                                .build(),
                        entryBuilder.startBooleanToggle(Component.translatable("option.cfm.cute_icon"), config.fishRecordIconShows)
                                .setDefaultValue(true)
                                .setSaveConsumer(newValue -> config.fishRecordIconShows = newValue)
                                .build(),
                        entryBuilder.startBooleanToggle(Component.translatable("option.cfm.always_shows"), config.fishRecordOverlayAlwaysShows)
                                .setDefaultValue(false)
                                .setSaveConsumer(newValue -> config.fishRecordOverlayAlwaysShows = newValue)
                                .build(),
                        entryBuilder.startEnumSelector(Component.translatable("option.cfm.show_earn_rate"),
                                        ConfigData.EarnRateMode.class, config.fishRecordEarnRateMode)
                                .setDefaultValue(ConfigData.EarnRateMode.OFF)
                                .setSaveConsumer(newValue -> config.fishRecordEarnRateMode = newValue)
                                .setEnumNameProvider(value -> Component.translatable(
                                        "option.cfm.earn_rate_mode." + ((ConfigData.EarnRateMode) value).name().toLowerCase()))
                                .setTooltip(Component.translatable("tooltip.cfm.show_earn_rate"))
                                .build(),
                        entryBuilder.startEnumSelector(Component.translatable("option.cfm.update_interval"),
                                        ConfigData.UpdateInterval.class, config.fishRecordUpdateInterval)
                                .setDefaultValue(ConfigData.UpdateInterval.ONE_SECOND)
                                .setSaveConsumer(newValue -> config.fishRecordUpdateInterval = newValue)
                                .setEnumNameProvider(value -> Component.translatable(
                                        "option.cfm.update_interval." + ((ConfigData.UpdateInterval) value).name().toLowerCase()))
                                .setTooltip(Component.translatable("tooltip.cfm.update_interval"))
                                .build()
                )).build());

        category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.cfm.augment_overlay"), config.enableAugmentOverlay)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableAugmentOverlay = newValue)
                .setTooltip(Component.translatable("tooltip.cfm.augment_overlay"))
                .build());

        ConfigCategory wallCategory = builder.getOrCreateCategory(Component.translatable("category.cfm.hole_in_the_wall"));

        wallCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("option.cfm.wall_record_overlay"), config.enableWallRecordOverlay)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> config.enableWallRecordOverlay = newValue)
                .setTooltip(Component.translatable("tooltip.cfm.wall_record_overlay"))
                .build());

        wallCategory.addEntry(entryBuilder.startSubCategory(
                Component.translatable("group.cfm.render_settings"),
                List.of(
                        entryBuilder.startIntSlider(Component.translatable("option.cfm.render_x"), config.wallRecordRenderTextX, 0, scaledWidth)
                                .setDefaultValue(10)
                                .setSaveConsumer(newValue -> config.wallRecordRenderTextX = newValue)
                                .build(),
                        entryBuilder.startIntSlider(Component.translatable("option.cfm.render_y"), config.wallRecordRenderTextY, 0, scaledHeight)
                                .setDefaultValue(100)
                                .setSaveConsumer(newValue -> config.wallRecordRenderTextY = newValue)
                                .build(),
                        entryBuilder.startFloatField(Component.translatable("option.cfm.font_size"), config.wallRecordRenderScale)
                                .setDefaultValue(1.0f)
                                .setSaveConsumer(newValue -> config.wallRecordRenderScale = newValue)
                                .build(),
                        entryBuilder.startIntSlider(Component.translatable("option.cfm.bg_color"), config.wallRecordBackgroundAlphaColor, 0, 255)
                                .setDefaultValue(0x88)
                                .setSaveConsumer(newValue -> config.wallRecordBackgroundAlphaColor = newValue)
                                .build(),
                        entryBuilder.startColorField(Component.translatable("option.cfm.text_color"), config.wallRecordTextRGBColor)
                                .setDefaultValue(0xFFFFFF)
                                .setSaveConsumer(newValue -> config.wallRecordTextRGBColor = newValue)
                                .build(),
                        entryBuilder.startBooleanToggle(Component.translatable("option.cfm.always_shows"), config.wallRecordOverlayAlwaysShows)
                                .setDefaultValue(false)
                                .setSaveConsumer(newValue -> config.wallRecordOverlayAlwaysShows = newValue)
                                .build()
                )).build());

        builder.setSavingRunnable(cfm::saveConfig);

        return builder.build();
    }

}
