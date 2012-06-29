package org.delaunay.dtfe;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.Lists;

public class ColorScales {

	public static final ColorScale MODIFIED_RAINBOW = new ColorScale()
			.stopAlpha(Color.HSBtoRGB(0.0f, 0.6f, 1.0f), 0x00, 0.1)
			.stopAlpha(Color.HSBtoRGB(1.0f, 0.8f, 1.0f), 0x20, 0.4)
			.stopAlpha(Color.HSBtoRGB(0.8f, 0.9f, 1.0f), 0x40, 0.5)
			.stopAlpha(Color.HSBtoRGB(0.6f, 1.0f, 1.0f), 0x60, 0.6)
			.stopAlpha(Color.HSBtoRGB(0.4f, 1.0f, 1.0f), 0x80, 0.7)
			.stopAlpha(Color.HSBtoRGB(0.2f, 1.0f, 1.0f), 0xA0, 0.8)
			.stopAlpha(0xFF0000, 0xB0, 0.801)
			.stopAlpha(0xFF00FF, 0xB0, 1.0);

	public static final ColorScale LINEAR_RAINBOW = new ColorScale()
			.stopAlpha(Color.HSBtoRGB(0.0f, 1.0f, 1.0f), 0x00, 0.0)
			.stopAlpha(Color.HSBtoRGB(1.0f, 1.0f, 1.0f), 0x80, 0.2)
			.stopAlpha(Color.HSBtoRGB(0.8f, 1.0f, 1.0f), 0xFF, 0.4)
			.stopAlpha(Color.HSBtoRGB(0.6f, 1.0f, 1.0f), 0xFF, 0.6)
			.stopAlpha(Color.HSBtoRGB(0.4f, 1.0f, 1.0f), 0xFF, 0.8)
			.stopAlpha(Color.HSBtoRGB(0.2f, 1.0f, 1.0f), 0xFF, 1.0);
	
	public static final ColorScale LINEAR_RAINBOW_NO_ALPHA = new ColorScale()
			.stopAlpha(Color.HSBtoRGB(0.0f, 1.0f, 1.0f), 0xFF, 0.0)
			.stopAlpha(Color.HSBtoRGB(1.0f, 1.0f, 1.0f), 0xFF, 0.2)
			.stopAlpha(Color.HSBtoRGB(0.8f, 1.0f, 1.0f), 0xFF, 0.4)
			.stopAlpha(Color.HSBtoRGB(0.6f, 1.0f, 1.0f), 0xFF, 0.6)
			.stopAlpha(Color.HSBtoRGB(0.4f, 1.0f, 1.0f), 0xFF, 0.8)
			.stopAlpha(Color.HSBtoRGB(0.2f, 1.0f, 1.0f), 0xFF, 1.0);

	// http://en.wikipedia.org/wiki/Color_temperature
	public static final ColorScale TEMPURATURE = new ColorScale()
			.stopAlpha(0xFF3C00, 0x00, 0.0)
			.stopAlpha(0xFF7400, 0x40, 0.2)
			.stopAlpha(0xFFAE2E, 0x80, 0.5)
			.stopAlpha(0xC3C0C2, 0xE0, 0.8)
			.stopAlpha(0x96B1EB, 0xFF, 0.9)
			.stopAlpha(0x779EFF, 0xFF, 1.0);
	
	public static final ColorScale BLUE_TO_YELLOW = new ColorScale()
			.stopAlpha(0x0000FF, 0x00, 0.0)
			.stopAlpha(0x0000FF, 0x80, 0.2)
			.stopAlpha(0xFFFF00, 0xFF, 1.0);

	public static final ColorScale PURPLE_TO_GREEN = new ColorScale()
			.stopAlpha(0x260404, 0x00, 0.2)
			.stopAlpha(0x260404, 0x80, 0.4)
			.stopAlpha(0x6B3BAB, 0xC0, 0.6)
			.stopAlpha(0x4CFFB6, 0xFF, 0.9);
	
	public static final ColorScale PURPLE_TO_GREEN_LINEAR = new ColorScale()
			.stopAlpha(0x260404, 0x00, 0.0)
			.stopAlpha(0x260404, 0x80, 0.2)
			.stopAlpha(0x6B3BAB, 0xC0, 0.4)
			.stopAlpha(0x4CFFB6, 0xFF, 1.0);

	public static final ColorScale RED_TO_BLUE_POS_NEG = new ColorScale()
			.stopAlpha(0xFF0000, 0xFF, -1.0)
			.stopAlpha(0xFFFF00, 0x60, -0.5)
			.stopAlpha(0xFFFFFF, 0x00, 0.0)
			.stopAlpha(0x00FFFF, 0x60, 0.5)
			.stopAlpha(0x0000FF, 0xFF, 1.0);
	
	public static final ColorScale RED_TO_BLUE = new ColorScale()
			.stopAlpha(0xFF0000, 0xFF, 0.0)
			.stopAlpha(0xFFFF00, 0x60, 0.25)
			.stopAlpha(0xFFFFFF, 0x00, 0.5)
			.stopAlpha(0x00FFFF, 0x60, 0.75)
			.stopAlpha(0x0000FF, 0xFF, 1.0);

	public static List<ColorScale> getDefaultColorScales() {
		return Lists.newArrayList(
				MODIFIED_RAINBOW,
				LINEAR_RAINBOW,
				TEMPURATURE,
				PURPLE_TO_GREEN,
				RED_TO_BLUE_POS_NEG,
				BLUE_TO_YELLOW);
	}
	
}
