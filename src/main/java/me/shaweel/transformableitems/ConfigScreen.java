package me.shaweel.transformableitems;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ConfigScreen extends Screen {
	public enum OptionTypes { FLOAT_SLIDER, BOOLEAN_OPTION }
	public static final int DONE_BUTTON_PADDING = 7;
	public static final int TITLE_PADDING = 12;
	public static final int TITLE_TO_TAB_PADDING = 12;
	public static final int RESET_BUTTON_WIDTH = 50;
	public static final int DONE_BUTTON_WIDTH = 200;
	public static final int TAB_WIDTH = 100;
	public static final int TAB_AMOUNT = 3;
	public static final int WIDGET_WIDTH = 208;
	public static final int WIDGET_HEIGHT = 20;
	public static final int WIDGET_PADDING = 4;

	public static final double MIN_SCALE = 0;
	public static final double MAX_SCALE = 2;
	public static final double MIN_OFFSET = -1;
	public static final double MAX_OFFSET = 1;

	public static final float DEFAULT_SCALE = 1f;
	public static final float DEFAULT_OFFSET = 0f;

	public int currentTab = 0;

	public ConfigScreen() {
		super(Component.literal("ConfigScreen"));
	}

	private int row(int index, int rowAmount) {
		int headerSpace = TITLE_PADDING + this.font.lineHeight;
		int footerSpace = DONE_BUTTON_PADDING + WIDGET_HEIGHT;

		int allWidgetsHeight = rowAmount * WIDGET_HEIGHT + (rowAmount - 1) * WIDGET_PADDING;

		int usableHeight = this.height - headerSpace - footerSpace;
		int startY = headerSpace + (usableHeight - allWidgetsHeight) / 2;

		return startY + (WIDGET_HEIGHT + WIDGET_PADDING) * index;
	}
	
	private int categoryX(int index) {
		float left = this.width / 2 - (WIDGET_PADDING + TAB_WIDTH) * ((float) TAB_AMOUNT / 2);
		return (int) left + (WIDGET_PADDING + TAB_WIDTH) * index;
	}

	private int x() {
		return x(WIDGET_WIDTH + WIDGET_PADDING + RESET_BUTTON_WIDTH);
	}

	private int x(int w) {
		return this.width / 2 - w / 2;
	}

	private float denormalize(double min, double max, double x) {
		return (float)(min + x * (max - min));
	}

	private double normalize(double min, double max, float x) {
		return (double)(x - min) / (max - min);
	}

	private void createButton(int x, int y, int w, int h, String name, Runnable action) {
		this.addRenderableWidget(Button.builder(Component.literal(name), button -> action.run()).bounds(x, y, w, h).build());
	}

	private void createTabButton(int index, int x, int y, int w, int h, String name, Runnable action) {
		this.addRenderableWidget(new TabButton(index, x, y, w, h, Component.literal(name), button -> action.run()));
	}

	private void createSlider(int x, int y, int w, int h, String name, double min, double max, Supplier<Float> getter, Consumer<Float> setter, Float defaultValue) {
		this.addRenderableWidget(new AbstractSliderButton(x, y, w, h, Component.literal(name), normalize(min, max, getter.get())) {
			{
				updateMessage();
				createButton(x + w + WIDGET_PADDING, y, RESET_BUTTON_WIDTH, h, "Reset", () -> {
					this.resetValue();
				});
			}
			
			public void resetValue() {
				double oldValue = this.value;
				double newValue = (defaultValue - min) / (max - min);
				this.value = Mth.clamp(newValue, (double)0.0F, (double)1.0F);
				if (oldValue != this.value) {
					this.applyValue();
				}

				this.updateMessage();
			}

			@Override
			protected void updateMessage() {
			setMessage(Component.literal(
				String.format("%s: %.2f", name, denormalize(min, max, this.value))
			));
			}

			@Override
			protected void applyValue() {
				setter.accept(Math.round(denormalize(min, max, this.value) * 100f) / 100f);
				ConfigFile.save();
			}
		});
	}

	private void createBooleanOption(int x, int y, int w, int h, String name, Supplier<Boolean> getter, Consumer<Boolean> setter, Boolean defaultValue) {
		CycleButton<Boolean> booleanOption = this.addRenderableWidget(CycleButton.onOffBuilder(getter.get()).create(
			x, y, w, h, Component.literal(name), (button, value) -> {
				setter.accept(value);
				ConfigFile.save();
			}
		));

		createButton(x + w + WIDGET_PADDING, y, RESET_BUTTON_WIDTH, h, "Reset", () -> {
			booleanOption.setValue(defaultValue);
		});
	}

	private void createText(int x, int y, String text) {
		StringWidget stringWidget = new StringWidget(Component.literal(text), this.font);
		stringWidget.setX(x); stringWidget.setY(y);

		this.addRenderableWidget(stringWidget);
	}

	private void switchTab(int index) {
		currentTab = index;

		clearWidgets();
		init();
	}

	private void createTab(int index, String name) {
		createTabButton(index, categoryX(index), TITLE_TO_TAB_PADDING + TITLE_PADDING + this.font.lineHeight, TAB_WIDTH, WIDGET_HEIGHT, name, () -> {
			switchTab(index);
		});
	}

	private void initTab() {
		createSlider(x(), row(0, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "X Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.get(currentTab).xScale, value -> ConfigFile.configData.get(currentTab).xScale = value, DEFAULT_SCALE);

		createSlider(x(), row(1, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.get(currentTab).yScale, value -> ConfigFile.configData.get(currentTab).yScale = value, DEFAULT_SCALE);

		createSlider(x(), row(2, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.get(currentTab).zScale, value -> ConfigFile.configData.get(currentTab).zScale = value, DEFAULT_SCALE);


		createSlider(x(), row(3, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "X Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.get(currentTab).xOffset, value -> ConfigFile.configData.get(currentTab).xOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(4, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.get(currentTab).yOffset, value -> ConfigFile.configData.get(currentTab).yOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(5, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.get(currentTab).zOffset, value -> ConfigFile.configData.get(currentTab).zOffset = value, DEFAULT_OFFSET);

		if (currentTab == 0) {
			createBooleanOption(x(), row(6, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Item Height Animations",
			() -> ConfigFile.configData.itemHeightAnimations, value -> ConfigFile.configData.itemHeightAnimations = value, true);
		}
	}

	@Override
	protected void init() {
		super.init();

		createText(x(this.font.width("Transformable Items Configuration")), TITLE_PADDING, "Transformable Items Configuration");

		createTab(0, "Normal");
		createTab(1, "Eating Animation");
		createTab(2, "Totem Animation");

		initTab();

		createButton(x(DONE_BUTTON_WIDTH), this.height - DONE_BUTTON_PADDING - WIDGET_HEIGHT, DONE_BUTTON_WIDTH, WIDGET_HEIGHT, "Done", this::onClose);
	}
}
