package com.yiistorm.elements;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 03.04.13
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */
public class VerticalMenuBar extends JMenuBar {
    private static final LayoutManager grid = new GridLayout(0, 1);

    public VerticalMenuBar() {
        setLayout(grid);
    }
}