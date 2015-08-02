package com.baselet.element.facet.specific.sequence_aio;

import java.util.HashMap;
import java.util.Map;

import com.baselet.control.basics.Line1D;
import com.baselet.control.basics.geom.PointDouble;
import com.baselet.control.enums.AlignHorizontal;
import com.baselet.control.enums.AlignVertical;
import com.baselet.diagram.draw.AdvancedTextSplitter;
import com.baselet.diagram.draw.DrawHandler;

public class InteractionUse implements LifelineSpanningTickSpanningOccurrence {

	private static final double TEXT_X_PADDING = 5;
	private static final double TEXT_Y_PADDING = 5;
	private static final double HEADER_BOTTOM_PADDING = 4;
	private static final String[] HEADER_TEXT = new String[] { "ref" };

	private final int tick;
	private final String[] textLines;
	private final Lifeline[] coveredLifelines;

	public InteractionUse(int tick, String text, Lifeline[] coveredLifelines) {
		this(tick, text.split("\n"), coveredLifelines);
	}

	public InteractionUse(int tick, String[] textLines, Lifeline[] coveredLifelines) {
		super();
		this.tick = tick;
		this.textLines = textLines;
		this.coveredLifelines = coveredLifelines;
	}

	@Override
	public Lifeline getFirstLifeline() {
		return coveredLifelines[0];
	}

	@Override
	public Lifeline getLastLifeline() {
		return coveredLifelines[coveredLifelines.length - 1];
	}

	@Override
	public void draw(DrawHandler drawHandler, DrawingInfo drawingInfo) {
		double width = drawingInfo.getSymmetricWidth(getFirstLifeline(), getLastLifeline(), tick);
		double height = getHeight(drawHandler, width);
		double topY = drawingInfo.getVerticalStart(tick) + (drawingInfo.getTickHeight(tick) - height) / 2;
		PointDouble topLeft = new PointDouble(
				drawingInfo.getHDrawingInfo(getFirstLifeline()).getSymmetricHorizontalStart(tick), topY);

		drawHandler.drawRectangle(topLeft.x, topLeft.y, width, height);
		PointDouble pentagonSize = PentagonDrawingHelper.draw(drawHandler, HEADER_TEXT, width, topLeft);
		AdvancedTextSplitter.drawText(drawHandler, textLines, topLeft.x + pentagonSize.x + TEXT_X_PADDING,
				topLeft.y, width - (pentagonSize.x + TEXT_X_PADDING) * 2, height,
				AlignHorizontal.CENTER, AlignVertical.CENTER);

		for (Lifeline ll : coveredLifelines) {
			drawingInfo.getDrawingInfo(ll).addInterruptedArea(new Line1D(topLeft.y, topLeft.y + height));
		}
	}

	@Override
	public double getOverallMinWidth(DrawHandler drawHandler, double lifelineHorizontalPadding) {
		return PentagonDrawingHelper.getPentagonMinimumWidth(drawHandler, HEADER_TEXT) * 2
				+ TEXT_X_PADDING * 2
				+ AdvancedTextSplitter.getTextMinWidth(textLines, drawHandler);
	}

	@Override
	public Map<Integer, Double> getEveryAdditionalYHeight(DrawHandler drawHandler,
			HorizontalDrawingInfo hInfo, double defaultTickHeight) {
		Map<Integer, Double> ret = new HashMap<Integer, Double>();
		double width = hInfo.getSymmetricWidth(getFirstLifeline(), getLastLifeline(), tick);
		if (getHeight(drawHandler, width) > defaultTickHeight) {
			ret.put(tick, getHeight(drawHandler, width) - defaultTickHeight);
		}
		return ret;
	}

	@Override
	public ContainerPadding getPaddingInformation() {
		return null;
	}

	private double getHeight(DrawHandler drawHandler, double width) {
		double textWidth = width - TEXT_X_PADDING * 2
							- PentagonDrawingHelper.getPentagonMinimumWidth(drawHandler, HEADER_TEXT) * 2;
		return Math.max(PentagonDrawingHelper.getHeight(drawHandler, HEADER_TEXT, width) + HEADER_BOTTOM_PADDING,
				AdvancedTextSplitter.getSplitStringHeight(textLines, textWidth, drawHandler) + TEXT_Y_PADDING * 2);
	}

}
