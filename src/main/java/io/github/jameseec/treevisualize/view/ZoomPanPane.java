package io.github.jameseec.treevisualize.view;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * A viewport with added zoom (via mouse scroll or touchpad pinch/dragging apart)
 * and panning functionality.
 */
public class ZoomPanPane extends Pane {
    private static final double SCALE_DELTA = 1.08;
    private static final double MAX_ZOOM = 2.0;
    private static final double MIN_ZOOM = 0.5;
    private final Scale scale = new Scale(1, 1, 0, 0); // for zoom transformations
    private final Translate translate = new Translate(0, 0);   // for panning
    private final Node contentPane;
    private final Group group;
    private Point2D lastMousePoint;

    /**
     * Constructs a zoomable and pannable pane.
     * Wraps contentPane in group (for scale + panning) then pane (for consistent viewport sizing).
     */
    public ZoomPanPane(Node contentPane) {
        // Pane with contentPane
        this.contentPane = contentPane;
        group = new Group(contentPane);
        this.getChildren().add(group);
        this.maxHeight(USE_COMPUTED_SIZE);
        this.maxWidth(USE_COMPUTED_SIZE);
        group.getTransforms().addAll(this.translate, this.scale);

        setupClip();
        setupZoom();
        setupPanning();
    }

    /**
     * Applies zoom with given mouse position as pivot.
     * scale between MAX_ZOOM and MIN_ZOOM.
     * @param y y-coord of pivot point in scene coords.
     * @param x x-coord of pivot point in scene coords.
     * @param zoomIn true if zooming in, false if zooming out
     */
    public void handleZoom(double x, double y, boolean zoomIn) {
        double scaleFactor = zoomIn ? SCALE_DELTA : 1 / SCALE_DELTA;
        double newScale = scaleFactor * scale.getX();
        if (newScale < MIN_ZOOM || newScale > MAX_ZOOM) {
            return;
        }

        // Zoom around (0,0), then translate so that the mouse position (sceneX, sceneY)
        // stays fixed on the same local point of the group (prevents jumping effect).
        Point2D pivotLocal = group.sceneToLocal(x, y);
        scale.setX(newScale);
        scale.setY(newScale);
        Point2D pivotAfterScaling = group.localToScene(pivotLocal);

        double dx = x - pivotAfterScaling.getX();
        double dy = y - pivotAfterScaling.getY();
        translate.setX(translate.getX() + dx);
        translate.setY(translate.getY() + dy);
    }

    /**
     * Resets zoom and resets panning.
     */
    public void resetView() {
        resetPanning();
        resetZoom();
    }

    /**
     * Resets zoom, centered on middle of viewport.
     */
    public void resetZoom() {
        // Sets the zoom scale to 1.0, then calculates and applies a
        // translation to maintain the visual center
        double viewportXMiddle = getWidth() / 2;
        double viewportYMiddle = getHeight() / 2;
        Point2D pivotLocal = group.sceneToLocal(viewportXMiddle, viewportYMiddle);
        scale.setX(1);
        scale.setY(1);
        Point2D pivotAfterScaling = group.localToScene(pivotLocal);

        double dx = viewportXMiddle - pivotAfterScaling.getX();
        double dy = viewportYMiddle - pivotAfterScaling.getY();
        translate.setX(translate.getX() + dx);
        translate.setY(translate.getY() + dy);
    }

    /**
     * Resets panning to top middle of contentPane.
     */
    public void resetPanning() {
        double viewportWidth = getWidth();
        double scaledContentWidth = contentPane.getBoundsInLocal().getWidth() * scale.getX();

        translate.setX((viewportWidth - scaledContentWidth) / 2);
        translate.setY(0);
    }

    private void setupPanning() {
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> lastMousePoint = new Point2D(e.getSceneX(), e.getSceneY()));
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handlePanning);
        this.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> lastMousePoint = null);
    }

    /**
     * Clips the ZoomPanPane to its bounds, prevents children from appearing outside.
     */
    private void setupClip() {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
    }

    private void setupZoom() {
        // zoom via pinch/dragging apart with two fingers
        this.addEventFilter(ZoomEvent.ZOOM, e -> {
            handleZoom(e.getSceneX(), e.getSceneY(), e.getZoomFactor() > 1);
            e.consume();
        });

        // zoom via scroll
        this.addEventFilter(ScrollEvent.SCROLL, e -> {
            handleZoom(e.getSceneX(), e.getSceneY(), e.getDeltaY() > 0);
            e.consume();
        });
    }

    private void handlePanning(MouseEvent e) {
        if (lastMousePoint != null) {
            double deltaX = e.getSceneX() - lastMousePoint.getX();
            double deltaY = e.getSceneY() - lastMousePoint.getY();

            translate.setX(translate.getX() + deltaX);
            translate.setY(translate.getY() + deltaY);

            lastMousePoint = new Point2D(e.getSceneX(), e.getSceneY());
        }
    }
}
