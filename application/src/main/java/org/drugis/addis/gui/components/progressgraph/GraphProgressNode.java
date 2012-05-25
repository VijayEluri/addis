package org.drugis.addis.gui.components.progressgraph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingConstants;

import org.drugis.common.gui.task.TaskProgressBar;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;

public class GraphProgressNode extends GraphComponent implements SwingConstants {
	private static final long serialVersionUID = 7151331776919970759L;

	private static final double DEFAULT_ROUNDING_ARCH = 5;

	private String d_labelText;
	private final boolean d_hasProgress;

	private Color d_color = Color.LIGHT_GRAY;
	
	public GraphProgressNode(Dimension gridCellSize, Task task) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR, task);
	}
	
	public GraphProgressNode(Dimension gridCellSize, int lineWidth, Color color, Task task) {
		this(gridCellSize, lineWidth, color, task, true);
	}
	
	
	public GraphProgressNode(Dimension gridCellSize, Task task, boolean hasProgress) {
		this(gridCellSize, DEFAULT_LINE_WIDTH, DEFAULT_COLOR, task, hasProgress);
	}
	
	
	public GraphProgressNode(Dimension gridCellSize, int lineWidth, Color color, Task task, boolean hasProgress) {
		super(gridCellSize, lineWidth, color);
		d_labelText = task.toString();
		d_hasProgress = hasProgress;
		if(hasProgress) { 
			TaskProgressBar tpb = new TaskProgressBar(new TaskProgressModel(task));			
			setLayout(new BorderLayout(0, 0));
			add(tpb, BorderLayout.NORTH);
			tpb.setVisible(true);
			revalidate();
		}
		task.addTaskListener(new TaskListener() {
			
			@Override
			public void taskEvent(TaskEvent event) {
				if(event.getType() == EventType.TASK_FINISHED) {
					d_color = Color.decode("#D7FF96");
				} else if(event.getType() == EventType.TASK_STARTED) {
					d_color = Color.decode("#7DA6FF");
				} else if(event.getType() == EventType.TASK_FAILED || event.getType() == EventType.TASK_ABORTED) {
					d_color = Color.decode("#C4000D");
				} else if(event.getType() == EventType.TASK_RESTARTED) {
					d_color = Color.LIGHT_GRAY;
				}
				GraphProgressNode.this.repaint();
			}
		});
	}


	@Override
	protected void paintComponent(Graphics g) {
		if(!d_hasProgress) {
			Graphics2D g2 = (Graphics2D)g;  
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Dimension s = getPreferredSize();
			g2.setStroke(new BasicStroke((float)d_lineWidth));
			RoundRectangle2D.Double s2 = new RoundRectangle2D.Double(d_lineWidth / 2.0, d_lineWidth / 2.0, s.getWidth() - d_lineWidth, s.getHeight() - d_lineWidth, DEFAULT_ROUNDING_ARCH, DEFAULT_ROUNDING_ARCH);
			g2.setPaint(d_color);
			g2.fill(s2);
			g2.setPaint(DEFAULT_COLOR);
			g2.draw(s2);

			Rectangle2D textBounds = g2.getFontMetrics().getStringBounds(d_labelText, g);
			g2.drawString(d_labelText, (float)(s.width / 2 - textBounds.getCenterX()), (float)(s.height / 2 - textBounds.getCenterY()));
		}
	}
}

