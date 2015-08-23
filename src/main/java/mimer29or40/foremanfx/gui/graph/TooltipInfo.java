package mimer29or40.foremanfx.gui.graph;

import javafx.geometry.Point2D;

public class TooltipInfo
{

    public Point2D   screenLocation;
    public Point2D   screenSize;
    public Direction direction;
    public String    text;

    public TooltipInfo(Point2D screenLocation, Point2D screenSize, Direction direction, String text)
    {
        this.screenLocation = screenLocation;
        this.screenSize = screenSize;
        this.direction = direction;
        this.text = text;
    }
}
