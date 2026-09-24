package me.shaweel.transformableitems;

import me.shaweel.transformableitems.functionalinterfaces.Function;
import me.shaweel.transformableitems.functionalinterfaces.Supplier;
import me.shaweel.transformableitems.functionalinterfaces.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ConfigScreen extends GuiScreen {
	public ConfigScreen() {
		super();
	}

	public ConfigScreen(GuiScreen parent) {
		super();
	}
	
	public enum OptionTypes { FLOAT_SLIDER, BOOLEAN_OPTION }
	public static final int DONE_BUTTON_PADDING = 7;
	public static final int TITLE_PADDING = 7;
	public static final int TITLE_TO_TAB_PADDING = 7;
	public static final int RESET_BUTTON_WIDTH = 50;
	public static final int DONE_BUTTON_WIDTH = 200;
	public static final int TAB_WIDTH = 100;
	public static final int TAB_AMOUNT = 2;
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
		int headerSpace = TITLE_PADDING + this.fontRendererObj.FONT_HEIGHT + TITLE_TO_TAB_PADDING + WIDGET_HEIGHT;
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

	private void createButton(final int x, final int y, final int w, final int h, final String name, final Runnable action) {
		this.buttonList.add(new GuiButton(this.currentId, x, y, w, h, name) {
			@Override
			public boolean mousePressed(Minecraft client, int mouseX, int mouseY) {
				if (!super.mousePressed(client, mouseX, mouseY)) return false;
				action.run();
				return true;
			}
		});
		this.currentId++;
	}

	private void createTabButton(final int index, final int x, final int y, final int w, final int h, final String name, final Runnable action) {
		this.buttonList.add(new TabButton(this.currentId, index, x, y, w, h, name) {
			@Override
			public boolean mousePressed(Minecraft client, int mouseX, int mouseY) {
				if (!super.mousePressed(client, mouseX, mouseY)) return false;
				action.run();
				return true;
			}
		});
		this.currentId++;
	}

	private void createSlider(final int x, final int y, final int w, final int h, final String name, final float min, final float max, 
					final Supplier<Float> getter, final Consumer<Float> setter, final Float defaultValue) {
		final Slider slider = new Slider(this.currentId, x, y, w, h, name, normalize(min, max, getter.get()), new Function<Float,String>() {
			public String apply(Float value) {
				float denormalized = Math.round(denormalize(min, max, value) * 100f) / 100f;
			
				setter.accept(denormalized);
				ConfigFile.save();
				return String.format("%.2f", denormalized);
			}
		});
		this.buttonList.add(slider);
		this.currentId++;

		createButton(
			x + w + WIDGET_PADDING, 
			y, 
			RESET_BUTTON_WIDTH, 
			h, 
			"Reset", 
			new Runnable() {
				public void run() {
					slider.changeValue(normalize(min, max, defaultValue));
				}	
			}
		);
	}

	private String getBooleanText(final String name, final boolean value) {
		return String.format("%s: %s", name, value ? "ON" : "OFF");
	}

	private void createBooleanOption(final int x, final int y, final int w, final int h, final String name, 
						final Supplier<Boolean> getter, final Consumer<Boolean> setter, final Boolean defaultValue) {
		final GuiButton booleanOption = new GuiButton(this.currentId, x, y, w, h, getBooleanText(name, getter.get())) {
			@Override
			public boolean mousePressed(Minecraft client, int mouseX, int mouseY) {
				if (!super.mousePressed(client, mouseX, mouseY)) return false;

				boolean newValue = !getter.get();
				setter.accept(newValue);

				this.displayString = getBooleanText(name, newValue);

				ConfigFile.save();
				return true;
			}
		};
		this.buttonList.add(booleanOption);
		this.currentId++;

		createButton(
			x + w + WIDGET_PADDING, 
			y, 
			RESET_BUTTON_WIDTH, 
			h, 
			"Reset", 
			new Runnable() {
				public void run() {
					setter.accept(defaultValue);
					ConfigFile.save();
					booleanOption.displayString = getBooleanText(name, defaultValue);
				}	
			}
		);
	}

	private void createText(final int x, final int y, final String text) {
		drawString(this.fontRendererObj, text, x, y, 0xFFFFFF);
	}

	private void switchTab(int index) {
		currentTab = index;

		this.buttonList.clear();
		initGui();
	}

	private void createTab(final int index, final String name) {
		createTabButton(
			index,
			categoryX(index),
			TITLE_TO_TAB_PADDING + TITLE_PADDING + this.fontRendererObj.FONT_HEIGHT,
			TAB_WIDTH,
			WIDGET_HEIGHT,
			name,
			new Runnable() {
				public void run() {
					switchTab(index);
				}	
			}
		);
	}

	private void initNormalTab() {
		createSlider(
			x(), 
			row(0, 7), 
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"X Scale", 
			MIN_SCALE, 
			MAX_SCALE, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.normalConfig.xScale;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.normalConfig.xScale = value;
				}
			}, 
			DEFAULT_SCALE
		);

		createSlider(
			x(), 
			row(1, 7), 
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Y Scale", 
			MIN_SCALE, 
			MAX_SCALE, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.normalConfig.yScale;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.normalConfig.yScale = value;
				}
			}, 
			DEFAULT_SCALE
		);

		createSlider(
			x(), 
			row(2, 7), 
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Z Scale", 
			MIN_SCALE, 
			MAX_SCALE, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.normalConfig.zScale;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.normalConfig.zScale = value;
				}
			}, 
			DEFAULT_SCALE
		);


		createSlider(
			x(), 
			row(3, 7), 
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"X Offset", 
			MIN_OFFSET, 
			MAX_OFFSET, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.normalConfig.xOffset;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.normalConfig.xOffset = value;
				}
			}, 
			DEFAULT_OFFSET
		);

		createSlider(
			x(), 
			row(4, 7), 
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Y Offset", 
			MIN_OFFSET, 
			MAX_OFFSET, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.normalConfig.yOffset;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.normalConfig.yOffset = value;
				}
			}, 
			DEFAULT_OFFSET
		);

		createSlider(
			x(), 
			row(5, 7), 
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Z Offset", 
			MIN_OFFSET, 
			MAX_OFFSET, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.normalConfig.zOffset;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.normalConfig.zOffset = value;
				}
			}, 
			DEFAULT_OFFSET
		);

		createBooleanOption(
			x(),
			row(6, 7),
			WIDGET_WIDTH,
			WIDGET_HEIGHT,
			"Item Height Animations",
			new Supplier<Boolean>() {
				public Boolean get() {
					return ConfigFile.configData.normalConfig.itemHeightAnimations;
				}
			},
			new Consumer<Boolean>() {
				public void accept(Boolean value) {
					ConfigFile.configData.normalConfig.itemHeightAnimations = value;
				}
			},
			true
		);
	}

	private void initFoodTab() {
		createSlider(
			x(), 
			row(0, 6),
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"X Scale", 
			MIN_SCALE, 
			MAX_SCALE, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.foodConfig.xScale;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.foodConfig.xScale = value;
				}
			}, 
			DEFAULT_SCALE
		);

		createSlider(
			x(), 
			row(1, 6),
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Y Scale", 
			MIN_SCALE, 
			MAX_SCALE, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.foodConfig.yScale;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.foodConfig.yScale = value;
				}
			}, 
			DEFAULT_SCALE
		);

		createSlider(
			x(), 
			row(2, 6),
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Z Scale", 
			MIN_SCALE, 
			MAX_SCALE, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.foodConfig.zScale;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.foodConfig.zScale = value;
				}
			}, 
			DEFAULT_SCALE
		);


		createSlider(
			x(), 
			row(3, 6),
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"X Offset", 
			MIN_OFFSET, 
			MAX_OFFSET, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.foodConfig.xOffset;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.foodConfig.xOffset = value;
				}
			}, 
			DEFAULT_OFFSET
		);

		createSlider(
			x(), 
			row(4, 6),
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Y Offset", 
			MIN_OFFSET, 
			MAX_OFFSET, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.foodConfig.yOffset;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.foodConfig.yOffset = value;
				}
			}, 
			DEFAULT_OFFSET
		);

		createSlider(
			x(), 
			row(5, 6),
			WIDGET_WIDTH, 
			WIDGET_HEIGHT, 
			"Z Offset", 
			MIN_OFFSET, 
			MAX_OFFSET, 
			new Supplier<Float>() {
				public Float get() {
					return ConfigFile.configData.foodConfig.zOffset;
				}
			}, 
			new Consumer<Float>() {
				public void accept(Float value) {
					ConfigFile.configData.foodConfig.zOffset = value;
				}
			}, 
			DEFAULT_OFFSET
		);
	}

	private void initTab() {
		if (currentTab == 0) {
			initNormalTab();
		} else if (currentTab == 1) {
			initFoodTab();
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		createTab(0, "Normal");
		createTab(1, "Eating Animation");

		initTab();

		final ConfigScreen self = this;

		createButton(
			x(DONE_BUTTON_WIDTH), 
			this.height - DONE_BUTTON_PADDING - WIDGET_HEIGHT, 
			DONE_BUTTON_WIDTH, 
			WIDGET_HEIGHT, 
			"Done",
			new Runnable() {
				public void run() {
					self.mc.displayGuiScreen(null);
				}
			}
		);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();

		createText(x(this.fontRendererObj.getStringWidth("Transformable Items Configuration")), TITLE_PADDING, "Transformable Items Configuration");

		super.drawScreen(mouseX, mouseY, partialTick);
	}
}
