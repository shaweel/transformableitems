package me.shaweel.transformableitems;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ConfigScreen extends GuiScreen {
	public enum OptionTypes { FLOAT_SLIDER, BOOLEAN_OPTION }
	public static final int DONE_BUTTON_PADDING = 7;
	public static final int TITLE_PADDING = 7;
	public static final int TITLE_TO_TAB_PADDING = 7;
	public static final int RESET_BUTTON_WIDTH = 50;
	public static final int DONE_BUTTON_WIDTH = 200;
	public static final int TAB_WIDTH = 100;
	public static final int TAB_AMOUNT = 3;
	public static final int WIDGET_WIDTH = 208;
	public static final int WIDGET_HEIGHT = 20;
	public static final int WIDGET_PADDING = 4;

	public static final float MIN_SCALE = 0;
	public static final float MAX_SCALE = 2;
	public static final float MIN_OFFSET = -1;
	public static final float MAX_OFFSET = 1;

	public static final float DEFAULT_SCALE = 1f;
	public static final float DEFAULT_OFFSET = 0f;

	public int currentTab = 0;
	private int currentId = 0;

	private int row(int index, int rowAmount) {
		int headerSpace = TITLE_PADDING + this.fontRenderer.FONT_HEIGHT + TITLE_TO_TAB_PADDING + WIDGET_HEIGHT;
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

	private float denormalize(float min, float max, float x) {
		return (float)(min + x * (max - min));
	}

	private float normalize(float min, float max, float x) {
		return (x - min) / (max - min);
	}

	private void createButton(int x, int y, int w, int h, String name, Runnable action) {
		this.addButton(new GuiButton(this.currentId, x, y, w, h, name) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				action.run();
			}
		});
		this.currentId++;
	}

	private void createTabButton(int index, int x, int y, int w, int h, String name, Runnable action) {
		this.addButton(new TabButton(this.currentId, index, x, y, w, h, name) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				action.run();
			}
		});
		this.currentId++;
	}

	private void createSlider(int x, int y, int w, int h, String name, float min, float max, Supplier<Float> getter, Consumer<Float> setter, Float defaultValue) {
		Slider slider = this.addButton(new Slider(this.currentId, x, y, w, h, name, normalize(min, max, getter.get()), value -> {
			float denormalized = Math.round(denormalize(min, max, value) * 100f) / 100f;
			
			setter.accept(denormalized);
			ConfigFile.save();
			return String.format("%.2f", denormalized);
		}));
		this.currentId++;

		createButton(x + w + WIDGET_PADDING, y, RESET_BUTTON_WIDTH, h, "Reset", () -> {
			slider.changeValue(normalize(min, max, defaultValue));
		});
	}

	private String getBooleanText(String name, boolean value) {
		return String.format("%s: %s", name, value ? "ON" : "OFF");
	}

	private void createBooleanOption(int x, int y, int w, int h, String name, Supplier<Boolean> getter, Consumer<Boolean> setter, Boolean defaultValue) {
		GuiButton booleanOption = this.addButton(new GuiButton(this.currentId, x, y, w, h, getBooleanText(name, getter.get())) {
			@Override
			public void onClick(double mouseX, double mouseY) {
				boolean newValue = !getter.get();
				setter.accept(newValue);

				this.displayString = getBooleanText(name, newValue);

				ConfigFile.save();
			}
		});
		this.currentId++;

		createButton(x + w + WIDGET_PADDING, y, RESET_BUTTON_WIDTH, h, "Reset", () -> {
			setter.accept(defaultValue);
			ConfigFile.save();
			booleanOption.displayString = getBooleanText(name, defaultValue);
		});
	}

	private void createText(int x, int y, String text) {
		drawString(this.fontRenderer, text, x, y, 0xFFFFFF);
	}

	private void switchTab(int index) {
		currentTab = index;

		this.buttons.clear();
		this.children.clear();
		initGui();
	}

	private void createTab(int index, String name) {
		createTabButton(index, categoryX(index), TITLE_TO_TAB_PADDING + TITLE_PADDING + this.fontRenderer.FONT_HEIGHT, TAB_WIDTH, WIDGET_HEIGHT, name, () -> {
			switchTab(index);
		});
	}

	private void initNormalTab() {
		createSlider(x(), row(0, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "X Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.normalConfig.xScale, value -> ConfigFile.configData.normalConfig.xScale = value, DEFAULT_SCALE);

		createSlider(x(), row(1, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.normalConfig.yScale, value -> ConfigFile.configData.normalConfig.yScale = value, DEFAULT_SCALE);

		createSlider(x(), row(2, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.normalConfig.zScale, value -> ConfigFile.configData.normalConfig.zScale = value, DEFAULT_SCALE);


		createSlider(x(), row(3, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "X Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.normalConfig.xOffset, value -> ConfigFile.configData.normalConfig.xOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(4, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.normalConfig.yOffset, value -> ConfigFile.configData.normalConfig.yOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(5, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.normalConfig.zOffset, value -> ConfigFile.configData.normalConfig.zOffset = value, DEFAULT_OFFSET);

		createBooleanOption(x(), row(6, 7), WIDGET_WIDTH, WIDGET_HEIGHT, "Item Height Animations",
		() -> ConfigFile.configData.normalConfig.itemHeightAnimations, value -> ConfigFile.configData.normalConfig.itemHeightAnimations = value, true);
	}

	private void initFoodTab() {
		createSlider(x(), row(0, 6), WIDGET_WIDTH, WIDGET_HEIGHT, "X Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.foodConfig.xScale, value -> ConfigFile.configData.foodConfig.xScale = value, DEFAULT_SCALE);

		createSlider(x(), row(1, 6), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.foodConfig.yScale, value -> ConfigFile.configData.foodConfig.yScale = value, DEFAULT_SCALE);

		createSlider(x(), row(2, 6), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.foodConfig.zScale, value -> ConfigFile.configData.foodConfig.zScale = value, DEFAULT_SCALE);


		createSlider(x(), row(3, 6), WIDGET_WIDTH, WIDGET_HEIGHT, "X Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.foodConfig.xOffset, value -> ConfigFile.configData.foodConfig.xOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(4, 6), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.foodConfig.yOffset, value -> ConfigFile.configData.foodConfig.yOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(5, 6), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.foodConfig.zOffset, value -> ConfigFile.configData.foodConfig.zOffset = value, DEFAULT_OFFSET);
	}

	private void initTotemTab() {
		createSlider(x(), row(0, 5), WIDGET_WIDTH, WIDGET_HEIGHT, "X Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.totemConfig.xScale, value -> ConfigFile.configData.totemConfig.xScale = value, DEFAULT_SCALE);

		createSlider(x(), row(1, 5), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.totemConfig.yScale, value -> ConfigFile.configData.totemConfig.yScale = value, DEFAULT_SCALE);

		createSlider(x(), row(2, 5), WIDGET_WIDTH, WIDGET_HEIGHT, "Z Scale", MIN_SCALE, MAX_SCALE, 
		() -> ConfigFile.configData.totemConfig.zScale, value -> ConfigFile.configData.totemConfig.zScale = value, DEFAULT_SCALE);


		createSlider(x(), row(3, 5), WIDGET_WIDTH, WIDGET_HEIGHT, "X Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.totemConfig.xOffset, value -> ConfigFile.configData.totemConfig.xOffset = value, DEFAULT_OFFSET);

		createSlider(x(), row(4, 5), WIDGET_WIDTH, WIDGET_HEIGHT, "Y Offset", MIN_OFFSET, MAX_OFFSET,
		() -> ConfigFile.configData.totemConfig.yOffset, value -> ConfigFile.configData.totemConfig.yOffset = value, DEFAULT_OFFSET);
	}

	private void initTab() {
		if (currentTab == 0) {
			initNormalTab();
		} else if (currentTab == 1) {
			initFoodTab();
		} else if (currentTab == 2) {
			initTotemTab();
		}
	}

	@Override
	protected void initGui() {
		super.initGui();

		createTab(0, "Normal");
		createTab(1, "Eating Animation");
		createTab(2, "Totem Animation");

		initTab();

		createButton(x(DONE_BUTTON_WIDTH), this.height - DONE_BUTTON_PADDING - WIDGET_HEIGHT, DONE_BUTTON_WIDTH, WIDGET_HEIGHT, "Done", this::close);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();

		createText(x(this.fontRenderer.getStringWidth("Transformable Items Configuration")), TITLE_PADDING, "Transformable Items Configuration");

		super.render(mouseX, mouseY, partialTick);
	}
}
